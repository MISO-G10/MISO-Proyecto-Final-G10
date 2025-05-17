from app.commands.base_command import BaseCommand
from app.lib.database import db
from app.lib.errors import BadRequestError
from app.lib.validators import validate_date_range
from app.models.sales_plan import SalesPlan


class CreateSalesPlanCommand(BaseCommand):
    def __init__(self, data):
        self.data = data

    def execute(self):
        try:
            validate_date_range(self.data['fecha_inicio'], self.data['fecha_fin'])
        except ValueError as e:
            raise BadRequestError(str(e))

        sales_plans = []
        seller_ids = self.data.get('seller_ids', [])

        for seller_id in seller_ids:
            sales_plan = SalesPlan(
                usuario_id=seller_id,
                nombre=self.data['nombre'],
                descripcion=self.data['descripcion'],
                valor_objetivo=float(self.data['valor_objetivo']),
                fecha_inicio=self.data['fecha_inicio'],
                fecha_fin=self.data['fecha_fin']
            )

            db.session.add(sales_plan)
            db.session.commit()

            sales_plans.append(sales_plan)

        return sales_plans
