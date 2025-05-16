import pytest
from datetime import datetime, timedelta
from src.models.producto import Categoria

@pytest.fixture
def valid_bulk_productos():
    fecha_vencimiento = datetime.now() + timedelta(days=30)
    tiempo_entrega = datetime.now() + timedelta(days=5)
    
    return {
        "productos": [
            {
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
            },
            {
                "nombre": "Detergente en Polvo",
                "descripcion": "Detergente multiusos",
                "perecedero": False,
                "fechaVencimiento": None,
                "valorUnidad": 12000,
                "tiempoEntrega": tiempo_entrega.isoformat(),
                "condicionAlmacenamiento": "Lugar seco",
                "reglasLegales": "Registro EPA",
                "reglasComerciales": "Descuento por volumen",
                "reglasTributarias": "IVA 19%",
                "categoria": "LIMPIEZA_HOGAR",
                "fabricante_id": "12345"
            }
        ]
    }

"""Test para verificar que se creen productos masivamente"""
def test_bulk_create_success(client, valid_bulk_productos):
    response = client.post('/inventarios/productos/bulk',
                          json=valid_bulk_productos,
                          headers={'Authorization': 'Bearer 1234'})
    
    assert response.status_code == 201
    json_response = response.get_json()
    assert "products" in json_response
    assert len(json_response["products"]) == 2
    assert all("sku" in product for product in json_response["products"])

"""Test para verificar que no se pueden crear productos sin token"""
def test_cannot_bulk_create_without_token(client, valid_bulk_productos):
    response = client.post('/inventarios/productos/bulk', json=valid_bulk_productos)
    assert response.status_code == 403

"""Test para validar errores de validación en productos"""
def test_bulk_create_validation_errors(client, valid_bulk_productos):
    valid_bulk_productos["productos"][0]["categoria"] = "CATEGORIA_INVALIDA"
    
    response = client.post('/inventarios/productos/bulk',
                          json=valid_bulk_productos,
                          headers={'Authorization': 'Bearer 1234'})
    
    assert response.status_code == 400
    json_response = response.get_json()
    assert "error" in json_response
    assert "details" in json_response
    assert len(json_response["details"]) == 1
    assert "categoria" in str(json_response["details"][0]["error"]).lower()

"""Test para validar que productos perecederos requieren fecha de vencimiento"""
def test_bulk_create_perishable_requires_expiry(client, valid_bulk_productos):
    valid_bulk_productos["productos"][0]["fechaVencimiento"] = None
    
    response = client.post('/inventarios/productos/bulk',
                          json=valid_bulk_productos,
                          headers={'Authorization': 'Bearer 1234'})
    
    assert response.status_code == 400
    json_response = response.get_json()
    assert "error" in json_response
    assert "details" in json_response
    assert "fechaVencimiento" in str(json_response["details"][0]["error"])

"""Test para validar que productos no perecederos pueden omitir fecha de vencimiento"""
def test_bulk_create_non_perishable_without_expiry(client, valid_bulk_productos):
    valid_bulk_productos["productos"] = [valid_bulk_productos["productos"][1]]  # Solo usar el producto no perecedero
    
    response = client.post('/inventarios/productos/bulk',
                          json=valid_bulk_productos,
                          headers={'Authorization': 'Bearer 1234'})
    
    assert response.status_code == 201
    json_response = response.get_json()
    assert len(json_response["products"]) == 1

"""Test para validar el manejo de lote vacío"""
def test_bulk_create_empty_batch(client):
    response = client.post('/inventarios/productos/bulk',
                          json={"productos": []},
                          headers={'Authorization': 'Bearer 1234'})
    
    assert response.status_code == 400
    json_response = response.get_json()
    assert "error" in json_response
    assert "vacío" in str(json_response["error"]).lower()

"""Test para validar el límite de productos por lote"""
def test_bulk_create_batch_limit(client, valid_bulk_productos):
    producto_base = valid_bulk_productos["productos"][0]
    productos = []
    for i in range(101):
        nuevo_producto = producto_base.copy()
        nuevo_producto["nombre"] = f"Producto {i}"
        productos.append(nuevo_producto)
    
    response = client.post('/inventarios/productos/bulk',
                          json={"productos": productos},
                          headers={'Authorization': 'Bearer 1234'})
    
    assert response.status_code == 400
    json_response = response.get_json()
    assert "error" in json_response
    assert "límite" in str(json_response["error"]).lower()
