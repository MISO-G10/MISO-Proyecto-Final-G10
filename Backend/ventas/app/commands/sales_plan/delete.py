from app.commands.base_command import BaseCommand
from app.models.sales_plan import SalesPlan
from app.lib.database import db
from app.lib.errors import NotFoundError


class DeleteSalesPlanCommand(BaseCommand):
    def __init__(self, sales_plan_id):
        self.sales_plan_id = sales_plan_id

    def execute(self):
        sales_plan = db.session.get(SalesPlan, self.sales_plan_id)

        if not sales_plan:
            raise NotFoundError(f"Sales plan with ID {self.sales_plan_id} not found")

        db.session.delete(sales_plan)
        db.session.commit()

        return True
