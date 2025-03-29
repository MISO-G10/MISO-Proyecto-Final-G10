from app.commands.base_command import BaseCommand
from app.models.seller import Seller
from app.lib.database import db
from app.lib.errors import ConflictError


class CreateSellerCommand(BaseCommand):
    def __init__(self, data):
        self.data = data

    def execute(self):
        existing_seller = db.session.execute(
            db.select(Seller).where(Seller.seller_id == self.data['seller_id'])
        ).scalar_one_or_none()

        if existing_seller:
            raise ConflictError(f"Seller with ID {self.data['seller_id']} already exists")

        seller = Seller(
            name=self.data['name'],
            seller_id=self.data['seller_id']
        )

        db.session.add(seller)
        db.session.commit()

        return seller
