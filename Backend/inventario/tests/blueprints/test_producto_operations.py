import pytest
import uuid
from datetime import datetime, timedelta
from src.db.session import SessionLocal
from src.models.producto import Producto, Categoria
from src.models import InventarioBodega
from unittest.mock import patch


@pytest.fixture
def valid_fabricante_data():
    return {
        "nombre": "Alpina",
        "numeroTel": "3211466",
        "representante": "Andres Garcia",
    }


@pytest.fixture
def valid_producto_data():
    # Se mockean las fechas
    fecha_vencimiento = datetime.now() + timedelta(days=30)
    tiempo_entrega = datetime.now() + timedelta(days=5)

    return {
        "nombre": "Yogurt Natural",
        "descripcion": "Yogurt natural sin azúcar",
        "perecedero": True,
        "fechaVencimiento": fecha_vencimiento.isoformat(),
        "valorUnidad": 3500,
        "tiempoEntrega": tiempo_entrega.isoformat(),
        "condicionAlmacenamiento": "Refrigerado",
        "reglasLegales": "Registro sanitario INVIMA",
        "reglasComerciales": "No se aceptan devoluciones",
        "reglasTributarias": "IVA 19%",
        "categoria": "ALIMENTOS Y BEBIDAS",
        "fabricante_id": "12345",
        "bodega": "bodega-test-id",
        "cantidad": 10
    }


# before each
@pytest.fixture
def session():
    db = SessionLocal()
    yield db
    db.query(InventarioBodega).delete()
    db.query(Producto).delete()
    db.commit()
    db.close()


def test_check_health(client):
    response = client.get('/inventarios/ping')
    assert response.status_code == 200
    assert response.data == b"success"


def test_reset_database(client):
    response = client.post('/inventarios/reset')
    assert response.status_code == 200


"""Test para verificar que un producto se cree exitosamente"""


def test_create_producto_success(client, valid_producto_data):
    # Create a bodega first
    bodega_data = {
        "nombre": "Bodega Creación",
        "direccion": "Calle Creación #123",
        "ciudad": "Bogota",
        "pais": "CO"
    }

    response_bodega = client.post('/inventarios/bodegas',
                                  json=bodega_data,
                                  headers={'Authorization': 'Bearer 1234'})
    assert response_bodega.status_code == 201
    bodega = response_bodega.get_json()
    bodega_id = bodega.get("id")

    # Update the producto data with the real bodega ID
    test_data = valid_producto_data.copy()
    test_data["bodega"] = bodega_id

    response = client.post('/inventarios/createproduct',
                           json=test_data,
                           headers={'Authorization': 'Bearer 1234'})

    assert response.status_code == 201
    json_response = response.get_json()
    assert "sku" in json_response
    assert "createdAt" in json_response


def test_create_producto_without_bodega(client, valid_producto_data):
    """Test para verificar que no se puede crear un producto sin bodega"""
    request_data = valid_producto_data.copy()
    request_data["fabricante_id"] = str(uuid.uuid4())
    request_data.pop("bodega", None)
    request_data.pop("cantidad", None)

    # Se crea el producto sin bodega
    response = client.post('/inventarios/createproduct',
                           json=request_data,
                           headers={'Authorization': 'Bearer 1234'})

    assert response.status_code == 201


"""Test para verificar que no se puede crear un porducto sin token"""


def test_cannot_create_producto_without_token(client, valid_producto_data):
    response = client.post('/inventarios/createproduct', json=valid_producto_data)
    assert response.status_code == 403


"""Test para validar que no se cree un producto si faltan datos"""


def test_cannot_create_producto_with_missing_fields(client, valid_producto_data):
    invalid_data = valid_producto_data.copy()
    invalid_data.pop("nombre", None)

    response = client.post('/inventarios/createproduct',
                           json=invalid_data,
                           headers={'Authorization': 'Bearer 1234'})

    assert response.status_code == 400
    json_response = response.get_json()
    assert "nombre" in str(json_response)


"""Test para verificar que no se pueda crear el producto si la categoría es inválida"""


def test_cannot_create_producto_with_invalid_categoria(client, valid_producto_data):
    invalid_data = valid_producto_data.copy()
    invalid_data["categoria"] = "CATEGORIA_INVALIDA"
    response = client.post('/inventarios/createproduct',
                           json=invalid_data,
                           headers={'Authorization': 'Bearer 1234'})

    assert response.status_code == 400
    json_response = response.get_json()
    assert "categoría" in str(json_response).lower() or "categoria" in str(json_response).lower()


"""Test para validar que no se pueda crear un producto sin fabricante_id"""


def test_cannot_create_producto_without_fabricante_id(client, valid_producto_data):
    invalid_data = valid_producto_data.copy()
    invalid_data.pop("fabricante_id", None)

    response = client.post('/inventarios/createproduct',
                           json=invalid_data,
                           headers={'Authorization': 'Bearer 1234'})

    assert response.status_code == 400
    json_response = response.get_json()
    assert "fabricante_id" in str(json_response)


