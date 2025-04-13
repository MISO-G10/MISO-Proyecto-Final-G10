from sqlalchemy import Column, String, UUID
from src.db.base import Base
from .model import Model

class Visita(Base, Model):
    __tablename__ = 'Visita'
    
    fecha = Column(String, nullable=False)
    horaDesde = Column(String, nullable=False)
    horaHasta = Column(String, nullable=False)
    comentarios = Column(String, nullable=False)
    idUsuario = Column(UUID(as_uuid=True), nullable=False)
    