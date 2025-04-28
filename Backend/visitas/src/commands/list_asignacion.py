from src.db.session import SessionLocal
from src.models.asignacion import AsignacionClienteTendero
from .base_command import BaseCommand


class ListAsignacion(BaseCommand):
    def __init__(self, usuario, data=None):
        self.usuario = usuario
        self.data = data or {}

    def execute(self):
        db = SessionLocal()
        
        # Se inicia la consulta base
        query = db.query(AsignacionClienteTendero)
        
        # Se filtra por vendedor si se proporciona
        if 'idVendedor' in self.data:
            query = query.filter(AsignacionClienteTendero.idVendedor == self.data['idVendedor'])
        
        # Se filtra por tendero si se proporciona
        if 'idTendero' in self.data:
            query = query.filter(AsignacionClienteTendero.idTendero == self.data['idTendero'])
        
        # Se filtra por estado si se proporciona
        if 'estado' in self.data:
            query = query.filter(AsignacionClienteTendero.estado == self.data['estado'])
        else:
            # Por defecto, mostrar solo las asignaciones activas
            query = query.filter(AsignacionClienteTendero.estado == 'ACTIVO')
        
        asignaciones = query.all()
        
        # Se muestran los resultados
        result = []
        for asignacion in asignaciones:
            result.append({
                "id": asignacion.id,
                "idVendedor": asignacion.idVendedor,
                "idTendero": asignacion.idTendero,
                "estado": asignacion.estado,
                "createdAt": asignacion.createdAt.isoformat(),
                "updatedAt": asignacion.updatedAt.isoformat()
            })
        
        return result
