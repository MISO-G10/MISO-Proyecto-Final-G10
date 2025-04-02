from .base_command import BaseCommand
from src.db.session import engine, SessionLocal
from src.models.producto import Producto

class Clean(BaseCommand):
  def __init__(self):
      pass
  
  def execute(self):
    try:
        session = SessionLocal()
        with engine.connect() as connection:
            prodLen = session.query(Producto).count()
            if prodLen == 0:
                return

            productos = session.query(Producto).delete()
            session.commit()
            print(f"Se eliminaron {productos} registros")
        return {"msg": "Todos los datos fueron eliminados"}
    except Exception as e:
        print(f"Error al eliminar datos: {str(e)}")
        return {"error": str(e)}

    