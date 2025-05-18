#!/usr/bin/env python
"""
Script para crear fabricantes de prueba en la base de datos.
"""
import sys
import os

# Agregar el directorio padre a sys.path para asegurar que las importaciones funcionen correctamente
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '../..')))

from src.db.session import SessionLocal
from src.models.fabricante import Fabricante

# Lista de fabricantes a crear
FABRICANTES = [
    {
        "nombre": "Colgate-Palmolive",
        "numeroTel": "+(571) 298-7100",
        "representante": "Juan Pérez"
    },
    {
        "nombre": "Unilever",
        "numeroTel": "+(571) 405-8000",
        "representante": "María Rodríguez"
    },
    {
        "nombre": "Nestlé",
        "numeroTel": "+(571) 592-8000",
        "representante": "Carlos García"
    },
    {
        "nombre": "P&G Colombia",
        "numeroTel": "+(571) 628-7000",
        "representante": "Ana Martínez"
    },
    {
        "nombre": "Johnson & Johnson",
        "numeroTel": "+(571) 745-9000",
        "representante": "Pedro Sánchez"
    }
]

def seed_fabricantes():
    """Crear fabricantes en la base de datos"""
    db = SessionLocal()

    try:
        # Verificar fabricantes existentes para evitar duplicados
        existing_fabricantes = db.query(Fabricante).all()
        existing_nombres = {fabricante.nombre for fabricante in existing_fabricantes}

        created_count = 0
        skipped_count = 0

        # Crear cada fabricante si no existe
        for fabricante_data in FABRICANTES:
            if fabricante_data["nombre"] in existing_nombres:
                print(f"Omitiendo {fabricante_data['nombre']} (ya existe)")
                skipped_count += 1
                continue

            fabricante = Fabricante(
                nombre=fabricante_data["nombre"],
                numeroTel=fabricante_data["numeroTel"],
                representante=fabricante_data["representante"]
            )
            db.add(fabricante)
            created_count += 1
            print(f"Creado {fabricante_data['nombre']}")

        db.commit()
        print(f"\nResumen: Creados {created_count} fabricantes, omitidos {skipped_count} existentes")

    except Exception as e:
        db.rollback()
        print(f"Error al crear fabricantes: {e}")
    finally:
        db.close()

if __name__ == "__main__":
    seed_fabricantes()
