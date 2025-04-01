from typing import Optional

from flask_openapi3 import Tag
from pydantic import BaseModel, Field

from app.commands.sales_plan_seller.create import CreateSalesPlanSellerCommand
from app.commands.sales_plan_seller.delete import DeleteSalesPlanSellerCommand
from app.commands.sales_plan_seller.get import GetSalesPlanSellerCommand, GetPlanSellersCommand
from app.commands.sales_plan_seller.update import UpdateSalesPlanSellerCommand
from app.lib.auth import validate_token, director_required
from app.responses import ErrorResponse
from app.responses.sales_plan import SalesPlanPath
from app.responses.seller import SellerResponse, SellerListResponse
from . import seller_blueprint

# Define API tag
sellers_tag = Tag(name="Plan Sellers", description="Operations on sellers assigned to sales plans")


# Pydantic models for request data
class SellerCreate(BaseModel):
    """Schema for creating a new Seller"""
    name: str = Field(..., min_length=2, max_length=255, description="Seller name")
    seller_id: int = Field(..., gt=0, description="Seller ID from the users service")


class SellerUpdate(BaseModel):
    """Schema for updating an existing Seller"""
    name: Optional[str] = Field(None, min_length=2, max_length=255, description="Seller name")


class PlanSellerPath(BaseModel):
    """Path parameters for Plan Seller routes"""
    plan_id: int = Field(..., description="Sales Plan ID")
    seller_id: int = Field(..., description="Seller ID")


@seller_blueprint.get('', tags=[sellers_tag],
                      responses={200: SellerListResponse, 404: ErrorResponse})
@validate_token
def get_plan_sellers(path: SalesPlanPath):
    """Get all sellers assigned to a specific sales plan"""
    plan_id = path.plan_id
    # Get sellers for the specified plan using the optimized command
    plan_sellers = GetPlanSellersCommand(plan_id).execute()

    # Return as Pydantic models
    return SellerListResponse(
        items=[
            SellerResponse(
                id=seller.id,
                name=seller.name,
                seller_id=seller.seller_id
            ) for seller in plan_sellers
        ]
    ).model_dump()


@seller_blueprint.get('/<int:seller_id>', tags=[sellers_tag],
                      responses={200: SellerResponse, 404: ErrorResponse})
@validate_token
def get_plan_seller(path: PlanSellerPath):
    """Get a specific seller by ID within a sales plan"""
    # Execute the command to get the seller, passing plan_id for verification
    seller = GetSalesPlanSellerCommand(path.seller_id, plan_id=path.plan_id).execute()

    # Return as Pydantic model
    return SellerResponse(
        id=seller.id,
        name=seller.name,
        seller_id=seller.seller_id
    ).model_dump()


@seller_blueprint.post('', tags=[sellers_tag],
                       responses={201: SellerResponse, 400: ErrorResponse, 403: ErrorResponse, 404: ErrorResponse})
@validate_token
@director_required
def add_seller_to_plan(path: SalesPlanPath, body: SellerCreate):
    """Add a new seller to a sales plan - requires Director role"""
    plan_id = path.plan_id

    # Prepare data with plan_id
    seller_data = body.model_dump()
    seller_data['sales_plan_id'] = plan_id

    # Execute the command to create a seller with validated data
    seller = CreateSalesPlanSellerCommand(seller_data).execute()

    # Return as Pydantic model
    response = SellerResponse(
        id=seller.id,
        name=seller.name,
        seller_id=seller.seller_id
    )

    return response.model_dump(), 201


@seller_blueprint.put('/<int:seller_id>', tags=[sellers_tag],
                      responses={200: SellerResponse, 400: ErrorResponse, 403: ErrorResponse, 404: ErrorResponse})
@validate_token
@director_required
def update_plan_seller(path: PlanSellerPath, body: SellerUpdate):
    """Update a seller within a sales plan - requires Director role"""
    seller_id = path.seller_id
    plan_id = path.plan_id

    # Verify the seller belongs to the plan
    GetSalesPlanSellerCommand(seller_id, plan_id=plan_id).execute()

    # Execute the command to update the seller with validated data
    updated_seller = UpdateSalesPlanSellerCommand(seller_id, body.model_dump(exclude_none=True)).execute()

    # Return as Pydantic model
    return SellerResponse(
        id=updated_seller.id,
        name=updated_seller.name,
        seller_id=updated_seller.seller_id
    ).model_dump()


@seller_blueprint.delete('/<int:seller_id>', tags=[sellers_tag],
                         responses={204: None, 403: ErrorResponse, 404: ErrorResponse})
@validate_token
@director_required
def remove_seller_from_plan(path: PlanSellerPath):
    """Remove a seller from a sales plan - requires Director role"""
    seller_id = path.seller_id
    plan_id = path.plan_id

    # Verify the seller belongs to the plan
    GetSalesPlanSellerCommand(seller_id, plan_id=plan_id).execute()

    # Execute the command to delete the seller
    DeleteSalesPlanSellerCommand(seller_id).execute()

    return '', 204
