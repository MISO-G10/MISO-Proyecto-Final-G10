from app.commands.base_command import BaseCommand
from app.lib.database import db
from app.lib.errors import NotFoundError, BadRequestError
from app.lib.validators import validate_date_range
from app.models.sales_plan import SalesPlan


class UpdateSalesPlanCommand(BaseCommand):
    def __init__(self, sales_plan_id, data):
        self.sales_plan_id = sales_plan_id
        self.data = data

    def execute(self):
        sales_plan = db.session.get(SalesPlan, self.sales_plan_id)

        if not sales_plan:
            raise NotFoundError(f"Sales plan with ID {self.sales_plan_id} not found")

        if 'nombre' in self.data:
            sales_plan.nombre = self.data['nombre']

        if 'descripcion' in self.data:
            sales_plan.descripcion = self.data['descripcion']

        if 'valor_objetivo' in self.data:
            sales_plan.valor_objetivo = float(self.data['valor_objetivo'])

        if 'fecha_inicio' in self.data:
            sales_plan.fecha_inicio = self.data['fecha_inicio']

        if 'fecha_fin' in self.data:
            sales_plan.fecha_fin = self.data['fecha_fin']

        try:
            validate_date_range(sales_plan.fecha_inicio, sales_plan.fecha_fin)
        except ValueError as e:
            raise BadRequestError(str(e))

        # # Update sales plan, DO NOT UPDATE SELLERS
        # db.session.query(
        #     SalesPlan
        # ).filter(
        #     SalesPlan.id == sales_plan.id
        # ).update({
        #     "nombre": sales_plan.nombre,
        #     "descripcion": sales_plan.descripcion,
        #     "valor_objetivo": sales_plan.valor_objetivo,
        #     "fecha_inicio": sales_plan.fecha_inicio,
        #     "fecha_fin": sales_plan.fecha_fin
        # })

        db.session.commit()

        return sales_plan
