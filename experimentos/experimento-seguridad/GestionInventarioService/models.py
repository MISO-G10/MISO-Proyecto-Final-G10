from sqlalchemy import Column, Integer, String, ForeignKey
from sqlalchemy.orm import relationship
from database import db 


class Producto(db.Model):
    __tablename__ = 'productos'

    id = Column(Integer, primary_key=True)
    sku = Column(String(255), unique=True, nullable=False)
    nombre = Column(String(255), nullable=False)
    descripcion = Column(String(255))

    # Relación con InventarioBodega
    inventarios = relationship('InventarioBodega', back_populates='producto')

class InventarioBodega(db.Model):
    __tablename__ = 'inventario_bodega'

    id = Column(Integer, primary_key=True)
    producto_id = Column(Integer, ForeignKey('productos.id'), nullable=False)
    cantidad = Column(Integer, nullable=False, default=0)
    ubicacion = Column(String(255))

    # Relación con Producto
    producto = relationship('Producto', back_populates='inventarios')
