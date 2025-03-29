from app.commands.base_command import BaseCommand
from app.models.sales_plan import SalesPlan
from app.models.seller import Seller
from app.lib.database import db
from app.lib.errors import NotFoundError, BadRequestError


class UpdateSalesPlanCommand(BaseCommand):
    """
    Command to update an existing sales plan.
    """

    def __init__(self, sales_plan_id, data):
        """
        Initialize the command with the sales plan ID and validated data.

        Args:
            sales_plan_id (int): The ID of the sales plan to update.
            data (dict): The validated data from the SalesPlanUpdateSchema.
        """
        self.sales_plan_id = sales_plan_id
        self.data = data

    def execute(self):
        """
        Execute the command to update an existing sales plan.

        Returns:
            SalesPlan: The updated sales plan.

        Raises:
            NotFoundError: If the sales plan is not found.
            BadRequestError: If the update data is invalid.
        """
        # Get the sales plan using session.get() as recommended in SQLAlchemy 2.0
        sales_plan = db.session.execute(
            db.select(SalesPlan).where(SalesPlan.id == self.sales_plan_id)
        ).scalar()

        if not sales_plan:
            raise NotFoundError(f"Sales plan with ID {self.sales_plan_id} not found")

        # Update basic fields
        if 'name' in self.data:
            sales_plan.name = self.data['name']

        if 'description' in self.data:
            sales_plan.description = self.data['description']

        if 'target_amount' in self.data:
            sales_plan.target_amount = float(self.data['target_amount'])

        if 'start_date' in self.data:
            sales_plan.start_date = self.data['start_date']

        if 'end_date' in self.data:
            sales_plan.end_date = self.data['end_date']

        # Validate that end_date is after start_date
        if sales_plan.end_date <= sales_plan.start_date:
            raise BadRequestError("End date must be after start date")

        # Update sellers if provided
        if 'seller_ids' in self.data:
            seller_ids = self.data['seller_ids']

            # Clear existing sellers and add new ones
            sales_plan.sellers = []

            for seller_id in seller_ids:
                seller = db.session.execute(
                    db.select(Seller).where(Seller.seller_id == seller_id)
                ).scalar()

                if not seller:
                    # In a real implementation, fetch from users microservice
                    seller = Seller(
                        name=f"Seller {seller_id}",
                        seller_id=seller_id
                    )
                    db.session.add(seller)

                sales_plan.sellers.append(seller)

        db.session.commit()

        return sales_plan
