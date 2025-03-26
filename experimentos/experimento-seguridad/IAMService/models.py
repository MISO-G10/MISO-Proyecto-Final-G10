from sqlalchemy import Column, Integer, String
from sqlalchemy.orm import mapped_column, Mapped
from database import db 

class User(db.Model):
    __tablename__ = 'users'

    id = Column(Integer, primary_key=True)
    uuid = Column(String(255))
    name = Column(String(255))
    username = Column(String(255), unique=True)
    password = Column(String(255))
    accesslevel = Column(String(255))  # El conjunto de reglas de acceso de acuerdo al rol del usuario

