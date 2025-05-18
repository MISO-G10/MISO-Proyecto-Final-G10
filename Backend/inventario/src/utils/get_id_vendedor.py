from flask import g
import os
import requests
from .config import get_config

def get_visita_id():
    try:
        token = request.headers.get("Authorization", None)
        if not token:
            raise ValueError("Authorization token is missing.")

        env_name = os.getenv('FLASK_ENV', 'development')
        envData = get_config(env_name)
        visitas_path = envData.VISITAS_PATH + "/visitas/asignaciones/vendedor"  # ajusta según el endpoint real

        print(f"Consultando VISITAS en: {visitas_path}")

        response = requests.get(visitas_path, headers={"Authorization": token})
        if response.status_code != 200:
            raise Exception("Failed to fetch data from visitas service.")

        visita_data = response.json()
        return visita_data

    except Exception as e:
        print(f"Error getting visita id: {e}")
        raise