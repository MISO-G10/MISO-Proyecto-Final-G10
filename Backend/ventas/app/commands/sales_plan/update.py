from app.commands.base_command import BaseCommand
from app.models.sales_plan import SalesPlan
from app.models.seller import Seller
from app.lib.database import db
from app.lib.errors import NotFoundError, BadRequestError
from app.lib.validators import validate_date_range


class UpdateSalesPlanCommand(BaseCommand):
    def __init__(self, sales_plan_id, data):
        self.sales_plan_id = sales_plan_id
        self.data = data

    def execute(self):
        sales_plan = db.session.get(SalesPlan, self.sales_plan_id)

        if not sales_plan:
            raise NotFoundError(f"Sales plan with ID {self.sales_plan_id} not found")

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
        
        try:
            validate_date_range(sales_plan.start_date, sales_plan.end_date)
        except ValueError as e:
            raise BadRequestError(str(e))

        if 'seller_ids' in self.data:
            seller_ids = self.data['seller_ids']
            sales_plan.sellers = []

            for seller_id in seller_ids:
                seller = db.session.execute(
                    db.select(Seller).where(Seller.seller_id == seller_id)
                ).scalar_one_or_none()

                if not seller:
                    seller = Seller(
                        name=f"Seller {seller_id}",
                        seller_id=seller_id
                    )
                    db.session.add(seller)

                sales_plan.sellers.append(seller)

        db.session.commit()

        return sales_plan
