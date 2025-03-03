from sqlalchemy import Integer, String
from sqlalchemy.orm import mapped_column, Mapped

from app.lib.database import db
from .model import Model


class User(db.Model, Model):
    __tablename__ = 'users'

    id: Mapped[int] = mapped_column(Integer, primary_key=True)
    uuid: Mapped[int] = mapped_column(String(255))
    name: Mapped[str] = mapped_column(String(255))
    username: Mapped[str] = mapped_column(String(255))
    password: Mapped[str] = mapped_column(String(255))
    accesslevel: Mapped[str] = mapped_column(String(255)) #Almacena las reglas de acceso para cada usuario
