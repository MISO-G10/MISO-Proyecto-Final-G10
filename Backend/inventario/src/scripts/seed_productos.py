#!/usr/bin/env python
"""
Script para crear productos de prueba y asociarlos a fabricantes e inventarios.
"""
import sys
import os
import requests
import uuid

# Agregar el directorio padre a sys.path para asegurar que las importaciones funcionen correctamente
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '../..')))

from src.db.session import SessionLocal
from src.models.producto import Producto
from src.models.inventario_bodega import InventarioBodega
from src.models.bodega import Bodega

from datetime import datetime, timedelta

# Lista de productos a crear
PRODUCTOS = [
    {
        "nombre": "Colgate Triple Acción",
        "descripcion": "Pasta dental con triple acción: limpia, protege y refresca",
        "categoria": "CUIDADO_PERSONAL",
        "fabricante": "Colgate-Palmolive",
        "sku": "CTA-001",
        "perecedero": True,
        "fechaVencimiento": datetime.now() + timedelta(days=365),
        "valorUnidad": 3500,
        "tiempoEntrega": datetime.now() + timedelta(days=2),
        "condicionAlmacenamiento": "Mantener en lugar fresco y seco",
        "reglasLegales": "Registro INVIMA",
        "reglasComerciales": "Venta libre",
        "reglasTributarias": "IVA 19%",
        "cantidadTotal": 1000
    },
    {
        "nombre": "Rexona Clinical",
        "descripcion": "Desodorante antitranspirante de máxima protección",
        "categoria": "CUIDADO_PERSONAL",
        "fabricante": "Unilever",
        "sku": "RXC-001",
        "perecedero": True,
        "fechaVencimiento": datetime.now() + timedelta(days=730),
        "valorUnidad": 15000,
        "tiempoEntrega": datetime.now() + timedelta(days=2),
        "condicionAlmacenamiento": "Mantener en lugar fresco y seco",
        "reglasLegales": "Registro INVIMA",
        "reglasComerciales": "Venta libre",
        "reglasTributarias": "IVA 19%",
        "cantidadTotal": 500
    },
    {
        "nombre": "Nescafé Clásico",
        "descripcion": "Café instantáneo clásico",
        "categoria": "ALIMENTOS_BEBIDAS",
        "fabricante": "Nestlé",
        "sku": "NSC-001",
        "perecedero": True,
        "fechaVencimiento": datetime.now() + timedelta(days=365),
        "valorUnidad": 12000,
        "tiempoEntrega": datetime.now() + timedelta(days=3),
        "condicionAlmacenamiento": "Mantener en lugar fresco y seco",
        "reglasLegales": "Registro INVIMA, Registro Sanitario",
        "reglasComerciales": "Venta libre",
        "reglasTributarias": "IVA 19%",
        "cantidadTotal": 800
    },
    {
        "nombre": "Pampers Premium Care",
        "descripcion": "Pañales para bebé talla G",
        "categoria": "BEBES",
        "fabricante": "P&G Colombia",
        "sku": "PPC-001",
        "perecedero": False,
        "fechaVencimiento": None,
        "valorUnidad": 45000,
        "tiempoEntrega": datetime.now() + timedelta(days=2),
        "condicionAlmacenamiento": "Mantener en lugar fresco y seco",
        "reglasLegales": "Registro INVIMA",
        "reglasComerciales": "Venta libre",
        "reglasTributarias": "IVA 19%",
        "cantidadTotal": 300
    },
    {
        "nombre": "Johnson's Baby Shampoo",
        "descripcion": "Shampoo suave para bebé",
        "categoria": "BEBES",
        "fabricante": "Johnson & Johnson",
        "sku": "JBS-001",
        "perecedero": True,
        "fechaVencimiento": datetime.now() + timedelta(days=730),
        "valorUnidad": 18000,
        "tiempoEntrega": datetime.now() + timedelta(days=2),
        "condicionAlmacenamiento": "Mantener en lugar fresco y seco",
        "reglasLegales": "Registro INVIMA",
        "reglasComerciales": "Venta libre",
        "reglasTributarias": "IVA 19%",
        "cantidadTotal": 400
    }
]

def get_fabricante_by_nombre(nombre):
    """Obtener el ID de un fabricante por su nombre usando un mapeo directo"""
    # Mapeo de nombres a IDs (estos IDs deben coincidir con los creados en el seeder de fabricantes)
    fabricantes = {
        "Colgate-Palmolive": "fab-001",
        "Unilever": "fab-002",
        "Nestlé": "fab-003",
        "P&G Colombia": "fab-004",
        "Johnson & Johnson": "fab-005"
    }
    
    return fabricantes.get(nombre)

def get_bodegas():
    """Obtener todas las bodegas de la base de datos"""
    db = SessionLocal()
    try:
        return db.query(Bodega).all()
    finally:
        db.close()

def seed_productos():
    """Crear productos y asociarlos a inventarios"""
    db = SessionLocal()

    try:
        # Obtener bodegas existentes
        bodegas = get_bodegas()
        if not bodegas:
            print("No hay bodegas disponibles. Ejecute primero seed_bodegas.py")
            return

        created_count = 0
        skipped_count = 0

        # Crear cada producto si no existe
        for producto_data in PRODUCTOS:
            # Verificar si el producto ya existe por SKU
            existing_producto = db.query(Producto).filter(
                Producto.sku == producto_data["sku"]
            ).first()

            if existing_producto:
                print(f"Omitiendo {producto_data['nombre']} (ya existe)")
                skipped_count += 1
                continue

            # Obtener ID del fabricante
            fabricante_id = get_fabricante_by_nombre(producto_data["fabricante"])
            if not fabricante_id:
                print(f"No se encontró el fabricante {producto_data['fabricante']}")
                continue

            # Crear el producto
            nuevo_producto = Producto(
                nombre=producto_data["nombre"],
                descripcion=producto_data["descripcion"],
                categoria=producto_data["categoria"],
                fabricante_id=fabricante_id,
                sku=producto_data["sku"],
                perecedero=producto_data["perecedero"],
                fechaVencimiento=producto_data["fechaVencimiento"],
                valorUnidad=producto_data["valorUnidad"],
                tiempoEntrega=producto_data["tiempoEntrega"],
                condicionAlmacenamiento=producto_data["condicionAlmacenamiento"],
                reglasLegales=producto_data["reglasLegales"],
                reglasComerciales=producto_data["reglasComerciales"],
                reglasTributarias=producto_data["reglasTributarias"]
            )
            db.add(nuevo_producto)
            db.flush()  # Para obtener el ID del producto

            # Distribuir el inventario entre las bodegas
            cantidad_por_bodega = producto_data["cantidadTotal"] // len(bodegas)
            for bodega in bodegas:
                inventario = InventarioBodega(
                    bodega_id=bodega.id,
                    producto_id=nuevo_producto.id,
                    cantidad=producto_data["cantidadTotal"] // len(bodegas)
                )
                db.add(inventario)

            created_count += 1
            print(f"Creado {producto_data['nombre']}")

        db.commit()
        print(f"\nResumen: Creados {created_count} productos, omitidos {skipped_count} existentes")

    except Exception as e:
        db.rollback()
        print(f"Error al crear productos: {e}")
    finally:
        db.close()

if __name__ == "__main__":
    seed_productos()
