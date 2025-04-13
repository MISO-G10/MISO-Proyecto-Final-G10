from .base_command import BaseCommand
from src.db.session import engine, SessionLocal
from src.models.visita import Visita

class Clean(BaseCommand):
  def __init__(self):
      pass
  
  def execute(self):
    try:
        session = SessionLocal()
        with engine.connect() as connection:
            visitasLen = session.query(Visita).count()
            if visitasLen == 0:
                return

            visitas = session.query(Visita).delete()
            session.commit()
            print(f"Se eliminaron {visitas} registros")
        return {"msg": "Todos los datos fueron eliminados"}
    except Exception as e:
        print(f"Error al eliminar datos: {str(e)}")
        return {"error": str(e)}

    