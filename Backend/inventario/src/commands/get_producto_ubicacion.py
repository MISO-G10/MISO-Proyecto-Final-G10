from sqlalchemy.orm import joinedload

from src.db.session import SessionLocal
from .base_command import BaseCommand
from .get_producto import GetProducto
from ..models import InventarioBodega


class GetProductoUbicacion(BaseCommand):
    def __init__(self, producto_id):
        self.producto_id = producto_id

    def execute(self):
        db = SessionLocal()

        try:
            # Get the product first
            producto = GetProducto(self.producto_id).execute()

            if not producto:
                return {"error": f"Producto con id/sku {self.producto_id} no encontrado"}, 404

            # Query the inventory_bodega relationships with bodega details
            inventory_items = db.query(InventarioBodega) \
                .filter(InventarioBodega.producto_id == producto.id) \
                .options(joinedload(InventarioBodega.bodega)) \
                .all()

            # Format the result
            ubicaciones = []
            for item in inventory_items:
                bodega = item.bodega
                ubicacion = {
                    "bodega_id": str(bodega.id),
                    "nombre_bodega": bodega.nombre,
                    "direccion": bodega.direccion,
                    "cantidad": item.cantidad,
                    "ciudad": bodega.ciudad,
                    "pais": bodega.pais,
                }
                ubicaciones.append(ubicacion)

            result = {
                "producto_id": str(producto.id),
                "sku": producto.sku,
                "nombre": producto.nombre,
                "ubicaciones": ubicaciones
            }

            return result

        except Exception as e:
            return {"error": str(e)}, 500
