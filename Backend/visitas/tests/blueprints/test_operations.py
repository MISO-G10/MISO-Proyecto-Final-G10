import pytest

from src.db.session import SessionLocal
from src.models.visita import Visita


@pytest.fixture
def valid_visita_data():
    return {
        "fecha": "2025-04-12",
        "horaDesde": "10:00",
        "horaHasta": "11:00",
        "comentarios": "Visita de pruebas",
        "idUsuario": "12345678-1234-1234-1234-123456789012"
    }


# before each
@pytest.fixture
def session():
    yield
    db = SessionLocal()
    db.query(Visita).delete()
    db.commit()
    db.close()


def test_check_health(client):
    response = client.get('/visitas/ping')
    assert response.status_code == 200
    assert response.data == b"success"


def test_reset_database(client):
    response = client.post('/visitas/reset')
    assert response.status_code == 200

'''
def test_create_visita_success(client, valid_visita_data):
    response = client.post('/visitas', json=valid_visita_data, headers={'Authorization': 'Bearer 1234'})
    assert response.status_code == 201'''
    


def test_cannot_create_visita_without_token(client, valid_visita_data):
    response = client.post('/visitas', json=valid_visita_data)

    assert response.status_code == 403


def test_cannot_create_visita_with_invalid_token(client, valid_visita_data):
    response = client.post('/visitas', json=valid_visita_data, headers={'Authorization ': 'Bearer 1234'})

    assert response.status_code == 403

'''
def test_cannot_create_visita_with_missing_fields(client, valid_visita_data):
    
    invalid_data = valid_visita_data.copy()
    invalid_data.pop("nombre", None)
    
    response = client.post('/visitas', json=invalid_data, headers={'Authorization': 'Bearer 1234'})

    json_response = response.get_json()

    assert response.status_code == 400
    assert "nombre" in json_response["msg"]
'''
'''
def test_list_visitas(client, valid_visita_data):
   
    client.post('/visitas', json=valid_visita_data, headers={"Authorization": "Bearer 1234"})

    response = client.get('/visitas', headers={'Authorization': 'Bearer 1234'})

    assert response.status_code == 200
    assert len(response.get_json()) == 1
''' 
# Visita filtrada por fecha
'''def test_list_visita_by_date(client, valid_visita_data):
    
    new_visita = valid_visita_data.copy()
    
    client.post('/visitas', json=new_visita, headers={'Authorization': 'Bearer 1234'})
    
    #print(new_visita["fecha"])
    response = client.get('/visitas?fecha=' + new_visita["fecha"], headers={'Authorization': 'Bearer 1234'})

    assert response.status_code == 200
    assert len(response.get_json()) == 1'''