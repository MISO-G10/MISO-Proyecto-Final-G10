from flask import jsonify
from marshmallow import ValidationError, Schema, fields

from src.db.session import SessionLocal
from src.errors.errors import InvalidVisitaData, VisitaNotFound
from src.models.visita import Visita
from .base_command import BaseCommand


class UpdateSchema(Schema):
    fecha = fields.Str(required=True)
    horaDesde = fields.Str(required=True)
    horaHasta = fields.Str(required=True)
    comentarios = fields.Str(required=False)
    idUsuario = fields.UUID(required=True)
    cancelada = fields.Bool(required=True)


class Update(BaseCommand):
    def __init__(self, id, data):
        self.id = id
        self.data = data

    def execute(self):
        schema = self.safe_payload()
        db = SessionLocal()
        
        #Se busca la visita por ID
        visita = db.query(Visita).filter(
            Visita.id == self.id
        ).first()
        
        if not visita:
            raise VisitaNotFound()
        
        # Se actualiza la visita
        visita.fecha = schema['fecha']
        visita.horaDesde = schema['horaDesde']
        visita.horaHasta = schema['horaHasta']
        visita.comentarios = schema['comentarios']
        visita.idUsuario = schema['idUsuario']
        visita.cancelada = schema['cancelada']
        db.commit()

        return {
            "id": visita.id,
            "message": f"Visita actualizada",
            "updatedAt": visita.updatedAt.isoformat()
        }

    def safe_payload(self):
        try:
            schema = UpdateSchema().load(self.data)
        except ValidationError as e:
            raise InvalidVisitaData(e.messages)
        
        # add more data validations here or data formatting before CRUD manipulation
        
        return schema
