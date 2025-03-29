from app.commands.base_command import BaseCommand
from app.models.seller import Seller
from app.lib.errors import NotFoundError
from app.lib.database import db


class GetSellerCommand(BaseCommand):
    def __init__(self, seller_id):
        self.seller_id = seller_id
    
    def execute(self):
        seller = db.session.execute(
            db.select(Seller).where(Seller.seller_id == self.seller_id)
        ).scalar_one_or_none()
        
        if not seller:
            raise NotFoundError(f"Seller with ID {self.seller_id} not found")
        
        return seller


class GetAllSellersCommand(BaseCommand):
    def execute(self):
        return db.session.execute(db.select(Seller)).scalars()