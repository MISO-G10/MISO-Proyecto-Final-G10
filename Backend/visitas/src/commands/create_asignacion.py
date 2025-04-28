from flask import jsonify
from marshmallow import ValidationError, Schema, fields

from src.db.session import SessionLocal
from src.errors.errors import InvalidAsignacionData
from src.models.asignacion import AsignacionClienteTendero
from .base_command import BaseCommand


class CreateAsignacionSchema(Schema):
    idVendedor = fields.UUID(required=True)
    idTendero = fields.UUID(required=True)
    estado = fields.Str(required=False)


class CreateAsignacion(BaseCommand):
    def __init__(self, usuario, data):
        self.usuario = usuario
        self.data = data

    def execute(self):
        schema = self.safe_payload()
        db = SessionLocal()
        
        # Verificar si ya existe una asignación entre estos usuarios
        existing = db.query(AsignacionClienteTendero).filter(
            AsignacionClienteTendero.idVendedor == schema['idVendedor'],
            AsignacionClienteTendero.idTendero == schema['idTendero']
        ).first()
        
        if existing:
            # Si ya existe pero está inactiva, la reactivamos
            if existing.estado == "INACTIVO":
                existing.estado = "ACTIVO"
                db.commit()
                return {
                    "id": existing.id,
                    "message": "Asignación reactivada exitosamente",
                    "createdAt": existing.createdAt.isoformat(),
                    "updatedAt": existing.updatedAt.isoformat()
                }
            else:
                # Si ya existe y está activa, devolvemos un mensaje
                return {
                    "id": existing.id,
                    "message": "La asignación ya existe",
                    "createdAt": existing.createdAt.isoformat()
                }
        
        # Si no existe, creamos una nueva asignación
        new_asignacion = AsignacionClienteTendero(
            idVendedor=schema['idVendedor'],
            idTendero=schema['idTendero'],
            estado=schema.get('estado', 'ACTIVO')
        )

        db.add(new_asignacion)
        db.commit()

        return {
            "id": new_asignacion.id,
            "message": "Asignación creada exitosamente",
            "createdAt": new_asignacion.createdAt.isoformat()
        }

    def safe_payload(self):
        try:
            schema = CreateAsignacionSchema().load(self.data)
        except ValidationError as e:
            raise InvalidAsignacionData(e.messages)
        
        # Validaciones adicionales podrían ir aquí
        # Por ejemplo, verificar que los IDs correspondan a usuarios existentes con los roles correctos
        
        return schema
