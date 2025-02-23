from sqlalchemy import Integer, String

from app.lib.database import db
from .model import Model
from sqlalchemy.orm import mapped_column, Mapped


class User(db.Model, Model):
    __tablename__ = 'users'

    id: Mapped[int] = mapped_column(Integer, primary_key=True)
    name: Mapped[str] = mapped_column(String(255))
    email: Mapped[str] = mapped_column(String(255))
