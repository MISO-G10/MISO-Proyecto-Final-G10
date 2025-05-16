#!/usr/bin/env python
"""
Script para crear asignaciones entre vendedores y tenderos en la tabla AsignacionClienteTendero.
Ejecuta este script para poblar la base de datos con asignaciones de prueba.
"""
import sys
import os
import requests
import uuid

# Agregar el directorio padre a sys.path para asegurar que las importaciones funcionen correctamente
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '../..')))

from src.db.session import SessionLocal
from src.models.asignacion import AsignacionClienteTendero

# Token de administrador para autenticación (este token se debe obtener previamente o usar credenciales almacenadas)
ADMIN_AUTH_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."  # Reemplazar con un token válido

def get_admin_token():
    """Obtener un token de administrador para autenticación"""
    usuarios_service_url = os.getenv('USUARIOS_PATH', 'http://usuarios:3000')
    
    try:
        # Credenciales de administrador (asegúrate de que estas credenciales existan en tu base de datos)
        admin_credentials = {
            "username": "admin@gmail.com",
            "password": "admin123"
        }
        
        # Autenticarse como administrador
        response = requests.post(
            f"{usuarios_service_url}/usuarios/auth",
            json=admin_credentials
        )
        
        if response.status_code == 200:
            data = response.json()
            return data.get("token")
        else:
            print(f"Error de autenticación: {response.status_code} - {response.text}")
            return None
    except Exception as e:
        print(f"Error al obtener token de administrador: {str(e)}")
        return None

def get_user_id_by_email(email):
    """Obtener el ID de usuario por email usando el servicio de usuarios"""
    # URL del servicio de usuarios (desde variables de entorno)
    usuarios_service_url = os.getenv('USUARIOS_PATH', 'http://usuarios:3000')
    
    # Obtener token de administrador para autenticación
    token = get_admin_token()
    if not token:
        print("No se pudo obtener un token de autenticación")
        return None
        
    headers = {
        'Authorization': f'Bearer {token}'
    }
    
    try:
        # Realizar petición al servicio de usuarios para obtener todos los usuarios
        response = requests.get(
            f"{usuarios_service_url}/usuarios",
            headers=headers
        )
        
        if response.status_code == 200:
            # Buscar el usuario con el email especificado
            usuarios = response.json()
            for usuario in usuarios:
                if usuario.get('username') == email:
                    return usuario.get('id')
            
            print(f"No se encontró un usuario con el email {email}")
            return None
        else:
            print(f"Error al obtener usuarios: {response.status_code} - {response.text}")
            return None
    except Exception as e:
        print(f"Error al consultar el servicio de usuarios: {str(e)}")
        return None

def create_asignacion(vendedor_id, tendero_id):
    """Crear una asignación entre un vendedor y un tendero"""
    db = SessionLocal()
    try:
        # Verificar si la asignación ya existe
        asignacion_exists = db.query(AsignacionClienteTendero).filter(
            AsignacionClienteTendero.idVendedor == vendedor_id,
            AsignacionClienteTendero.idTendero == tendero_id
        ).first()
        
        if asignacion_exists:
            print(f'Asignación entre vendedor {vendedor_id} y tendero {tendero_id} ya existe.')
            return None
            
        # Crear nueva asignación
        new_asignacion = AsignacionClienteTendero(
            idVendedor=vendedor_id,
            idTendero=tendero_id,
            estado="ACTIVO"
        )
        db.add(new_asignacion)
        db.commit()
        print(f'Asignación entre vendedor {vendedor_id} y tendero {tendero_id} creada con éxito.')
        return new_asignacion.id
    except Exception as e:
        db.rollback()
        print(f'Error al crear asignación: {str(e)}')
    finally:
        db.close()

def seed_asignaciones():
    """Crear asignaciones de prueba"""
    print("Creando asignaciones de prueba entre vendedor y tenderos...")
    
    # Obtener IDs de usuarios desde el servicio de usuarios
    vendedor_email = "vendedor@gmail.com"
    tendero1_email = "tendero1@gmail.com"
    tendero2_email = "tendero2@gmail.com"
    tendero3_email = "tendero3@gmail.com"
    
    # Obtener los IDs de los usuarios
    vendedor_id = get_user_id_by_email(vendedor_email)
    tendero1_id = get_user_id_by_email(tendero1_email)
    tendero2_id = get_user_id_by_email(tendero2_email)
    tendero3_id = get_user_id_by_email(tendero3_email)
    
    if not all([vendedor_id, tendero1_id, tendero2_id, tendero3_id]):
        print("ADVERTENCIA: No se pudieron obtener todos los IDs necesarios desde el servicio de usuarios.")
        print("Asegúrate de que el servicio de usuarios esté funcionando y que los emails existan en la base de datos.")
        
        # Opción alternativa: usar UUIDs fijos si no se pueden obtener por API
        # Esta opción debe usarse solo para desarrollo/pruebas y requiere conocer los IDs exactos
        print("Intentando usar IDs conocidos para desarrollo (solo para entornos de prueba)...")
        
        # Estos son IDs de ejemplo que deberías reemplazar con los IDs reales de tu base de datos
        # vendedor_id = "ID_REAL_DEL_VENDEDOR"
        # tendero1_id = "ID_REAL_DEL_TENDERO1"
        # tendero2_id = "ID_REAL_DEL_TENDERO2"
        # tendero3_id = "ID_REAL_DEL_TENDERO3"
        
        # Si no tienes los IDs, termina la ejecución
        return
    
    # Crear asignaciones
    create_asignacion(vendedor_id, tendero1_id)
    create_asignacion(vendedor_id, tendero2_id)
    create_asignacion(vendedor_id, tendero3_id)
    
    print("Proceso de creación de asignaciones de prueba completado.")

if __name__ == "__main__":
    seed_asignaciones()
