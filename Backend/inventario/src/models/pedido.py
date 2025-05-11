from sqlalchemy import Column, String,Enum,Float,DateTime,ForeignKey
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import relationship
import enum
from src.db.base import Base
from .model import Model
from .producto import Producto

class EstadoPedido(enum.Enum):
    PENDIENTE = "PENDIENTE"
    ENVIADO = "ENVIADO"
    ENTREGADO = "ENTREGADO"

class Pedido(Base, Model):
    __tablename__ = 'Pedido'
    usuario_id = Column(UUID(as_uuid=True), nullable=False)    
    fechaEntrega = Column(DateTime, nullable=True)
    fechaSalida = Column(DateTime, nullable=True)    
    estado = Column(Enum(EstadoPedido), nullable=False)
    valor= Column(Float, nullable=False)
    pedido_productos = None

class PedidoProducto(Base, Model):
    __tablename__ = 'PedidoProducto'    
    cantidad = Column(Float, nullable=False)
    pedido_id = Column(UUID(as_uuid=True), ForeignKey("Pedido.id"), primary_key=True)
    producto_id = Column(UUID(as_uuid=True), ForeignKey("Producto.id"), primary_key=True)    
    valorUnitario = Column(Float, nullable=False)
    subtotal = Column(Float, nullable=False)

    pedido = relationship("Pedido", back_populates="pedido_productos")
    producto = relationship("Producto", back_populates="pedido_productos")

Producto.pedido_productos = relationship("PedidoProducto", back_populates="producto")
Pedido.pedido_productos = relationship("PedidoProducto", back_populates="pedido")