from src.db.session import SessionLocal
from src.models.usuario import Usuario
from .base_command import BaseCommannd


class BatchGet(BaseCommannd):
    def __init__(self, data):
        self.data = data
    
    def execute(self):
        if 'ids' not in self.data or not isinstance(self.data['ids'], list):
            return []
        
        # Se obtiene información de los usuarios por sus IDs
        db = SessionLocal()
        
        try:
            # Convertir todos los IDs a string para asegurar compatibilidad
            ids_list = [str(id) for id in self.data['ids']]
            
            # Consultar usuarios
            usuarios = db.query(Usuario).filter(Usuario.id.in_(ids_list)).all()
            
            result = []
            for usuario in usuarios:
                result.append({
                    "id": str(usuario.id),
                    "nombre": usuario.nombre,
                    "apellido": usuario.apellido,
                    "username": usuario.username,
                    "telefono": usuario.telefono,
                    "direccion": usuario.direccion,
                    "rol": usuario.rol.value if hasattr(usuario.rol, 'value') else usuario.rol
                })
            
            return result
        except Exception as e:
            print(f"Error al obtener usuarios por batch: {str(e)}")
            return []
        finally:
            db.close()
