from marshmallow import Schema, fields, ValidationError, validates

from src.commands.base_command import BaseCommand
from src.db.session import SessionLocal
from src.errors.errors import InvalidBodegaData
from src.models import Bodega


class CreateBodegaSchema(Schema):
    nombre = fields.Str(required=True)
    direccion = fields.Str(required=True)
    ciudad = fields.Str(required=True)
    pais = fields.Str(required=True)


class CreateBodega(BaseCommand):
    def __init__(self, data):
        self.data = data

    def execute(self):
        schema = self.safe_payload()
        db = SessionLocal()

        try:
            # Se instancia una nueva bodega
            bodega = Bodega(
                nombre=schema['nombre'],
                direccion=schema['direccion'],
                ciudad=schema['ciudad'],
                pais=schema['pais'],
            )
            db.add(bodega)
            db.commit()

            # Convert to dictionary before session closes
            result = {
                "id": str(bodega.id),
                "nombre": bodega.nombre,
                "direccion": bodega.direccion,
                "ciudad": bodega.ciudad,
                "pais": bodega.pais,
                "createdAt": bodega.createdAt.isoformat(),
                "updatedAt": bodega.updatedAt.isoformat()
            }

            return result
        except Exception as e:
            db.rollback()
            return {"error": str(e)}, 500

    def safe_payload(self):
        try:
            schema = CreateBodegaSchema().load(self.data)
            return schema
        except ValidationError as e:
            raise InvalidBodegaData(e.messages)
