from flask import jsonify
from marshmallow import ValidationError, Schema, fields

from src.db.session import SessionLocal
from src.errors.errors import InvalidVisitaData
from src.models.visita import Visita
from .base_command import BaseCommand


class CreateVisitaSchema(Schema):
    fecha = fields.Str(required=True)
    horaDesde = fields.Str(required=True)
    horaHasta = fields.Str(required=True)
    comentarios = fields.Str(required=False)
    idUsuario = fields.UUID(required=True)

class Create(BaseCommand):
    def __init__(self, usuario, data):
        self.usuario = usuario
        self.data = data

    def execute(self):
        schema = self.safe_payload()
        db = SessionLocal()
        
        new_visita = Visita(
            fecha=schema['fecha'],
            horaDesde=schema['horaDesde'],
            horaHasta=schema['horaHasta'],
            comentarios=schema['comentarios'] if 'comentarios' in schema else None,
            idUsuario=schema['idUsuario']
        )

        db.add(new_visita)
        db.commit()

        return {
            "id": new_visita.id,
            "createdAt": new_visita.createdAt.isoformat()
        }

    def safe_payload(self):
        try:
            schema = CreateVisitaSchema().load(self.data)
        except ValidationError as e:
            raise InvalidVisitaData(e.messages)
        

        # add more data validations here or data formatting before CRUD manipulation
        
        return schema