import requests
from flask import jsonify
import os
from .config import get_config

def get_vendedor(id, token=None):
    try:
        env_name = os.getenv('FLASK_ENV', 'development')
        envData = get_config(env_name)
        usuarios_path = f"{envData.USUARIOS_PATH}/usuarios/{id}"

        headers = {}
        if token:
            headers["Authorization"] = f"Bearer {token}"

        response = requests.get(usuarios_path, headers=headers)

        if response.status_code != 200:
            raise Exception(f"No se pudo obtener el usuario. Código: {response.status_code}")

        return response.json()

    except Exception as e:
        raise Exception(f"Error al obtener usuario: {e}")
