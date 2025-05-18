from sqlalchemy import select
from src.db.session import SessionLocal
from src.models.bodega import Bodega

class ListBodegas:
    def execute(self):
        """
        Obtiene todas las bodegas disponibles en la base de datos.
        
        Returns:
            list: Lista de bodegas con sus detalles
        """
        session = SessionLocal()
        
        try:
            # Consultar todas las bodegas
            stmt = select(Bodega)
            bodegas = session.execute(stmt).scalars().all()

            # Convertir a lista de diccionarios para la respuesta JSON
            result = []
            for bodega in bodegas:
                result.append({
                    "id": str(bodega.id),
                    "nombre": bodega.nombre,
                    "direccion": bodega.direccion,
                    "createdAt": bodega.createdAt.isoformat() if bodega.createdAt else None,
                    "updatedAt": bodega.updatedAt.isoformat() if bodega.updatedAt else None
                })
            
            return result
        
        except Exception as e:
            session.rollback()
            return {"error": str(e)}, 500
        
        finally:
            session.close()