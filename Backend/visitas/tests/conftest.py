import pytest
import requests

from src.main import create_app
from unittest.mock import patch, MagicMock
from sqlalchemy.orm import sessionmaker
from sqlalchemy import create_engine


@pytest.fixture(scope='module')
def session():
    with patch('sqlalchemy.create_engine') as mock_engine, patch('sqlalchemy.inspect') as mock_inspect:
        # Mockeamos el motor de SQLAlchemy para evitar conexiones reales
        mock_engine.return_value = MagicMock()

        # Crear un engine falso para las pruebas (SQLite en memoria)
        fake_engine = create_engine('sqlite:///:memory:')

        # Configuramos el inspector para que retorne una lista vacía de tablas
        mock_inspect.return_value.get_table_names.return_value = []

        # Creamos un sessionmaker usando el engine falso
        Session = sessionmaker(bind=fake_engine)
        session = Session()

        yield session
        session.close()


@pytest.fixture(scope='module')
def client():
    app = create_app('test')
    app.config.update({
        "TESTING": True,
    })

    with app.test_client() as client, patch('sqlalchemy.create_engine') as mock_engine, patch('sqlalchemy.inspect') as mock_inspect:
        # Mockeamos el engine para evitar conexiones reales
        mock_engine.return_value.connect.return_value = None

        # Configuramos el inspector para que retorne una lista vacía
        mock_inspect.return_value.get_table_names.return_value = []

        yield client


# Fixture para simular la autenticación mediante monkeypatch
@pytest.fixture(autouse=True)
def patch_auth(monkeypatch):
    """
    Este fixture parchea requests.get para simular una respuesta exitosa
    al llamar a "http://localhost:3000/usuarios/me". Se aplica automáticamente
    a todos los tests, por lo que no es necesario modificar cada test individual.
    """
    class FakeResponse:
        def __init__(self, json_data, status_code):
            self.json_data = json_data
            self.status_code = status_code

        def json(self):
            return self.json_data

    def fake_get(url, headers=None, *args, **kwargs):
        if url == "http://localhost:3000/usuarios/me":
            return FakeResponse({"id": "test_usuario_id"}, 200)
        return FakeResponse({}, 404)

    monkeypatch.setattr(requests, "get", fake_get)