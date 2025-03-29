from app.commands.base_command import BaseCommand
from app.lib.database import db
from app.lib.errors import NotFoundError
from app.models.sales_plan import SalesPlan


class GetSalesPlanCommand(BaseCommand):
    """
    Command to get a specific sales plan by ID.
    """

    def __init__(self, sales_plan_id):
        """
        Initialize the command with the sales plan ID.

        Args:
            sales_plan_id (int): The ID of the sales plan to retrieve.
        """
        self.sales_plan_id = sales_plan_id

    def execute(self):
        """
        Execute the command to get a sales plan.

        Returns:
            SalesPlan: The requested sales plan.

        Raises:
            NotFoundError: If the sales plan is not found.
        """
        # Get the sales plan using session.get() as recommended in SQLAlchemy 2.0

        sales_plan = db.session.execute(
            db.select(SalesPlan).where(SalesPlan.id == self.sales_plan_id)
        ).scalar()

        if not sales_plan:
            raise NotFoundError(f"Sales plan with ID {self.sales_plan_id} not found")

        return sales_plan


class GetAllSalesPlansCommand(BaseCommand):
    """
    Command to get all sales plans.
    """

    def execute(self):
        """
        Execute the command to get all sales plans.

        Returns:
            list: A list of all sales plans.
        """
        return db.session.execute(
            db.select(SalesPlan)
        ).scalars()
