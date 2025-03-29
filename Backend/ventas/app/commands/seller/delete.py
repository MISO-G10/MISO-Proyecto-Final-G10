from app.commands.base_command import BaseCommand
from app.models.seller import Seller
from app.lib.database import db
from app.lib.errors import NotFoundError, ConflictError


class DeleteSellerCommand(BaseCommand):
    def __init__(self, seller_id):
        self.seller_id = seller_id
    
    def execute(self):
        seller = db.session.execute(
            db.select(Seller).where(Seller.seller_id == self.seller_id)
        ).scalar_one_or_none()
        
        if not seller:
            raise NotFoundError(f"Seller with ID {self.seller_id} not found")
        
        if seller.sales_plans:
            raise ConflictError(f"Cannot delete seller with ID {self.seller_id} as it is associated with sales plans")
        
        db.session.delete(seller)
        db.session.commit()
        
        return True