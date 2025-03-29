from app.commands.base_command import BaseCommand
from app.lib.database import db
from app.lib.errors import BadRequestError
from app.models.sales_plan import SalesPlan
from app.models.seller import Seller


class CreateSalesPlanCommand(BaseCommand):
    """
    Command to create a new sales plan.
    """

    def __init__(self, data):
        """
        Initialize the command with the validated data.

        Args:
            data (dict): The validated data from the SalesPlanCreateSchema.
        """
        self.data = data

    def execute(self):
        """
        Execute the command to create a new sales plan.

        Returns:
            SalesPlan: The created sales plan.

        Raises:
            BadRequestError: If the data is invalid.
        """
        # Validate dates
        if self.data['end_date'] <= self.data['start_date']:
            raise BadRequestError("End date must be after start date")

        # Create the sales plan
        sales_plan = SalesPlan(
            name=self.data['name'],
            description=self.data['description'],
            target_amount=float(self.data['target_amount']),
            start_date=self.data['start_date'],
            end_date=self.data['end_date']
        )

        # Process sellers
        seller_ids = self.data.get('seller_ids', [])

        # Add sellers to the sales plan
        for seller_id in seller_ids:
            # Check if seller exists in our local reference table

            seller = db.session.execute(
                db.select(Seller).where(Seller.seller_id == seller_id)
            ).scalar()

            # If not, create a new seller reference
            if not seller:
                # In a real implementation, make an authenticated API call to users microservice
                # For now, let's just create a placeholder seller
                seller = Seller(
                    name=f"Seller {seller_id}",  # This would come from the users microservice
                    seller_id=seller_id
                )
                db.session.add(seller)

            # Add seller to sales plan
            sales_plan.sellers.append(seller)

        # Save to database
        db.session.add(sales_plan)
        db.session.commit()

        return sales_plan
