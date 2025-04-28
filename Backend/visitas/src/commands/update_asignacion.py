from flask import jsonify
from marshmallow import ValidationError, Schema, fields

from src.db.session import SessionLocal
from src.errors.errors import InvalidAsignacionData, AsignacionNotFound
from src.models.asignacion import AsignacionClienteTendero
from .base_command import BaseCommand


class UpdateAsignacionSchema(Schema):
    estado = fields.Str(required=True)


class UpdateAsignacion(BaseCommand):
    def __init__(self, id, data):
        self.id = id
        self.data = data

    def execute(self):
        schema = self.safe_payload()
        db = SessionLocal()
        
        #Se busca la asignación por ID
        asignacion = db.query(AsignacionClienteTendero).filter(
            AsignacionClienteTendero.id == self.id
        ).first()
        
        if not asignacion:
            raise AsignacionNotFound()
        
        # Se actualiza el estado
        asignacion.estado = schema['estado']
        db.commit()

        return {
            "id": asignacion.id,
            "message": f"Estado de asignación actualizado a {schema['estado']}",
            "updatedAt": asignacion.updatedAt.isoformat()
        }

    def safe_payload(self):
        try:
            schema = UpdateAsignacionSchema().load(self.data)
        except ValidationError as e:
            raise InvalidAsignacionData(e.messages)
        
        # Se verifica que el estado sea válido
        if schema['estado'] not in ['ACTIVO', 'INACTIVO']:
            raise InvalidAsignacionData({"estado": ["El estado debe ser ACTIVO o INACTIVO"]})
        
        return schema
