from sqlalchemy import Column, UUID, ForeignKey, String
from src.db.base import Base
from .model import Model

class AsignacionClienteTendero(Base, Model):
    __tablename__ = 'AsignacionClienteTendero'
    
    idVendedor = Column(UUID(as_uuid=True), nullable=False)
    idTendero = Column(UUID(as_uuid=True), nullable=False)
    estado = Column(String, nullable=False, default="ACTIVO")  # ACTIVO, INACTIVO
