from sqlalchemy import Column, Integer, ForeignKey
from sqlalchemy.orm import relationship
from sqlalchemy.dialects.postgresql import UUID

from src.db.base import Base
from .bodega import Bodega
from .model import Model
from .producto import Producto


class InventarioBodega(Base, Model):
    __tablename__ = "InventarioBodega"

    bodega_id = Column(UUID(as_uuid=True), ForeignKey("Bodega.id"), primary_key=True)
    producto_id = Column(UUID(as_uuid=True), ForeignKey("Producto.id"), primary_key=True)
    cantidad = Column(Integer, default=0)

    bodega = relationship("Bodega", back_populates="bodega_productos")
    producto = relationship("Producto", back_populates="bodega_productos")


Producto.bodega_productos = relationship("InventarioBodega", back_populates="producto")
Bodega.bodega_productos = relationship("InventarioBodega", back_populates="bodega")
