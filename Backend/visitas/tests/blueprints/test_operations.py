import pytest
from faker import Faker
from src.db.session import SessionLocal
from src.models.visita import Visita


@pytest.fixture
def valid_visita_data(faker: Faker):
    return {
        "fecha": faker.date(),
        "horaDesde": faker.time(pattern="%H:%M"),
        "horaHasta": faker.time(pattern="%H:%M"),
        "comentarios": faker.text(),
        "idUsuario": faker.uuid4()
    }

@pytest.fixture(autouse=True)
def clean_visitas():
    """Limpia la tabla de visitas antes de cada test."""
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


def test_create_visita_success(client, valid_visita_data):
    response = client.post('/visitas', json=valid_visita_data, headers={'Authorization': 'Bearer 1234'})
    assert response.status_code == 201
    


def test_cannot_create_visita_without_token(client, valid_visita_data):
    response = client.post('/visitas', json=valid_visita_data)

    assert response.status_code == 403


def test_cannot_create_visita_with_invalid_token(client, valid_visita_data):
    response = client.post('/visitas', json=valid_visita_data, headers={'Authorization ': 'Bearer 1234'})

    assert response.status_code == 403


def test_cannot_create_visita_with_missing_fields(client, valid_visita_data):
    
    invalid_data = valid_visita_data.copy()
    invalid_data.pop("idUsuario", None)
    
    response = client.post('/visitas', json=invalid_data, headers={'Authorization': 'Bearer 1234'})

    json_response = response.get_json()

    assert response.status_code == 400
    assert "idUsuario" in json_response["msg"]
    

def test_list_visitas(client, valid_visita_data):
   
    client.post('/visitas', json=valid_visita_data, headers={"Authorization": "Bearer 1234"})

    response = client.get('/visitas', headers={'Authorization': 'Bearer 1234'})

    assert response.status_code == 200
    assert len(response.get_json()) == 1

# Visita filtrada por fecha
def test_list_visita_by_date(client, valid_visita_data):
    
    new_visita = valid_visita_data.copy()
    
    client.post('/visitas', json=new_visita, headers={'Authorization': 'Bearer 1234'})
    
    #print(new_visita["fecha"])
    response = client.get('/visitas?fecha=' + new_visita["fecha"], headers={'Authorization': 'Bearer 1234'})

    assert response.status_code == 200
    assert len(response.get_json()) == 1


# Tests para obtener una visita específica
'''
def test_get_visita_success(client, valid_visita_data):
    # Primero creamos una visita
    response = client.post('/visitas', json=valid_visita_data, headers={'Authorization': 'Bearer 1234'})
    assert response.status_code == 201
    
    # Obtenemos el ID de la visita creada
    visita_id = response.get_json()['id']
    
    # Consultamos la visita por su ID
    response = client.get(f'/visitas/{visita_id}', headers={'Authorization': 'Bearer 1234'})
    
    assert response.status_code == 200
    json_response = response.get_json()
    assert json_response['id'] == visita_id
    assert json_response['fecha'] == valid_visita_data['fecha']
    assert json_response['horaDesde'] == valid_visita_data['horaDesde']
    assert json_response['horaHasta'] == valid_visita_data['horaHasta']
    assert json_response['comentarios'] == valid_visita_data['comentarios']
'''
'''
def test_get_visita_not_found(client):
    # Intentamos obtener una visita con un ID que no existe
    non_existent_id = "00000000-0000-0000-0000-000000000000"
    response = client.get(f'/visitas/{non_existent_id}', headers={'Authorization': 'Bearer 1234'})
    
    assert response.status_code == 404
'''

def test_get_visita_invalid_id(client):
    # Intentamos obtener una visita con un ID inválido
    invalid_id = "not-a-uuid"
    response = client.get(f'/visitas/{invalid_id}', headers={'Authorization': 'Bearer 1234'})
    
    assert response.status_code == 400

'''
# Tests para eliminar una visita
def test_delete_visita_success(client, valid_visita_data):
    # Primero creamos una visita
    response = client.post('/visitas', json=valid_visita_data, headers={'Authorization': 'Bearer 1234'})
    assert response.status_code == 201
    
    # Obtenemos el ID de la visita creada
    visita_id = response.get_json()['id']
    
    # Eliminamos la visita
    response = client.delete(f'/visitas/{visita_id}', headers={'Authorization': 'Bearer 1234'})
    
    assert response.status_code == 200
    assert response.get_json()['msg'] == "la visita fue eliminada"
    
    # Verificamos que ya no existe
    response = client.get(f'/visitas/{visita_id}', headers={'Authorization': 'Bearer 1234'})
    assert response.status_code == 404
'''

'''
def test_delete_visita_not_found(client):
    # Intentamos eliminar una visita con un ID que no existe
    non_existent_id = "00000000-0000-0000-0000-000000000000"
    response = client.delete(f'/visitas/{non_existent_id}', headers={'Authorization': 'Bearer 1234'})
    
    assert response.status_code == 404
'''

def test_delete_visita_invalid_id(client):
    # Intentamos eliminar una visita con un ID inválido
    invalid_id = "not-a-uuid"
    response = client.delete(f'/visitas/{invalid_id}', headers={'Authorization': 'Bearer 1234'})
    
    assert response.status_code == 400

'''
# Tests adicionales para validación de datos
def test_create_visita_invalid_date_format(client, valid_visita_data):
    # Creamos una copia de los datos válidos y modificamos el formato de fecha
    invalid_data = valid_visita_data.copy()
    invalid_data['fecha'] = "fecha-invalida"
    
    response = client.post('/visitas', json=invalid_data, headers={'Authorization': 'Bearer 1234'})
    
    assert response.status_code == 400
'''
'''
def test_create_visita_invalid_time_format(client, valid_visita_data):
    # Creamos una copia de los datos válidos y modificamos el formato de hora
    invalid_data = valid_visita_data.copy()
    invalid_data['horaDesde'] = "hora-invalida"
    
    response = client.post('/visitas', json=invalid_data, headers={'Authorization': 'Bearer 1234'})
    
    assert response.status_code == 400
'''

# Test para verificar que la respuesta de lista de visitas incluye todos los campos
def test_list_visitas_response_structure(client, valid_visita_data):
    # Primero creamos una visita
    client.post('/visitas', json=valid_visita_data, headers={'Authorization': 'Bearer 1234'})
    
    # Obtenemos la lista de visitas
    response = client.get('/visitas', headers={'Authorization': 'Bearer 1234'})
    
    assert response.status_code == 200
    visitas = response.get_json()
    assert len(visitas) > 0
    
    # Verificamos la estructura de la primera visita
    visita = visitas[0]
    assert 'id' in visita
    assert 'fecha' in visita
    assert 'horaDesde' in visita
    assert 'horaHasta' in visita
    assert 'comentarios' in visita
    assert 'idUsuario' in visita
    assert 'createdAt' in visita
    assert 'updatedAt' in visita