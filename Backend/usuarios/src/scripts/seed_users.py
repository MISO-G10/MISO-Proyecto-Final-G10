#!/usr/bin/env python
"""
Script to create test users for each role in the application.
Run this script to populate your database with test users.
"""
from src.db.session import SessionLocal
from src.models.usuario import Usuario, UsuarioRol
import bcrypt
from datetime import datetime, timedelta
import sys
import os

# Add the parent directory to sys.path to ensure imports work correctly
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '../..')))


def create_test_user(username, password, nombre, apellido, rol, telefono=None, direccion=None):
    """Create a test user with the specified attributes"""
    db = SessionLocal()
    try:
        # Check if user already exists
        user_exists = db.query(Usuario).filter(Usuario.username == username).first()
        if user_exists:
            print(f'Usuario {username} ya existe.')
            return
            
        # Generate password hash
        salt = bcrypt.gensalt()
        hashed_password = bcrypt.hashpw(password.encode('utf-8'), salt)
        
        # Set expiration date to 3 months from now
        expiration_date = datetime.now() + timedelta(days=90)  # expiración: 3 meses (90 dias)
        
        # Create new user
        new_user = Usuario(
            username=username,
            password=hashed_password.decode('utf-8'),
            nombre=nombre,
            apellido=apellido,
            telefono=telefono,
            direccion=direccion,
            rol=rol,
            salt=salt,
            token="",
            expireAt=expiration_date
        )
        db.add(new_user)
        db.commit()
        print(f'Usuario {username} ({rol.value}) creado con éxito.')
        return new_user.id
    except Exception as e:
        db.rollback()
        print(f'Error al crear usuario {username}: {str(e)}')
    finally:
        db.close()


def seed_all_users():
    """Create test users for all roles"""
    print("Creando usuarios de prueba...")
    
    # Create admin user
    admin_id = create_test_user(
        "admin@gmail.com", 
        "admin123", 
        "Administrador", 
        "Sistema", 
        UsuarioRol.ADMINISTRADOR
    )
    
    # Create tendero user
    tendero_id = create_test_user(
        "tendero1@gmail.com", 
        "tendero123", 
        "Tienda", 
        "Don Jose", 
        UsuarioRol.TENDERO,
        telefono="+(300) 221-5843",
        direccion="Cra 20 #127A-05, Bogotá, Colombia"
    )
    
    # Create tendero user
    tendero_id2 = create_test_user(
        "tendero2@gmail.com", 
        "tendero123", 
        "Tienda", 
        "Surtimax", 
        UsuarioRol.TENDERO,
        telefono="+(311) 205-34897",
        direccion="Cra. 12 A #134-10, Bogotá, Colombia"
    )
    
    # Create tendero user
    tendero_id3 = create_test_user(
        "tendero3@gmail.com", 
        "tendero123", 
        "Tienda", 
        "Calleja", 
        UsuarioRol.TENDERO,
        telefono="+(312) 225-3544",
        direccion="Calle 163A # 13B-60, Bogotá, Colombia"
    )
    
    # Create tendero user
    tendero_id4 = create_test_user(
        "tendero4@gmail.com", 
        "tendero123", 
        "Tienda", 
        "Santa Ana", 
        UsuarioRol.TENDERO,
        telefono="+(318) 205-2160",
        direccion="Cll. 108 #3-42, Bogotá, Colombia"
    )

    # Create tendero user
    tendero_id5 = create_test_user(
        "tendero5@gmail.com", 
        "tendero123", 
        "Tienda", 
        "La Septima", 
        UsuarioRol.TENDERO,
        telefono="+(318) 205-2160",
        direccion="Carrera 7 # 32-16, Bogotá, Colombia"
    )

    # Create tendero user
    tendero_id6 = create_test_user(
        "tendero6@gmail.com", 
        "tendero123", 
        "Tienda", 
        "Nacional", 
        UsuarioRol.TENDERO,
        telefono="+(318) 205-2160",
        direccion="Museo Nacional, Bogotá, Colombia"
    )

    # Create tendero user
    tendero_id7 = create_test_user(
        "tendero7@gmail.com", 
        "tendero123", 
        "Tienda", 
        "Centro", 
        UsuarioRol.TENDERO,
        telefono="+(318) 205-2160",
        direccion="Plaza de Bolívar, Bogotá, Colombia"
    )

    # Create tendero user
    tendero_id8 = create_test_user(
        "tendero7@gmail.com", 
        "tendero123", 
        "Tienda", 
        "Oro", 
        UsuarioRol.TENDERO,
        telefono="+(318) 205-2160",
        direccion="Museo del Oro, Bogotá, Colombia"
    )
    
    # Create vendedor user
    vendedor_id = create_test_user(
        "vendedor@gmail.com", 
        "vendedor123", 
        "Andres Garcia", 
        "Triana", 
        UsuarioRol.VENDEDOR,
        telefono="+(314) 235-2199",
        direccion="Carrera 20 21-22 Apt 206"
    )

    # Create vendedor user
    vendedor_id2 = create_test_user(
        "vendedor2@gmail.com", 
        "vendedor123", 
        "Juan", 
        "Perez", 
        UsuarioRol.VENDEDOR,
        telefono="+(314) 235-2199",
        direccion="Carrera 20 21-22 Apt 206"
    )

    # Create vendedor user
    vendedor_id3 = create_test_user(
        "vendedor3@gmail.com", 
        "vendedor123", 
        "Camilo", 
        "Triana", 
        UsuarioRol.VENDEDOR,
        telefono="+(311) 344-9987",
        direccion="Calle 18 134-22"
    )
    
    # Create logistica user
    logistica_id = create_test_user(
        "logistica@gmail.com", 
        "logistica123", 
        "Logistica", 
        "Prueba", 
        UsuarioRol.LOGISTICA,
        telefono="+(314) 344-9987",
        direccion="Calle 18 134-20"
    )

    # Create director ventas user
    director_ventas_id = create_test_user(
        "directorventas@gmail.com", 
        "directorventas123", 
        "Director", 
        "Ventas", 
        UsuarioRol.DIRECTOR_VENTAS,
        telefono="+(314) 344-1822",
        direccion="Calle 18 134-20"
    )

    # Create comprasproveedores user
    comprasproveedores_id = create_test_user(
        "encargadoproveedores@gmail.com", 
        "encargadoproveedores123", 
        "Encargado", 
        "ComprasProveedores", 
        UsuarioRol.ENCARGADO_COMPRAS_PROVEEDORES,
        telefono="+(300) 211-9944",
        direccion="Carrera 18 114-60"
    )
    
    print("Proceso de creación de usuarios de prueba completado.")


if __name__ == "__main__":
    seed_all_users()
