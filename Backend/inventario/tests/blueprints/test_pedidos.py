import uuid
import pytest
from datetime import datetime, timedelta

from src.models.producto import Producto, Categoria
from src.db.session import SessionLocal

@pytest.fixture
def session():
    db = SessionLocal()
    yield db
    db.query(Producto).delete()
    db.commit()
    db.close()

@pytest.fixture
def producto_valido(session):
    db = session
    producto = Producto(
        sku="PROD-001",
        nombre="Producto de prueba",
        descripcion="Producto de prueba para pedidos",
        perecedero=False,
        valorUnidad=10000,
        tiempoEntrega=datetime.now() + timedelta(days=5),
        condicionAlmacenamiento="Normal",
        reglasLegales="N/A",
        reglasComerciales="N/A",
        reglasTributarias="IVA 19%",
        categoria=Categoria.OTROS,
        fabricante_id="1"
    )
    db.add(producto)
    db.commit()
    return producto

def test_create_pedido_success(client, producto_valido):
    pedido_data = {
        "productos": [
            {
                "producto_id": str(producto_valido.id),
                "cantidad": 2
            }
        ]
    }

    response = client.post('/pedidos',
                           json=pedido_data,
                           headers={'Authorization': 'Bearer 1234'})

    assert response.status_code == 201
    json_response = response.get_json()

    assert "id" in json_response
    assert json_response["estado"] == "PENDIENTE"
    assert json_response["valorTotal"] == 20000
    assert len(json_response["productos"]) == 1
    assert json_response["productos"][0]["producto_id"] == str(producto_valido.id)
    assert json_response["productos"][0]["cantidad"] == 2

def test_create_pedido_without_token(client, producto_valido):
    pedido_data = {
        "productos": [
            {
                "producto_id": str(producto_valido.id),
                "cantidad": 2
            }
        ]
    }

    response = client.post('/pedidos', json=pedido_data)
    assert response.status_code == 403

def test_create_pedido_with_empty_request(client):
    response = client.post('/pedidos',
                           json={},
                           headers={'Authorization': 'Bearer 1234'})
    assert response.status_code == 400
    assert "error" in response.get_json()

def test_create_pedido_with_invalid_producto_id(client):
    pedido_data = {
        "productos": [
            {
                "producto_id": str(uuid.uuid4()),  # ID inexistente
                "cantidad": 2
            }
        ]
    }

    response = client.post('/pedidos',
                           json=pedido_data,
                           headers={'Authorization': 'Bearer 1234'})
    assert response.status_code == 404
    json_response = response.get_json()
    assert "error" in json_response

def test_create_pedido_without_productos(client):
    pedido_data = {
        # Sin la lista de productos
    }

    response = client.post('/pedidos',
                           json=pedido_data,
                           headers={'Authorization': 'Bearer 1234'})
    assert response.status_code == 400
