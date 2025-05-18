from sqlalchemy import and_
from datetime import datetime

from src.db.session import SessionLocal
from src.models.pedido import Pedido, PedidoProducto
from src.models.producto import Producto
from .base_command import BaseCommand
from sqlalchemy.orm import joinedload
from src.errors.errors import InvalidPedidoData
from src.utils.helpers import serialize_sqlalchemy


class GetPedido(BaseCommand):
    def __init__(self, pedido_id=None, user_id=None, tipo_usuario=None, fecha_entrega=None):
        """Inicializa el comando GetPedido con filtros opcionales.
        Args:
            pedido_id (str, optional): ID del pedido a buscar
            user_id (str, optional): ID del usuario
            tipo_usuario (str, optional): Tipo de usuario ('v' para vendedor, 't' para tendero)
            fecha_entrega (str, optional): Fecha de entrega en formato YYYY-MM-DD
        """
        self.pedido_id = pedido_id
        self.user_id = user_id
        self.tipo_usuario = tipo_usuario
        self.fecha_entrega = fecha_entrega

    def execute(self):
        db = SessionLocal()
        try:
        
            query = db.query(Pedido).options(
                joinedload(Pedido.pedido_productos).joinedload(PedidoProducto.producto)
            )
            filters = []

            # Filtrar por ID de pedido si se proporciona
            if self.pedido_id:
                filters.append(Pedido.id == self.pedido_id)

            # Filtrar por ID de usuario según el tipo
            if self.user_id and self.tipo_usuario:
                if self.tipo_usuario.lower() == 'v':  # Vendedor
                    filters.append(Pedido.vendedor_id == self.user_id)
                elif self.tipo_usuario.lower() == 't':  # Tendero
                    filters.append(Pedido.usuario_id == self.user_id)

            # Filtrar por fecha de entrega
            if self.fecha_entrega:
                try:
                    fecha = datetime.strptime(self.fecha_entrega, '%Y-%m-%d')
                    filters.append(Pedido.fechaEntrega <= fecha)
                except ValueError:
                    print(f"Error: Formato de fecha inválido. Use YYYY-MM-DD")

            # Aplicar todos los filtros
            if filters:
                query = query.filter(and_(*filters))

            # Si es búsqueda por ID específico, retornar solo el primer resultado
            if self.pedido_id:
                pedido = query.first()
                if not pedido:
                    return {"error": f"Pedido con ID {self.pedido_id} no encontrado"}, 404
                return serialize_sqlalchemy(pedido)
            
            # Si no hay búsqueda por ID, retornar todos los pedidos
            pedidos = query.all()
            return [serialize_sqlalchemy(pedido) for pedido in pedidos]

        except Exception as e:
            db.rollback()
            raise InvalidPedidoData(str(e))
        finally:
            db.close()
