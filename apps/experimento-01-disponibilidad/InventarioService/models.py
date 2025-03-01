from sqlalchemy import Column, Integer, String
from database import db  # Importa la instancia db creada en database.py

class Producto(db.Model):
    __tablename__ = 'productos'
    id = Column(Integer, primary_key=True, autoincrement=True)
    nombre = Column(String, nullable=False)
    stock = Column(Integer, default=0)