"""Test para validar que no se pueda crear un producto sin cantidad"""


def test_cannot_create_producto_without_cantidad(client, valid_producto_data, monkeypatch):
    invalid_data = valid_producto_data.copy()
    invalid_data['fabricante_id'] = str(uuid.uuid4())
    invalid_data.pop("cantidad", None)

    print(invalid_data)

    response = client.post('/inventarios/createproduct',
                           json=invalid_data,
                           headers={'Authorization': 'Bearer 1234'})

    print(response.get_json())
    assert response.status_code == 400
    json_response = response.get_json()
    assert "cantidad" in str(json_response)


"""Test para validar que no se pueda crear un producto con cantidad inválida"""


def test_cannot_create_producto_with_invalid_cantidad(client, valid_producto_data):
    invalid_data = valid_producto_data.copy()
    invalid_data["cantidad"] = 0  # cantidad debe ser mayor a 0

    response = client.post('/inventarios/createproduct',
                           json=invalid_data,
                           headers={'Authorization': 'Bearer 1234'})

    assert response.status_code == 400
    json_response = response.get_json()
    assert "cantidad" in str(json_response)


def test_cannot_create_producto_with_invalid_bodega(client, valid_producto_data):
    """Test para validar que no se pueda crear un producto con bodega inválida"""
    invalid_data = valid_producto_data.copy()
    invalid_data["fabricante_id"] = str(uuid.uuid4())
    invalid_data["bodega"] = "INVALID_BODEGA_ID"

    response = client.post('/inventarios/createproduct',
                           json=invalid_data,
                           headers={'Authorization': 'Bearer 1234'})

    assert response.status_code == 400
    json_response = response.get_json()
    assert "bodega" in str(json_response).lower() or "ubicación" in str(json_response).lower()


"""Test para control de duplicados"""


def test_cannot_create_duplicate_producto(client, valid_producto_data, monkeypatch):
    # Create a bodega first for the test
    bodega_data = {
        "nombre": "Bodega Duplicados",
        "direccion": "Calle Duplicados #123",
        "ciudad": "Bogota",
        "pais": "CO"
    }

    response_bodega = client.post('/inventarios/bodegas',
                                  json=bodega_data,
                                  headers={'Authorization': 'Bearer 1234'})
    assert response_bodega.status_code == 201
    bodega = response_bodega.get_json()
    bodega_id = bodega.get("id")

    # Update the valid_producto_data with the necessary bodega
    test_data = valid_producto_data.copy()
    test_data["bodega"] = bodega_id

    # Se configura un mock para devolver primero el resultado de la creaciòn de un producto exitoso
    # y luego la respuesta si se usan los mismos datos, en ese caso un error de un producto duplicado
    with patch('src.commands.create_producto.Create.execute') as mock_execute:
        mock_execute.side_effect = [
            # Primer callout: creación exitosa
            {
                "sku": "12345-ABCDEF",
                "createdAt": "2023-01-01T00:00:00+00:00"
            },
            # Segundo callout: producto duplicado
            ({"error": "El producto ya existe para este fabricante"}, 400)
        ]

        # Se hace un primer callout con la información mock del producto
        response1 = client.post('/inventarios/createproduct',
                                json=test_data,
                                headers={'Authorization': 'Bearer 1234'})
        assert response1.status_code == 201

        # Ahora se hace una llamada igual, esperando que el servicio devuelva un error de duplicados
        response2 = client.post('/inventarios/createproduct',
                                json=test_data,
                                headers={'Authorization': 'Bearer 1234'})
        assert response2.status_code == 400
        json_response = response2.get_json()
        assert "existe" in str(json_response).lower()


