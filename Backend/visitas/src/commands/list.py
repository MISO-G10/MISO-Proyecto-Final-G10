from marshmallow import ValidationError, Schema, fields

from .base_command import BaseCommand
from ..db.session import SessionLocal
from ..errors.errors import InvalidVisitaData
from ..models.visita import Visita
from ..utils.helpers import serialize_sqlalchemy


class ListVisitaSchema(Schema):
    fecha = fields.Str(required=False) # filtrar por fecha

class List(BaseCommand):
    def __init__(self, usuario, data):
        self.usuario = usuario
        self.data = data

    def execute(self):
        db = SessionLocal()
        payload = self.safe_payload()
        conditions = []
        query = db.query(Visita)
        print(payload.get('fecha'))
        
        if payload.get('fecha'):
            conditions.append(Visita.fecha == payload['fecha'])

        if conditions:
            query = query.filter(*conditions)

        return serialize_sqlalchemy(query.all())

    def safe_payload(self):
        try:
            schema = ListVisitaSchema().load(self.data)
        except ValidationError as e:
            raise InvalidVisitaData(e.messages)

        return schema
