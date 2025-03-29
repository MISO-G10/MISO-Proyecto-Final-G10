from typing import Optional, List

from pydantic import BaseModel, Field, field_validator
from flask_openapi3 import Tag

from app.commands.seller.create import CreateSellerCommand
from app.commands.seller.delete import DeleteSellerCommand
from app.commands.seller.get import GetSellerCommand, GetAllSellersCommand
from app.commands.seller.update import UpdateSellerCommand
from app.lib.auth import validate_token
from . import api

# Import response models
from app.responses.seller import SellerResponse, SellerListResponse, SellerPath
from app.responses import ErrorResponse

# Define API tag
sellers_tag = Tag(name="Sellers", description="Operations on sellers")


# Pydantic models for request data
class SellerCreate(BaseModel):
    """Schema for creating a new Seller"""
    name: str = Field(..., min_length=2, max_length=255, description="Seller name")
    seller_id: int = Field(..., gt=0, description="Seller ID from the users service")


class SellerUpdate(BaseModel):
    """Schema for updating an existing Seller"""
    name: Optional[str] = Field(None, min_length=2, max_length=255, description="Seller name")


@api.get('/sellers', tags=[sellers_tag], responses={200: SellerListResponse})
@validate_token
def get_sellers():
    """Get all sellers"""
    # Execute the command to get all sellers
    sellers = GetAllSellersCommand().execute()

    # Return as Pydantic models
    return SellerListResponse(
        items=[
            SellerResponse(
                id=seller.id,
                name=seller.name,
                seller_id=seller.seller_id
            ) for seller in sellers
        ]
    ).model_dump()


@api.get('/sellers/<int:id>', tags=[sellers_tag], responses={200: SellerResponse, 404: ErrorResponse})
@validate_token
def get_seller(path: SellerPath):
    """Get a specific seller by ID"""
    id = path.id
    # Execute the command to get the seller
    seller = GetSellerCommand(id).execute()

    # Return as Pydantic model
    return SellerResponse(
        id=seller.id,
        name=seller.name,
        seller_id=seller.seller_id
    ).model_dump()


@api.post('/sellers', tags=[sellers_tag], responses={201: SellerResponse, 400: ErrorResponse})
@validate_token
def create_seller(body: SellerCreate):
    """Create a new seller reference"""
    # Execute the command to create a seller with validated data
    seller = CreateSellerCommand(body.model_dump()).execute()

    # Return as Pydantic model
    response = SellerResponse(
        id=seller.id,
        name=seller.name,
        seller_id=seller.seller_id
    )

    return response.model_dump(), 201


@api.put('/sellers/<int:id>', tags=[sellers_tag],
         responses={200: SellerResponse, 400: ErrorResponse, 404: ErrorResponse})
@validate_token
def update_seller(path: SellerPath, body: SellerUpdate):
    """Update a seller reference"""
    id = path.id
    # Execute the command to update the seller with validated data
    seller = UpdateSellerCommand(id, body.model_dump(exclude_none=True)).execute()

    # Return as Pydantic model
    return SellerResponse(
        id=seller.id,
        name=seller.name,
        seller_id=seller.seller_id
    ).model_dump()


@api.delete('/sellers/<int:id>', tags=[sellers_tag], responses={204: None, 404: ErrorResponse})
@validate_token
def delete_seller(path: SellerPath):
    """Delete a seller reference"""
    id = path.id
    # Execute the command to delete the seller
    DeleteSellerCommand(id).execute()

    return '', 204
