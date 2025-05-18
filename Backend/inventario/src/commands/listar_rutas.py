from sqlalchemy import select
from src.db.session import SessionLocal
from src.models.ruta import Ruta

class ListRutas:
    def execute(self):
        """
        Obtiene todas las rutas disponibles en la base de datos.
        
        Returns:
            list: Lista de rutas con sus detalles
        """
        session = SessionLocal()
        
        try:
            # Consultar todas las rutas
            stmt = select(Ruta)
            rutas = session.execute(stmt).scalars().all()
            
            # Convertir a lista de diccionarios para la respuesta JSON
            result = []
            for ruta in rutas:
                result.append({
                    "id": str(ruta.id),
                    "nombre": ruta.nombre,
                    "placa": ruta.placa,
                    "conductor": ruta.conductor,
                    "createdAt": ruta.createdAt.isoformat() if ruta.createdAt else None,
                    "updatedAt": ruta.updatedAt.isoformat() if ruta.updatedAt else None
                })
            
            return result
        
        except Exception as e:
            session.rollback()
            return {"error": str(e)}, 500
        
        finally:
            session.close()
