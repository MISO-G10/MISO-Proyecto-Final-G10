from app.commands.base_command import BaseCommand
from app.models.sales_plan_seller import SalesPlanSeller
from app.lib.database import db
from app.lib.errors import NotFoundError


class GetSalesPlanSellerCommand(BaseCommand):
    def __init__(self, seller_id):
        self.seller_id = seller_id

    def execute(self):
        seller = db.session.get(SalesPlanSeller, self.seller_id)

        if not seller:
            raise NotFoundError(f"Seller with ID {self.seller_id} not found")

        return seller


class GetAllSalesPlanSellersCommand(BaseCommand):
    def execute(self):
        sellers = db.session.execute(
            db.select(SalesPlanSeller).order_by(SalesPlanSeller.id)
        ).scalars().all()

        return sellers