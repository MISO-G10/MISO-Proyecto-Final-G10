from app.commands.base_command import BaseCommand
from app.models.seller import Seller
from app.lib.errors import NotFoundError


class GetSellerCommand(BaseCommand):
    """
    Command to get a specific seller by ID.
    """
    
    def __init__(self, seller_id):
        """
        Initialize the command with the seller ID.
        
        Args:
            seller_id (int): The external ID of the seller to retrieve.
        """
        self.seller_id = seller_id
    
    def execute(self):
        """
        Execute the command to get a seller.
        
        Returns:
            Seller: The requested seller.
            
        Raises:
            NotFoundError: If the seller is not found.
        """
        # Get the seller
        seller = Seller.query.filter_by(seller_id=self.seller_id).first()
        if not seller:
            raise NotFoundError(f"Seller with ID {self.seller_id} not found")
        return seller


class GetAllSellersCommand(BaseCommand):
    """
    Command to get all sellers.
    """
    
    def execute(self):
        """
        Execute the command to get all sellers.
        
        Returns:
            list: A list of all sellers.
        """
        return Seller.query.all()