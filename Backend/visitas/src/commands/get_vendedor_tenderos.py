import os
import requests
from src.db.session import SessionLocal
from src.models.asignacion import AsignacionClienteTendero
from .base_command import BaseCommand


class GetVendedorTenderos(BaseCommand):
    def __init__(self, usuario):
        self.usuario = usuario
        self.id_vendedor = usuario['id']
    
    def execute(self):
        db = SessionLocal()
        
        try:
            # Obtener todas las asignaciones activas del vendedor
            asignaciones = db.query(AsignacionClienteTendero).filter(
                AsignacionClienteTendero.idVendedor == self.id_vendedor,
                AsignacionClienteTendero.estado == 'ACTIVO'
            ).all()
            
            # Extraer IDs de tenderos
            ids_tenderos = [str(asignacion.idTendero) for asignacion in asignaciones]
            
            if not ids_tenderos:
                return []
            
            # Obtener información completa de los tenderos del servicio de usuarios
            tenderos_info = self._get_tenderos_info(ids_tenderos)
            
            return tenderos_info
        except Exception as e:
            print(f"Error al obtener tenderos: {str(e)}")
            return []
        finally:
            db.close()
    
    def _get_tenderos_info(self, ids_tenderos):
        # URL del servicio de usuarios (desde variables de entorno)
        usuarios_service_url = os.getenv('USUARIOS_PATH', 'http://usuarios:3000')
        
        # Obtener token de autorización para comunicación entre servicios
        token = self.usuario.get("token", "")
        
        headers = {
            'Authorization': f'Bearer {token}'
        }
        
        try:
            # Realizar petición al servicio de usuarios
            # La URL correcta debe incluir el prefijo /usuarios porque el blueprint está registrado con ese prefijo
            url = f"{usuarios_service_url}/usuarios/batch"
            
            response = requests.post(
                url,
                json={"ids": ids_tenderos},
                headers=headers
            )
            
            if response.status_code == 200:
                return response.json()
            else:
                print(f"Error al obtener información de tenderos: {response.status_code} - {response.text}")
                return []
        except Exception as e:
            print(f"Error en la comunicación con el servicio de usuarios: {str(e)}")
            return []
