from app.commands.base_command import BaseCommand
from app.models.seller import Seller
from app.lib.database import db
from app.lib.errors import NotFoundError, ConflictError


class DeleteSellerCommand(BaseCommand):
    """
    Command to delete a seller reference.
    """
    
    def __init__(self, seller_id):
        """
        Initialize the command with the seller ID.
        
        Args:
            seller_id (int): The external ID of the seller to delete.
        """
        self.seller_id = seller_id
    
    def execute(self):
        """
        Execute the command to delete a seller reference.
        
        Returns:
            bool: True if the seller was deleted successfully.
            
        Raises:
            NotFoundError: If the seller is not found.
            ConflictError: If the seller is associated with any sales plans.
        """
        # Get the seller
        seller = Seller.query.filter_by(seller_id=self.seller_id).first()
        if not seller:
            raise NotFoundError(f"Seller with ID {self.seller_id} not found")
        
        # Check if seller is associated with any sales plans
        if seller.sales_plans:
            raise ConflictError(f"Cannot delete seller with ID {self.seller_id} as it is associated with sales plans")
        
        # Delete the seller
        db.session.delete(seller)
        db.session.commit()
        
        return True