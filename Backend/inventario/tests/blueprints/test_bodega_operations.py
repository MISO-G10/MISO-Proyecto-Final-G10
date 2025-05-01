import uuid
from datetime import datetime, timedelta

import pytest

from src.db.session import SessionLocal
from src.models import Bodega, InventarioBodega, Producto, Categoria


@pytest.fixture
def valid_bodega_data():
    return {
        "nombre": "Bodega Principal",
        "direccion": "Calle 123 #45-67, Bogotá"
    }


# before each
@pytest.fixture
def session():
    db = SessionLocal()
    yield db
    db.query(InventarioBodega).delete()
    db.query(Producto).delete()
    db.query(Bodega).delete()
    db.commit()
    db.close()


def test_check_health(client):
    response = client.get('/inventarios/ping')
    assert response.status_code == 200
    assert response.data == b"success"


def test_create_bodega_success(client, valid_bodega_data):
    response = client.post('/inventarios/bodegas',
                           json=valid_bodega_data,
                           headers={'Authorization': 'Bearer 1234'})

    assert response.status_code == 201
    json_response = response.get_json()
    assert json_response.get("id") is not None
    assert json_response.get("nombre") == valid_bodega_data["nombre"]
    assert json_response.get("direccion") == valid_bodega_data["direccion"]


def test_cannot_create_bodega_without_token(client, valid_bodega_data):
    response = client.post('/inventarios/bodegas', json=valid_bodega_data)
    assert response.status_code == 403


def test_cannot_create_bodega_with_missing_fields(client, valid_bodega_data):
    # Test missing nombre
    invalid_data = valid_bodega_data.copy()
    invalid_data.pop("nombre", None)

    response = client.post('/inventarios/bodegas',
                           json=invalid_data,
                           headers={'Authorization': 'Bearer 1234'})

    assert response.status_code == 400
    json_response = response.get_json()
    assert "nombre" in str(json_response)

    # Test missing direccion
    invalid_data = valid_bodega_data.copy()
    invalid_data.pop("direccion", None)

    response = client.post('/inventarios/bodegas',
                           json=invalid_data,
                           headers={'Authorization': 'Bearer 1234'})

    assert response.status_code == 400
    json_response = response.get_json()
    assert "direccion" in str(json_response)


def test_cannot_create_bodega_with_empty_request(client):
    response = client.post('/inventarios/bodegas',
                           json={},
                           headers={'Authorization': 'Bearer 1234'})

    assert response.status_code == 400


def test_assign_producto_to_bodega(client, session):
    # Create a bodega
    bodega_data = {
        "nombre": "Bodega Asignación",
        "direccion": "Calle Principal #123"
    }

    response = client.post('/inventarios/bodegas',
                           json=bodega_data,
                           headers={'Authorization': 'Bearer 1234'})

    assert response.status_code == 201
    bodega = response.get_json()
    bodega_id = bodega.get("id")

    # Create a producto
    db = session
    fecha_vencimiento = datetime.now() + timedelta(days=30)
    tiempo_entrega = datetime.now() + timedelta(days=5)

    # Create a product with a known SKU
    producto = Producto(
        sku="ASSIGN-123",
        nombre="Producto para Asignar",
        descripcion="Producto para prueba de asignación",
        perecedero=True,
        fechaVencimiento=fecha_vencimiento,
        valorUnidad=5000,
        tiempoEntrega=tiempo_entrega,
        condicionAlmacenamiento="Ambiente",
        reglasLegales="N/A",
        reglasComerciales="N/A",
        reglasTributarias="IVA 19%",
        categoria=Categoria.ALIMENTOS_BEBIDAS,
        fabricante_id="1"
    )
    db.add(producto)
    db.commit()

    # Assign producto to bodega through API
    assignment_data = {
        "producto_id": producto.sku,
        "cantidad": 100
    }

    response = client.post(f'/inventarios/bodegas/{bodega_id}/productos',
                           json=assignment_data,
                           headers={'Authorization': 'Bearer 1234'})

    assert response.status_code == 201
    result = response.get_json()

    # Verify the response
    assert result.get("bodega_id") == bodega_id
    assert len(result.get("productos")) == 1
    assert result.get("productos")[0].get("cantidad") == 100

    # Verify in database
    db = session
    inventory = db.query(InventarioBodega).filter(
        InventarioBodega.bodega_id == uuid.UUID(bodega_id),
        InventarioBodega.producto_id == producto.id
    ).first()

    assert inventory is not None
    assert inventory.cantidad == 100


def test_update_producto_cantidad_in_bodega(client, session):
    # Create a bodega and assign a product first
    bodega_data = {
        "nombre": "Bodega Actualización",
        "direccion": "Av. Actualización #456"
    }

    response = client.post('/inventarios/bodegas',
                           json=bodega_data,
                           headers={'Authorization': 'Bearer 1234'})

    assert response.status_code == 201
    bodega = response.get_json()
    bodega_id = bodega.get("id")

    # Create a producto
    db = session
    fecha_vencimiento = datetime.now() + timedelta(days=30)
    tiempo_entrega = datetime.now() + timedelta(days=5)

    # Create a product with a known SKU
    producto = Producto(
        sku="UPDATE-456",
        nombre="Producto para Actualizar",
        descripcion="Producto para prueba de actualización",
        perecedero=True,
        fechaVencimiento=fecha_vencimiento,
        valorUnidad=5000,
        tiempoEntrega=tiempo_entrega,
        condicionAlmacenamiento="Ambiente",
        reglasLegales="N/A",
        reglasComerciales="N/A",
        reglasTributarias="IVA 19%",
        categoria=Categoria.ALIMENTOS_BEBIDAS,
        fabricante_id="1"
    )
    db.add(producto)
    db.commit()

    # First assignment
    assignment_data = {
        "producto_id": producto.sku,
        "cantidad": 50
    }

    response = client.post(f'/inventarios/bodegas/{bodega_id}/productos',
                           json=assignment_data,
                           headers={'Authorization': 'Bearer 1234'})

    assert response.status_code == 201

    # Update the cantidad
    update_data = {
        "producto_id": producto.sku,
        "cantidad": 75
    }

    response = client.post(f'/inventarios/bodegas/{bodega_id}/productos',
                           json=update_data,
                           headers={'Authorization': 'Bearer 1234'})

    assert response.status_code == 201
    result = response.get_json()

    # Verify the response
    assert result.get("bodega_id") == bodega_id
    assert len(result.get("productos")) == 1
    assert result.get("productos")[0].get("cantidad") == 75

    # Verify in database
    db = session
    inventory = db.query(InventarioBodega).filter(
        InventarioBodega.bodega_id == uuid.UUID(bodega_id),
        InventarioBodega.producto_id == producto.id
    ).first()

    assert inventory is not None
    assert inventory.cantidad == 75
