from .base_command import BaseCommannd
from src.db.session import SessionLocal
from src.models.usuario import Usuario
from src.errors.errors import  Unauthorized

class GetUsuario(BaseCommannd):
    def __init__(self, id):
        self.id = id

    def execute(self):
        db = SessionLocal()
        try:
            usuario = db.query(Usuario).filter_by(id=self.id).first()
            if not usuario:
                raise Unauthorized

            return {
                "id": usuario.id,
                "username": usuario.username,
                "nombre": usuario.nombre,
                "apellido": usuario.apellido,
                "rol": usuario.rol.value,
                "direccion": usuario.direccion,
            }
        except Exception as e:
            db.rollback()
            raise e
