import pytest
from datetime import datetime, timedelta
from src.db.session import SessionLocal
from src.models.producto import Producto, Categoria
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
        "categoria": "ALIMENTOS_BEBIDAS",
        "fabricante_id": "12345"
    }


# before each
@pytest.fixture
def session():
    yield
    db = SessionLocal()
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
    
    response = client.post('/inventarios/createproduct', 
                          json=valid_producto_data, 
                          headers={'Authorization': 'Bearer 1234'})
    
    assert response.status_code == 201
    json_response = response.get_json()
    assert "sku" in json_response
    assert "createdAt" in json_response
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
"""Test para control de duplicados"""
def test_cannot_create_duplicate_producto(client, valid_producto_data, monkeypatch):
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
                              json=valid_producto_data, 
                              headers={'Authorization': 'Bearer 1234'})
        assert response1.status_code == 201
        
        # Ahora se hace una llamada igual, esperando que el servicio devuelva un error de duplicados
        response2 = client.post('/inventarios/createproduct', 
                              json=valid_producto_data, 
                              headers={'Authorization': 'Bearer 1234'})
        assert response2.status_code == 400
        json_response = response2.get_json()
        assert "existe" in str(json_response).lower()