from src.db.session import SessionLocal
from src.models.producto import Producto
from src.models.inventario_bodega import InventarioBodega
from .base_command import BaseCommand
from sqlalchemy.orm import joinedload
from src.utils.helpers import serialize_sqlalchemy


class ListProductos(BaseCommand):
    def __init__(self):
        pass

    def execute(self):
        db = SessionLocal()
        try:
            # Consultar productos con al menos un registro en InventarioBodega
            productos = (
                db.query(Producto)
                .join(InventarioBodega, Producto.id == InventarioBodega.producto_id)
                .filter(InventarioBodega.cantidad > 0)
                .options(joinedload(Producto.bodega_productos))
                .group_by(Producto.id)
                .all()
            )

            productos_list = []
            for producto in productos:
                # Calculo de cantidad total de productos en todas las bodegas
                cantidad_total = sum([ib.cantidad for ib in producto.bodega_productos])
                # Serializar el producto usando serialize_sqlalchemy
                producto_dict = serialize_sqlalchemy(producto)
                # Agregar la cantidad total que no es parte del modelo
                producto_dict['cantidad_total'] = cantidad_total
                productos_list.append(producto_dict)

            return productos_list

        except Exception as e:
            db.rollback()
            raise e
