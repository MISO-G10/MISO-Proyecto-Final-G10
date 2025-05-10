from sqlalchemy.orm import joinedload

from src.db.session import SessionLocal
from .base_command import BaseCommand
from .get_producto import GetProducto
from .get_bodega import GetBodega
from ..models import InventarioBodega


class GetProductoBodegaInventario(BaseCommand):
    def __init__(self, bodega_id, producto_id):
        self.producto_id = producto_id
        self.bodega_id = bodega_id

    def execute(self):
        db = SessionLocal()

        try:
            # Get the product first
            producto = GetProducto(self.producto_id).execute()
            bodega = GetBodega(self.bodega_id).execute()

            if not producto:
                return {"error": f"Producto con id/sku {self.producto_id} no encontrado"}, 404

            if not bodega:
                return {"error": f"Bodega con id {self.bodega_id} no encontrada"}, 404

            # Query the inventory_bodega relationships with bodega details
            inventario = db.query(InventarioBodega) \
                .filter(InventarioBodega.producto_id == producto.id) \
                .filter(InventarioBodega.bodega_id == bodega.id) \
                .options(joinedload(InventarioBodega.bodega)) \
                .first()

            result = {
                "bodega_id": bodega.id,
                "producto_id": producto.id,
                "cantidad": inventario.cantidad if inventario else 0,
                "nombre": bodega.nombre,
                "direccion": bodega.direccion,
            }

            return result

        except Exception as e:
            return {"error": str(e)}, 500
