from src.db.session import SessionLocal
from src.models.pedido import Pedido, PedidoProducto
from src.models.producto import Producto
from .base_command import BaseCommand
from sqlalchemy.orm import joinedload
from src.errors.errors import InvalidPedidoData
from src.utils.helpers import serialize_sqlalchemy


class GetPedido(BaseCommand):
    def __init__(self, pedido_id):
        self.pedido_id = pedido_id

    def execute(self):
        db = SessionLocal()
        try:
            pedido = db.query(Pedido).filter(
                Pedido.id == self.pedido_id
            ).options(
                joinedload(Pedido.pedido_productos).joinedload(PedidoProducto.producto)
            ).first()

            if not pedido:
                return {"error": f"Pedido con ID {self.pedido_id} no encontrado"}, 404

            return serialize_sqlalchemy(pedido)

        except Exception as e:
            db.rollback()
            raise InvalidPedidoData(str(e))
        finally:
            db.close()
