from sqlalchemy import Column, String

from src.db.base import Base
from .model import Model


class Ruta(Base, Model):
    __tablename__ = 'Ruta'
    nombre = Column(String, nullable=False)
    placa = Column(String, nullable=False)
    conductor = Column(String, nullable=False)