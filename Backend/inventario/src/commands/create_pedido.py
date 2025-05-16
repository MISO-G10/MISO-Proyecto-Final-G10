from marshmallow import Schema, fields, ValidationError, validates_schema
from flask import jsonify
from src.db.session import SessionLocal
from src.models.pedido import Pedido, PedidoProducto, EstadoPedido
from src.models.producto import Producto
from src.errors.errors import InvalidPedidoData
from .base_command import BaseCommand
from datetime import datetime
import uuid


class PedidoProductoInputSchema(Schema):    
    producto_id = fields.UUID(required=True)
    cantidad = fields.Float(required=True)

class CreatePedidoSchema(Schema):
    usuario_id= fields.UUID(required=False)
    fechaEntrega = fields.DateTime(required=False)
    productos = fields.List(fields.Nested(PedidoProductoInputSchema), required=True)

    @validates_schema
    def validate_productos(self, data, **kwargs):
        if not data.get("productos"):
            raise ValidationError("Debe incluir al menos un producto en el pedido.")


class CreatePedido(BaseCommand):
    def __init__(self, vendedor, data):
        self.vendedor = vendedor
        self.data = data

    def execute(self):
        schema = self.safe_payload()
        db = SessionLocal()

        try:
            productos_data = schema["productos"]
            total_valor = 0
            pedido_productos = []

            for item in productos_data:
                producto = db.query(Producto).filter_by(id=item["producto_id"]).first()
                if not producto:
                    return {"error": f"Producto con ID {item['producto_id']} no encontrado"}, 404

                subtotal = item["cantidad"] * producto.valorUnidad
                total_valor += subtotal

                pedido_producto = PedidoProducto(
                    cantidad=item["cantidad"],
                    producto_id=producto.id,
                    valorUnitario=producto.valorUnidad,
                    subtotal=subtotal
                )
                pedido_productos.append(pedido_producto)

            nuevo_pedido = Pedido(
                usuario_id = self.vendedor["id"] if not self.data.get("usuario_id") else self.data["usuario_id"],
                vendedor_id=self.vendedor["id"],                
                estado=EstadoPedido.PENDIENTE,
                valor=total_valor
            )

            db.add(nuevo_pedido)
            db.flush()  # Para obtener el ID antes de asignar en PedidoProducto

            for pp in pedido_productos:
                pp.pedido_id = nuevo_pedido.id
                db.add(pp)

            db.commit()

            return {
                "id": nuevo_pedido.id,
                "estado": nuevo_pedido.estado.value,
                "valorTotal": nuevo_pedido.valor,
                "fechaSalida": nuevo_pedido.fechaSalida,
                "fechaEntrega": nuevo_pedido.fechaEntrega,
                "vendedor_id": nuevo_pedido.vendedor_id,
                "usuario_id": nuevo_pedido.usuario_id,
                "productos": [
                    {
                        "producto_id": pp.producto_id,
                        "cantidad": pp.cantidad,
                        "valorUnitario": pp.valorUnitario,
                        "subtotal": pp.subtotal
                    } for pp in pedido_productos
                ]
            }

        except Exception as e:
            db.rollback()
            raise e

    def safe_payload(self):
        try:
            return CreatePedidoSchema().load(self.data)
        except ValidationError as e:
            raise InvalidPedidoData(e.messages)
