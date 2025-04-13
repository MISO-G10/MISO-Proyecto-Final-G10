from marshmallow import ValidationError, Schema, fields

from .base_command import BaseCommannd
from ..db.session import SessionLocal
from ..errors.errors import InvalidUsuarioData
from ..models.usuario import Usuario
from ..utils.helpers import serialize_sqlalchemy


class ListUsuarioSchema(Schema):
    #Esta linea permite filtrar por nombre del Usuario
    rol = fields.Str(required=False)

class List(BaseCommannd):
    def __init__(self, usuario, data):
        self.usuario = usuario
        self.data = data

    def execute(self):
        db = SessionLocal()
        payload = self.safe_payload()
        conditions = []
        query = db.query(Usuario)
        print(payload.get('rol'))
        
        if payload.get('rol'):
            conditions.append(Usuario.rol == payload['rol'])

        if conditions:
            query = query.filter(*conditions)

        return serialize_sqlalchemy(query.all())

    def safe_payload(self):
        try:
            schema = ListUsuarioSchema().load(self.data)
        except ValidationError as e:
            raise InvalidUsuarioData(e.messages)

        return schema