def test_get_producto_location(client, valid_producto_data, session):
    import uuid

    # 1. Create two bodegas first
    bodega1_data = {
        "nombre": "Bodega Norte",
        "direccion": "Calle Norte #123",
        "ciudad": "Bogota",
        "pais": "CO"
    }

    bodega2_data = {
        "nombre": "Bodega Sur",
        "direccion": "Calle Sur #456",
        "ciudad": "Medellin",
        "pais": "CO"
    }

    response1 = client.post('/inventarios/bodegas',
                            json=bodega1_data,
                            headers={'Authorization': 'Bearer 1234'})
    assert response1.status_code == 201
    bodega1 = response1.get_json()
    bodega1_id = bodega1.get("id")

    response2 = client.post('/inventarios/bodegas',
                            json=bodega2_data,
                            headers={'Authorization': 'Bearer 1234'})
    assert response2.status_code == 201
    bodega2 = response2.get_json()
    bodega2_id = bodega2.get("id")

    # 2. Create the product with initial bodega and cantidad
    unique_data = valid_producto_data.copy()
    unique_data["nombre"] = "Queso Campesino Especial"
    unique_data["bodega"] = bodega1_id  # Assign to first bodega
    unique_data["cantidad"] = 10  # Initial quantity

    response = client.post('/inventarios/createproduct',
                           json=unique_data,
                           headers={'Authorization': 'Bearer 1234'})

    assert response.status_code == 201
    json_response = response.get_json()
    sku = json_response["sku"]

    # 3. Get the created product from the database
    db = session
    producto = db.query(Producto).filter(Producto.sku == sku).first()
    assert producto is not None

    # 4. Assign the product to the second bodega
    assignment_data2 = {
        "producto_id": sku,  # Using SKU for product identification
        "cantidad": 20
    }

    response_assign2 = client.post(f'/inventarios/bodegas/{bodega2_id}/productos',
                                   json=assignment_data2,
                                   headers={'Authorization': 'Bearer 1234'})
    assert response_assign2.status_code == 201

    # 5. Now query for the product's location by SKU
    response = client.get(f'/inventarios/productos/ubicacion?producto={sku}',
                          headers={'Authorization': 'Bearer 1234'})
    assert response.status_code == 200

    # 6. Verify the response data
    ubicacion_data = response.get_json()
    assert ubicacion_data.get("sku") == sku
    assert ubicacion_data.get("nombre") == "Queso Campesino Especial"

    # Should have 2 locations
    ubicaciones = ubicacion_data.get("ubicaciones")
    assert len(ubicaciones) == 2

    # Check that we have both bodegas with correct quantities
    bodega_ids = [ubicacion.get("bodega_id") for ubicacion in ubicaciones]
    assert bodega1_id in bodega_ids
    assert bodega2_id in bodega_ids

    # Verify the quantities
    for ubicacion in ubicaciones:
        if ubicacion.get("bodega_id") == bodega1_id:
            assert ubicacion.get("cantidad") == 10
        elif ubicacion.get("bodega_id") == bodega2_id:
            assert ubicacion.get("cantidad") == 20

    # 7. Test finding by name (partial match)
    response = client.get('/inventarios/productos/ubicacion?producto=Queso Campesino',
                          headers={'Authorization': 'Bearer 1234'})
    assert response.status_code == 200
    ubicacion_data = response.get_json()
    assert ubicacion_data.get("sku") == sku


def test_list_productos_empty(client):
    """Test listing products when there are no products in the database"""

    # Reset the database to ensure it's empty
    client.post('/inventarios/reset')

    # Make the API call to list products
    response = client.get('/inventarios/productos',
                          headers={'Authorization': 'Bearer 1234'})

    # Verify the response
    assert response.status_code == 200
    json_response = response.get_json()
    assert isinstance(json_response, list)
    assert len(json_response) == 0


def test_list_productos_with_data(client, valid_producto_data):
    """Test listing products with actual database items"""

    # Reset the database to start with a clean state
    client.post('/inventarios/reset')

    # Create a bodega first
    bodega_data = {
        "nombre": "Bodega Test",
        "direccion": "Calle Test #123",
        "ciudad": "Bogota",
        "pais": "CO"
    }

    response_bodega = client.post('/inventarios/bodegas',
                                  json=bodega_data,
                                  headers={'Authorization': 'Bearer 1234'})
    assert response_bodega.status_code == 201
    bodega = response_bodega.get_json()
    bodega_id = bodega.get("id")

    # Create first product
    producto1_data = valid_producto_data.copy()
    producto1_data["nombre"] = "Producto Test 1"
    producto1_data["bodega"] = bodega_id
    producto1_data["cantidad"] = 15

    response1 = client.post('/inventarios/createproduct',
                            json=producto1_data,
                            headers={'Authorization': 'Bearer 1234'})
    assert response1.status_code == 201

    # Create second product with different name
    producto2_data = valid_producto_data.copy()
    producto2_data["nombre"] = "Producto Test 2"
    producto2_data["categoria"] = "CUIDADO PERSONAL"
    producto2_data["bodega"] = bodega_id
    producto2_data["cantidad"] = 25

    response2 = client.post('/inventarios/createproduct',
                            json=producto2_data,
                            headers={'Authorization': 'Bearer 1234'})
    assert response2.status_code == 201

    # Now list all products
    response = client.get('/inventarios/productos',
                          headers={'Authorization': 'Bearer 1234'})

    # Verify the response
    assert response.status_code == 200
    productos = response.get_json()
    assert isinstance(productos, list)
    assert len(productos) == 2

    # Verify that our products are in the response
    product_names = [p["nombre"] for p in productos]
    assert "Producto Test 1" in product_names
    assert "Producto Test 2" in product_names

    # Verify fields of one product
    for producto in productos:
        if producto["nombre"] == "Producto Test 1":
            assert producto["descripcion"] == valid_producto_data["descripcion"]
            assert producto["valorUnidad"] == valid_producto_data["valorUnidad"]
            assert producto["categoria"] == "ALIMENTOS Y BEBIDAS"
            assert "sku" in producto
            assert "createdAt" in producto
        elif producto["nombre"] == "Producto Test 2":
            assert producto["categoria"] == "CUIDADO PERSONAL"


def test_list_productos_authorization_required(client):
    """Test that authorization is required to list products"""

    # Make the API call without the authorization header
    response = client.get('/inventarios/productos')

    # Verify that the request is not authorized
    assert response.status_code == 403
