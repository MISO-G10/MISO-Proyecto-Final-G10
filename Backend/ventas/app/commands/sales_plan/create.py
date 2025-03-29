from app.commands.base_command import BaseCommand
from app.lib.database import db
from app.lib.errors import BadRequestError
from app.models.sales_plan import SalesPlan
from app.models.seller import Seller
from app.lib.validators import validate_date_range


class CreateSalesPlanCommand(BaseCommand):
    def __init__(self, data):
        self.data = data

    def execute(self):
        try:
            validate_date_range(self.data['start_date'], self.data['end_date'])
        except ValueError as e:
            raise BadRequestError(str(e))

        sales_plan = SalesPlan(
            name=self.data['name'],
            description=self.data['description'],
            target_amount=float(self.data['target_amount']),
            start_date=self.data['start_date'],
            end_date=self.data['end_date']
        )

        seller_ids = self.data.get('seller_ids', [])

        for seller_id in seller_ids:
            seller = db.session.execute(
                db.select(Seller).where(Seller.seller_id == seller_id)
            ).scalar_one_or_none()

            if not seller:
                # In a real implementation, make an authenticated API call to users microservice
                seller = Seller(
                    name=f"Seller {seller_id}",  # This would come from the users microservice
                    seller_id=seller_id
                )
                db.session.add(seller)

            sales_plan.sellers.append(seller)

        db.session.add(sales_plan)
        db.session.commit()

        return sales_plan
