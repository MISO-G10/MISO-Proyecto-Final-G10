#!/usr/bin/env python
"""
Seed Bodegas Script

This script creates predefined bodegas in the inventory system by directly
inserting them into the database.
"""

from src.db.session import SessionLocal
from src.models.bodega import Bodega

# List of bodegas to create
BODEGAS = [
    {
        "nombre": "Bodega Principal Bogotá",
        "direccion": "Calle 100 #15-25",
        "ciudad": "Bogotá",
        "pais": "Colombia"
    },
    {
        "nombre": "Bodega Norte Medellín",
        "direccion": "Carrera 43A #1-50",
        "ciudad": "Medellín",
        "pais": "Colombia"
    },
    {
        "nombre": "Bodega Este Cali",
        "direccion": "Avenida 6N #28N-10",
        "ciudad": "Cali",
        "pais": "Colombia"
    },
    {
        "nombre": "Bodega Sur Barranquilla",
        "direccion": "Carrera 54 #58-90",
        "ciudad": "Barranquilla",
        "pais": "Colombia"
    },
    {
        "nombre": "Bodega Oeste Bucaramanga",
        "direccion": "Carrera 27 #52-41",
        "ciudad": "Bucaramanga",
        "pais": "Colombia"
    }
]

def seed_bodegas():
    """Create bodegas in the database"""
    db = SessionLocal()

    try:
        # Check if bodegas already exist to avoid duplicates
        existing_bodegas = db.query(Bodega).all()
        existing_nombres = {bodega.nombre for bodega in existing_bodegas}

        created_count = 0
        skipped_count = 0

        # Create each bodega if it doesn't already exist
        for bodega_data in BODEGAS:
            if bodega_data["nombre"] in existing_nombres:
                print(f"Skipping {bodega_data['nombre']} (already exists)")
                skipped_count += 1
                continue

            bodega = Bodega(
                nombre=bodega_data["nombre"],
                direccion=bodega_data["direccion"],
                ciudad=bodega_data["ciudad"],
                pais=bodega_data["pais"]
            )
            db.add(bodega)
            created_count += 1
            print(f"Created {bodega_data['nombre']}")

        db.commit()
        print(f"\nSummary: Created {created_count} bodegas, skipped {skipped_count} existing bodegas")

    except Exception as e:
        db.rollback()
        print(f"Error creating bodegas: {e}")
    finally:
        db.close()

if __name__ == "__main__":
    seed_bodegas()
