from app.commands.base_command import BaseCommand
from app.models.sales_plan import SalesPlan
from app.lib.database import db
from app.lib.errors import NotFoundError


class DeleteSalesPlanCommand(BaseCommand):
    """
    Command to delete a sales plan.
    """

    def __init__(self, sales_plan_id):
        """
        Initialize the command with the sales plan ID.

        Args:
            sales_plan_id (int): The ID of the sales plan to delete.
        """
        self.sales_plan_id = sales_plan_id

    def execute(self):
        """
        Execute the command to delete a sales plan.

        Returns:
            bool: True if the sales plan was deleted successfully.

        Raises:
            NotFoundError: If the sales plan is not found.
        """
        # Get the sales plan using session.get() as recommended in SQLAlchemy 2.0
        sales_plan = db.session.execute(
            db.select(SalesPlan).where(SalesPlan.id == self.sales_plan_id)
        ).scalar()

        if not sales_plan:
            raise NotFoundError(f"Sales plan with ID {self.sales_plan_id} not found")

        # Delete the sales plan
        db.session.delete(sales_plan)
        db.session.commit()

        return True
