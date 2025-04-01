from sqlalchemy import Column, String, Boolean, DateTime, Float
from src.db.base import Base
from .model import Model

class Categoria(enum.Enum):
    PERECEDERO = 'PERECEDERO'
    NO_PERECEDERO = 'NO_PERECEDERO'

class Producto(Base, Model):
    __tablename__ = 'Producto'
    
    nombre = Column(String, nullable=False)
    descripcion = Column(String, nullable=False)
    perecedero = Column(Boolean, nullable=False)
    fechaVencimiento = Column(DateTime, nullable=False)
    valorUnidad = Column(Float, nullable=False)
    tiempoEntrega = Column(DateTime, nullable=False)
    condicionAlmacenamiento = Column(String, nullable=False)
    reglasLegales = Column(String, nullable=False)
    reglasComerciales = Column(String, nullable=False)
    reglasTributarias = Column(String, nullable=False)
    categoria = Column(db.Enum(Categoria), nullable=False)

    