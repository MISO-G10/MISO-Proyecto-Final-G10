from src.db.session import SessionLocal
from src.models.bodega import Bodega
from .base_command import BaseCommand


class ListBodegas(BaseCommand):
    def __init__(self):
        pass

    def execute(self):
        db = SessionLocal()
        try:
            bodegas = db.query(Bodega).all()

            bodegas_list = []
            for bodega in bodegas:
                bodegas_list.append({
                    "id": bodega.id,
                    "nombre": bodega.nombre,
                    "direccion": bodega.direccion,
                    "ciudad": bodega.ciudad,
                    "pais": bodega.pais,
                    "createdAt": bodega.createdAt.isoformat() if bodega.createdAt else None
                })

            return bodegas_list

        except Exception as e:
            db.rollback()
            raise e