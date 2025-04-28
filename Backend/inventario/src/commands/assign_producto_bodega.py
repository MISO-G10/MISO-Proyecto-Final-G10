import uuid

from marshmallow import Schema, fields, ValidationError

from src.commands.base_command import BaseCommand
from src.commands.get_producto import GetProducto
from src.db.session import SessionLocal
from src.errors.errors import InvalidBodegaData
from src.models import Bodega, InventarioBodega


class AssignProductoSchema(Schema):
    producto_id = fields.Str(required=True)
    cantidad = fields.Int(required=True, validate=lambda n: n > 0)


class AssignProductoBodega(BaseCommand):
    def __init__(self, bodega_id, data):
        self.bodega_id = bodega_id
        self.data = data

    def execute(self):
        schema = self.safe_payload()
        db = SessionLocal()
        try:
            try:
                if isinstance(self.bodega_id, str):
                    bodega_id = uuid.UUID(self.bodega_id)
                else:
                    bodega_id = self.bodega_id
            except ValueError:
                return {"error": "ID de bodega inválido"}, 400

            bodega = db.query(Bodega).filter(Bodega.id == bodega_id).first()
            if not bodega:
                return {"error": f"Bodega con id {self.bodega_id} no encontrada"}, 404

            producto = GetProducto(schema['producto_id']).execute()
            if not producto:
                return {"error": f"Producto con id/sku {schema['producto_id']} no encontrado"}, 404

            existing = db.query(InventarioBodega).filter(
                InventarioBodega.bodega_id == bodega.id,
                InventarioBodega.producto_id == producto.id
            ).first()

            if existing:
                existing.cantidad = schema['cantidad']
            else:
                # Create new relationship
                inventario = InventarioBodega(
                    bodega_id=bodega.id,
                    producto_id=producto.id,
                    cantidad=schema['cantidad']
                )
                db.add(inventario)

            db.commit()

            # Return response with updated relationships
            productos_en_bodega = db.query(InventarioBodega).filter(
                InventarioBodega.bodega_id == bodega.id
            ).all()

            result = {
                "bodega_id": str(bodega.id),
                "nombre_bodega": bodega.nombre,
                "productos": [
                    {
                        "producto_id": str(item.producto_id),
                        "sku": item.producto.sku if hasattr(item.producto, 'sku') else None,
                        "nombre": item.producto.nombre if hasattr(item.producto, 'nombre') else None,
                        "cantidad": item.cantidad
                    } for item in productos_en_bodega
                ]
            }

            return result
        except Exception as e:
            db.rollback()
            return {"error": str(e)}, 500

    def safe_payload(self):
        try:
            schema = AssignProductoSchema().load(self.data)
            return schema
        except ValidationError as e:
            raise InvalidBodegaData(e.messages)
