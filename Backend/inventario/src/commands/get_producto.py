from sqlalchemy import or_
import uuid

from src.db.session import SessionLocal
from .base_command import BaseCommand
from ..models import Producto


class GetProducto(BaseCommand):
    def __init__(self, producto_id):
        self.producto_id = producto_id

    def execute(self):
        db = SessionLocal()

        try:
            producto = db.query(Producto).filter(Producto.sku == self.producto_id).first()

            if not producto:
                producto = db.query(Producto).filter(Producto.nombre.ilike(f"%{self.producto_id}%")).first()

            if not producto:
                try:
                    if len(self.producto_id) == 36 and '-' in self.producto_id:
                        uuid_obj = uuid.UUID(self.producto_id)
                        producto = db.query(Producto).filter(Producto.id == uuid_obj).first()
                except (ValueError, TypeError):
                    pass

            if not producto:
                return None

            return producto
        except Exception as e:
            print(f"Error al buscar producto: {str(e)}")
            return None
