from app.commands.base_command import BaseCommand
from app.models.seller import Seller
from app.lib.database import db
from app.lib.errors import NotFoundError


class UpdateSellerCommand(BaseCommand):
    """
    Command to update an existing seller reference.
    """
    
    def __init__(self, seller_id, data):
        """
        Initialize the command with the seller ID and validated data.
        
        Args:
            seller_id (int): The external ID of the seller to update.
            data (dict): The validated data from the SellerUpdateSchema.
        """
        self.seller_id = seller_id
        self.data = data
    
    def execute(self):
        """
        Execute the command to update a seller reference.
        
        Returns:
            Seller: The updated seller.
            
        Raises:
            NotFoundError: If the seller is not found.
        """
        # Get the seller
        seller = Seller.query.filter_by(seller_id=self.seller_id).first()
        if not seller:
            raise NotFoundError(f"Seller with ID {self.seller_id} not found")
        
        # Update fields
        if 'name' in self.data:
            seller.name = self.data['name']
        
        db.session.commit()
        
        return seller