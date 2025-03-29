from app.commands.base_command import BaseCommand
from app.models.seller import Seller
from app.lib.database import db
from app.lib.errors import NotFoundError


class UpdateSellerCommand(BaseCommand):
    def __init__(self, seller_id, data):
        self.seller_id = seller_id
        self.data = data
    
    def execute(self):
        seller = db.session.execute(
            db.select(Seller).where(Seller.seller_id == self.seller_id)
        ).scalar_one_or_none()
        
        if not seller:
            raise NotFoundError(f"Seller with ID {self.seller_id} not found")
        
        if 'name' in self.data:
            seller.name = self.data['name']
        
        db.session.commit()
        
        return seller