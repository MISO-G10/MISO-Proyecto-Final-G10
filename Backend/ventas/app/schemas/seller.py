from marshmallow import fields, validates, ValidationError, validate
from app.lib.schema import marshmallow
from app.models.seller import Seller


class SellerCreateSchema(marshmallow.Schema):
    """Schema for creating a new Seller with validation rules"""
    
    name = fields.String(required=True, validate=validate.Length(min=2, max=255))
    seller_id = fields.Integer(required=True, validate=validate.Range(min=1))


class SellerUpdateSchema(marshmallow.Schema):
    """Schema for updating an existing Seller with validation rules"""
    
    name = fields.String(validate=validate.Length(min=2, max=255))


class SellerSchema(marshmallow.SQLAlchemyAutoSchema):
    """Schema for serializing a Seller model"""
    
    class Meta:
        model = Seller
        include_fk = True
        load_instance = True