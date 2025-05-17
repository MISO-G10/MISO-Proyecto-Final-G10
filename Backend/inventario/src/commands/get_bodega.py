from src.db.session import SessionLocal
from .base_command import BaseCommand
from ..models import Bodega


class GetBodega(BaseCommand):
    def __init__(self, bodega_id):
        self.bodega_id = bodega_id

    def execute(self):
        db = SessionLocal()

        try:
            bodega = db.query(Bodega).filter(Bodega.id == self.bodega_id).first()

            if not bodega:
                return None

            return bodega
        except Exception as e:
            print(f"Error al buscar producto: {str(e)}")
            return None
