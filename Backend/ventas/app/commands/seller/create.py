from app.commands.base_command import BaseCommand
from app.models.seller import Seller
from app.lib.database import db
from app.lib.errors import ConflictError


class CreateSellerCommand(BaseCommand):
    """
    Command to create a new seller reference.
    """
    
    def __init__(self, data):
        """
        Initialize the command with the validated data.
        
        Args:
            data (dict): The validated data from the SellerCreateSchema.
        """
        self.data = data
    
    def execute(self):
        """
        Execute the command to create a new seller reference.
        
        Returns:
            Seller: The created seller.
            
        Raises:
            ConflictError: If a seller with the same seller_id already exists.
        """
        # Check if seller already exists
        existing_seller = Seller.query.filter_by(seller_id=self.data['seller_id']).first()
        if existing_seller:
            raise ConflictError(f"Seller with ID {self.data['seller_id']} already exists")
        
        # Create the seller
        seller = Seller(
            name=self.data['name'],
            seller_id=self.data['seller_id']
        )
        
        # Save to database
        db.session.add(seller)
        db.session.commit()
        
        return seller