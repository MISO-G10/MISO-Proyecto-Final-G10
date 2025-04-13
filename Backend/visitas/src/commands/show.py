from src.commands.base_command import BaseCommand
from src.db.session import SessionLocal
from src.errors.errors import VisitaNotFound, InvalidVisitaData
from src.models.visita import Visita
from src.utils.helpers import serialize_sqlalchemy, is_valid_uuid


class Show(BaseCommand):
    def __init__(self, visita_id):
        self.id = visita_id

    def execute(self):
        if not is_valid_uuid(str(self.id)):
            raise InvalidVisitaData()

        db = SessionLocal()
        visita = db.query(Visita).filter(Visita.id == self.id).first()

        if not visita:
            raise VisitaNotFound()

        return serialize_sqlalchemy(visita)
