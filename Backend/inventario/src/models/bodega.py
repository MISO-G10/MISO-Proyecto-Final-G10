from sqlalchemy import Column, String

from src.db.base import Base
from .model import Model


class Bodega(Base, Model):
    __tablename__ = 'Bodega'
    nombre = Column(String, nullable=False)
    direccion = Column(String, nullable=False)
    ciudad = Column(String, nullable=False)
    pais = Column(String, nullable=False)

    bodega_productos = None

    @property
    def productos(self):
        return [wp.producto for wp in self.bodega_productos]
