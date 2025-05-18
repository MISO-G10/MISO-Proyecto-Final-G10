from marshmallow import Schema, fields, ValidationError, validates_schema
from flask import jsonify
from src.db.session import SessionLocal
from src.models.pedido import Pedido, PedidoProducto, EstadoPedido
from src.models.producto import Producto
from src.errors.errors import InvalidPedidoData
from .base_command import BaseCommand
from datetime import datetime,timedelta
from src.models.inventario_bodega import InventarioBodega
from sqlalchemy.exc import SQLAlchemyError
from sqlalchemy import func
import uuid
import random
from ..utils.config import get_config
import os
import requests
class PedidoProductoInputSchema(Schema):    
    producto_id = fields.UUID(required=True)
    cantidad = fields.Float(required=True)

class CreatePedidoSchema(Schema):
    usuario_id= fields.UUID(required=False)
    direccion= fields.Str(required=True)
    fechaEntrega = fields.DateTime(required=False)
    productos = fields.List(fields.Nested(PedidoProductoInputSchema), required=True)

    @validates_schema
    def validate_productos(self, data, **kwargs):
        if not data.get("productos"):
            raise ValidationError("Debe incluir al menos un producto en el pedido.")


class CreatePedido(BaseCommand):
    def __init__(self, vendedor, data,token):
        self.vendedor = vendedor
        self.data = data
        self.token = token

    def execute(self):
        schema = self.safe_payload()
        db = SessionLocal()

        try:
            productos_data = schema["productos"]
            total_valor = 0
            pedido_productos = []
            
            #Verificar stock de los productos
            for item in productos_data:
                producto_id = item["producto_id"]
                cantidad_solicitada = item["cantidad"]

                # Verificar si hay suficiente stock en el total de las bodegas
                inventario_total = db.query(
                    func.sum(InventarioBodega.cantidad)
                ).filter(
                    InventarioBodega.producto_id == producto_id
                ).scalar() or 0

                if inventario_total < cantidad_solicitada:
                    return {
                        "error": f"No hay suficiente stock para el producto con ID {producto_id}. Disponible: {inventario_total}, Solicitado: {cantidad_solicitada}"
                    }, 400
            fecha_entrega = datetime.now() + timedelta(days=random.randint(1, 5))
            fecha_salida = datetime.now() + timedelta(days=random.randint(0, (fecha_entrega - datetime.now()).days - 1))
            vendedor_id = self.vendedor["id"]
            if self.data.get("usuario_id") and self.data["usuario_id"] == vendedor_id:
                # Significa que no es el verdadero vendedor
                vendedor_id = self.obtener_vendedor_real()
            usuario_id=self.vendedor["id"] if not self.data.get("usuario_id") else self.data["usuario_id"]
            print(vendedor_id, usuario_id)
            nuevo_pedido = Pedido(
                direccion=self.data["direccion"],
                usuario_id=usuario_id,
                vendedor_id=vendedor_id,
                fechaEntrega=fecha_entrega,
                fechaSalida=fecha_salida,
                estado=EstadoPedido.PENDIENTE,
                valor=0  # Se calcula más abajo
            )

            db.add(nuevo_pedido)
            db.flush()  # para obtener el ID

            for item in productos_data:
                producto = db.query(Producto).filter_by(id=item["producto_id"]).first()
                cantidad_solicitada = item["cantidad"]
                subtotal = cantidad_solicitada * producto.valorUnidad
                total_valor += subtotal

                # Crear relación PedidoProducto
                pedido_producto = PedidoProducto(
                    cantidad=cantidad_solicitada,
                    pedido_id=nuevo_pedido.id,
                    producto_id=producto.id,
                    valorUnitario=producto.valorUnidad,
                    subtotal=subtotal
                )
                db.add(pedido_producto)

                # Descontar del inventario
                inventarios = db.query(InventarioBodega).filter(
                    InventarioBodega.producto_id == producto.id,
                    InventarioBodega.cantidad > 0
                ).order_by(InventarioBodega.cantidad.desc()).all()

                for inv in inventarios:
                    if cantidad_solicitada == 0:
                        break
                    if inv.cantidad >= cantidad_solicitada:
                        inv.cantidad -= cantidad_solicitada
                        cantidad_solicitada = 0
                    else:
                        cantidad_solicitada -= inv.cantidad
                        inv.cantidad = 0

            nuevo_pedido.valor = total_valor
            db.commit()

            return {
                "id": nuevo_pedido.id,
                "estado": nuevo_pedido.estado.value,
                "valorTotal": nuevo_pedido.valor,
                "fechaSalida": nuevo_pedido.fechaSalida,
                "fechaEntrega": nuevo_pedido.fechaEntrega,
                "vendedor_id": nuevo_pedido.vendedor_id,
                "usuario_id": nuevo_pedido.usuario_id,
                "direccion": nuevo_pedido.direccion,
                "productos": [
                    {
                        "producto_id": pp.producto_id,
                        "cantidad": pp.cantidad,
                        "valorUnitario": pp.valorUnitario,
                        "subtotal": pp.subtotal
                    } for pp in nuevo_pedido.pedido_productos
                ]
            }

        except SQLAlchemyError as e:
            db.rollback()
            return {"error": str(e)}, 500

    def safe_payload(self):
        try:
            return CreatePedidoSchema().load(self.data)
        except ValidationError as e:
            raise InvalidPedidoData(e.messages)
    def obtener_vendedor_real(self):    

        env_name = os.getenv('FLASK_ENV', 'development')
        envData = get_config(env_name)
        visitas_path = envData.VISITAS_PATH + "/visitas/asignaciones/vendedor"

        try:
            headers = {"Authorization": f"Bearer {self.token}"}
            response = requests.get(visitas_path, headers=headers)

            if response.status_code == 200:
                data = response.json()
                return data.get("idVendedor")  # Asegúrate que el micro devuelva {"id": ...}
            else:
                raise Exception(f"Error al consultar vendedor: {response.text}")
        except Exception as e:
            raise Exception(f"Error conectando con servicio de visitas: {e}")

