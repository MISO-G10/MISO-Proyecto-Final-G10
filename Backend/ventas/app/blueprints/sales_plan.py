from flask import request, jsonify

from app.lib.auth import validate_token, director_required
from app.lib.errors import BadRequestError, NotFoundError
from app.commands.sales_plan.create import CreateSalesPlanCommand
from app.commands.sales_plan.update import UpdateSalesPlanCommand
from app.commands.sales_plan.delete import DeleteSalesPlanCommand
from app.commands.sales_plan.get import GetSalesPlanCommand, GetAllSalesPlansCommand
from app.schemas.sales_plan import SalesPlanSchema, SalesPlanCreateSchema, SalesPlanUpdateSchema
from . import api


@api.route('/sales-plans', methods=['GET'])
@validate_token
def get_sales_plans():
    """Get all sales plans"""
    # Execute the command to get all sales plans
    sales_plans = GetAllSalesPlansCommand().execute()
    
    # Serialize the result
    schema = SalesPlanSchema(many=True)
    return jsonify(schema.dump(sales_plans))


@api.route('/sales-plans/<int:id>', methods=['GET'])
@validate_token
def get_sales_plan(id):
    """Get a specific sales plan by ID"""
    # Execute the command to get the sales plan
    sales_plan = GetSalesPlanCommand(id).execute()
    
    # Serialize the result
    schema = SalesPlanSchema()
    return jsonify(schema.dump(sales_plan))


@api.route('/sales-plans', methods=['POST'])
@validate_token
@director_required
def create_sales_plan():
    """Create a new sales plan - requires Director role"""
    # Get request data
    data = request.get_json()
    if not data:
        raise BadRequestError("No input data provided")
    
    # Validate the request data
    schema = SalesPlanCreateSchema()
    validated_data = schema.load(data)
    
    # Execute the command to create a sales plan
    sales_plan = CreateSalesPlanCommand(validated_data).execute()
    
    # Serialize the result
    result_schema = SalesPlanSchema()
    return jsonify(result_schema.dump(sales_plan)), 201


@api.route('/sales-plans/<int:id>', methods=['PUT'])
@validate_token
@director_required
def update_sales_plan(id):
    """Update an existing sales plan - requires Director role"""
    # Get request data
    data = request.get_json()
    if not data:
        raise BadRequestError("No input data provided")
    
    # Validate the request data
    schema = SalesPlanUpdateSchema()
    validated_data = schema.load(data)
    
    # Execute the command to update the sales plan
    sales_plan = UpdateSalesPlanCommand(id, validated_data).execute()
    
    # Serialize the result
    result_schema = SalesPlanSchema()
    return jsonify(result_schema.dump(sales_plan))


@api.route('/sales-plans/<int:id>', methods=['DELETE'])
@validate_token
@director_required
def delete_sales_plan(id):
    """Delete a sales plan - requires Director role"""
    # Execute the command to delete the sales plan
    DeleteSalesPlanCommand(id).execute()
    
    return '', 204