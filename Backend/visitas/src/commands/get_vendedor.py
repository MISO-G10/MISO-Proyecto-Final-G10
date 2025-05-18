from src.db.session import SessionLocal
from src.models.asignacion import AsignacionClienteTendero
from .base_command import BaseCommand

class GetVendedorIdFromTendero(BaseCommand):
    def __init__(self, usuario):
        self.usuario = usuario
        self.id_tendero = usuario['id']

    def execute(self):
        db = SessionLocal()
        try:
            asignacion = db.query(AsignacionClienteTendero).filter(
                AsignacionClienteTendero.idTendero == self.id_tendero,
                AsignacionClienteTendero.estado == 'ACTIVO'
            ).first()

            if asignacion:
                return {"idVendedor": str(asignacion.idVendedor)}
            else:
                return {"idVendedor": None}

        except Exception as e:
            print(f"Error al obtener idVendedor: {str(e)}")
            return {"error": str(e)}
        finally:
            db.close()
