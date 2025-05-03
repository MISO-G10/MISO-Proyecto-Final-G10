from src.db.session import SessionLocal
from src.models.visita import Visita
from .base_command import BaseCommand
from sqlalchemy import func, desc


class GetTenderoVisitasInfo(BaseCommand):
    def __init__(self, id_tendero):
        self.id_tendero = id_tendero
    
    def execute(self):
        """
        Obtiene información sobre las visitas realizadas a un tendero:
        - Fecha de la última visita
        - Número total de visitas
        """
        db = SessionLocal()
        
        try:
            # Contar el número total de visitas para este tendero
            visitas_count = db.query(func.count(Visita.id)).filter(
                Visita.idUsuario == self.id_tendero
            ).scalar() or 0
            
            # Obtener la última visita (ordenada por fecha descendente)
            ultima_visita = db.query(Visita).filter(
                Visita.idUsuario == self.id_tendero
            ).order_by(desc(Visita.fecha), desc(Visita.horaDesde)).first()
            
            # obtener todas las visitas asignadas para el tendero
            visitas = db.query(Visita).filter(
                Visita.idUsuario == self.id_tendero
            ).all()
            
            visitas_serializadas = []
            
            # Serializar las visitas
            for visita in visitas:
                visitas_serializadas.append({
                    "id": visita.id,
                    "fecha": visita.fecha,
                    "horaDesde": visita.horaDesde,
                    "horaHasta": visita.horaHasta,
                    "comentarios": visita.comentarios,
                    "cancelada": visita.cancelada,
                })
            
            # Formatear la respuesta
            result = {
                "numero_visitas": visitas_count,
                "ultima_visita": None,
                "visitas": visitas_serializadas
            }
            
            # Si hay una última visita, incluir su fecha
            if ultima_visita:
                result["ultima_visita"] = f"{ultima_visita.fecha}T{ultima_visita.horaDesde}:00"
            
            return result
        except Exception as e:
            print(f"Error al obtener información de visitas para tendero {self.id_tendero}: {str(e)}")
            return {
                "numero_visitas": 0,
                "ultima_visita": None,
                "visitas": []
            }
        finally:
            db.close()
