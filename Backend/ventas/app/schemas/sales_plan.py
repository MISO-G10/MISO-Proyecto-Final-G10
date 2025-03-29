from marshmallow import fields, validates, ValidationError, validate, post_load
from datetime import datetime

from app.lib.schema import marshmallow
from app.models.sales_plan import SalesPlan
from app.schemas.seller import SellerSchema


class SalesPlanCreateSchema(marshmallow.Schema):
    """Schema for creating a new SalesPlan with validation rules"""

    name = fields.String(required=True, validate=validate.Length(min=3, max=255))
    description = fields.String(required=True, validate=validate.Length(min=3, max=500))
    target_amount = fields.Float(required=True, validate=validate.Range(min=0.01))
    start_date = fields.Date(required=True, format='%Y-%m-%d')
    end_date = fields.Date(required=True, format='%Y-%m-%d')
    seller_ids = fields.List(fields.Integer(), required=True, validate=validate.Length(min=1))


class SalesPlanUpdateSchema(marshmallow.Schema):
    """Schema for updating an existing SalesPlan with validation rules"""

    name = fields.String(validate=validate.Length(min=3, max=255))
    description = fields.String(validate=validate.Length(min=3, max=500))
    target_amount = fields.Float(validate=validate.Range(min=0.01))
    start_date = fields.Date(format='%Y-%m-%d')
    end_date = fields.Date(format='%Y-%m-%d')
    seller_ids = fields.List(fields.Integer(), validate=validate.Length(min=1))


class SalesPlanSchema(marshmallow.SQLAlchemyAutoSchema):
    """Schema for serializing a SalesPlan model"""

    sellers = fields.Nested(SellerSchema, many=True)

    class Meta:
        model = SalesPlan
        include_fk = True
        load_instance = True

    @validates('end_date')
    def validate_end_date(self, end_date):
        """Validate that end_date is after start_date"""
        start_date = self.context.get('start_date')
        if start_date and end_date < start_date:
            raise ValidationError("End date must be after start date.")

    @validates('target_amount')
    def validate_target_amount(self, amount):
        """Validate that target_amount is positive"""
        if amount <= 0:
            raise ValidationError("Target amount must be positive.")
