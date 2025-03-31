from app.commands.base_command import BaseCommand
from app.models.sales_plan_seller import SalesPlanSeller
from app.lib.database import db
from app.lib.errors import NotFoundError


class DeleteSalesPlanSellerCommand(BaseCommand):
    def __init__(self, seller_id):
        self.seller_id = seller_id

    def execute(self):
        seller = db.session.get(SalesPlanSeller, self.seller_id)

        if not seller:
            raise NotFoundError(f"Seller with ID {self.seller_id} not found")

        db.session.delete(seller)
        db.session.commit()

        return None