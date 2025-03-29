from flask import request, jsonify

from app.lib.auth import validate_token
from app.lib.errors import BadRequestError
from app.commands.seller.create import CreateSellerCommand
from app.commands.seller.update import UpdateSellerCommand
from app.commands.seller.delete import DeleteSellerCommand
from app.commands.seller.get import GetSellerCommand, GetAllSellersCommand
from app.schemas.seller import SellerSchema, SellerCreateSchema, SellerUpdateSchema
from . import api


@api.route('/sellers', methods=['GET'])
@validate_token
def get_sellers():
    """Get all sellers"""
    # Execute the command to get all sellers
    sellers = GetAllSellersCommand().execute()
    
    # Serialize the result
    schema = SellerSchema(many=True)
    return jsonify(schema.dump(sellers))


@api.route('/sellers/<int:id>', methods=['GET'])
@validate_token
def get_seller(id):
    """Get a specific seller by ID"""
    # Execute the command to get the seller
    seller = GetSellerCommand(id).execute()
    
    # Serialize the result
    schema = SellerSchema()
    return jsonify(schema.dump(seller))


@api.route('/sellers', methods=['POST'])
@validate_token
def create_seller():
    """Create a new seller reference"""
    # Get request data
    data = request.get_json()
    if not data:
        raise BadRequestError("No input data provided")
    
    # Validate the request data
    schema = SellerCreateSchema()
    validated_data = schema.load(data)
    
    # Execute the command to create a seller
    seller = CreateSellerCommand(validated_data).execute()
    
    # Serialize the result
    result_schema = SellerSchema()
    return jsonify(result_schema.dump(seller)), 201


@api.route('/sellers/<int:id>', methods=['PUT'])
@validate_token
def update_seller(id):
    """Update a seller reference"""
    # Get request data
    data = request.get_json()
    if not data:
        raise BadRequestError("No input data provided")
    
    # Validate the request data
    schema = SellerUpdateSchema()
    validated_data = schema.load(data)
    
    # Execute the command to update the seller
    seller = UpdateSellerCommand(id, validated_data).execute()
    
    # Serialize the result
    result_schema = SellerSchema()
    return jsonify(result_schema.dump(seller))


@api.route('/sellers/<int:id>', methods=['DELETE'])
@validate_token
def delete_seller(id):
    """Delete a seller reference"""
    # Execute the command to delete the seller
    DeleteSellerCommand(id).execute()
    
    return '', 204