from datetime import datetime
from typing import List, Optional

from pydantic import BaseModel, Field, model_validator, field_validator

from app.lib.validators import validate_date_string, validate_date_range
from flask_openapi3 import Tag

from app.commands.sales_plan.create import CreateSalesPlanCommand
from app.commands.sales_plan.delete import DeleteSalesPlanCommand
from app.commands.sales_plan.get import GetSalesPlanCommand, GetAllSalesPlansCommand
from app.commands.sales_plan.update import UpdateSalesPlanCommand
from app.lib.auth import validate_token, director_required
from . import plan_blueprint

from app.responses.sales_plan import SalesPlanResponse, SalesPlanListResponse, SalesPlanPath
from app.responses.seller import SellerResponse
from app.responses import ErrorResponse

sales_plan_tag = Tag(name="Sales Plans", description="Operations on sales plans")


# Pydantic models for request data
class SalesPlanCreate(BaseModel):
    """Schema for creating a new SalesPlan"""
    name: str = Field(..., min_length=3, max_length=255, description="Sales plan name")
    description: str = Field(..., min_length=3, max_length=500, description="Sales plan description")
    target_amount: float = Field(..., gt=0, description="Target sales amount")
    start_date: str = Field(..., description="Start date in format YYYY-MM-DD")
    end_date: str = Field(..., description="End date in format YYYY-MM-DD")
    seller_ids: List[int] = Field(..., description="List of seller IDs assigned to this plan")

    @field_validator('start_date', 'end_date', mode='after')
    @classmethod
    def validate_date(cls, value: str) -> str:
        """Validate that the date string represents a valid date"""
        return validate_date_string(value)

    @model_validator(mode='after')
    def validate_dates(self):
        """Validate that end_date is after start_date"""
        if not hasattr(self, 'start_date') or not hasattr(self, 'end_date'):
            return self

        validate_date_range(self.start_date, self.end_date)
        return self

    @field_validator('seller_ids', mode='after')
    @classmethod
    def validate_seller_ids(cls, v: List[int]) -> List[int]:
        """Validate that seller_ids is not empty"""
        if len(v) < 1:
            raise ValueError("At least one seller ID must be provided")
        return v


class SalesPlanUpdate(BaseModel):
    """Schema for updating an existing SalesPlan"""
    name: Optional[str] = Field(None, min_length=3, max_length=255, description="Sales plan name")
    description: Optional[str] = Field(None, min_length=3, max_length=500, description="Sales plan description")
    target_amount: Optional[float] = Field(None, gt=0, description="Target sales amount")
    start_date: Optional[str] = Field(None, description="Start date in format YYYY-MM-DD")
    end_date: Optional[str] = Field(None, description="End date in format YYYY-MM-DD")
    seller_ids: Optional[List[int]] = Field(None, description="List of seller IDs assigned to this plan")

    @field_validator('start_date', 'end_date', mode='after')
    @classmethod
    def validate_date(cls, value: Optional[str]) -> Optional[str]:
        """Validate that the date string represents a valid date if provided"""
        return validate_date_string(value)

    @model_validator(mode='after')
    def validate_dates(self):
        """Validate that end_date is after start_date if both are provided"""
        if not hasattr(self, 'start_date') or not hasattr(self, 'end_date'):
            return self

        validate_date_range(self.start_date, self.end_date)
        return self

    @field_validator('seller_ids', mode='after')
    @classmethod
    def validate_seller_ids(cls, v: Optional[List[int]]) -> Optional[List[int]]:
        """Validate that seller_ids is not empty if provided"""
        if v is not None and len(v) < 1:
            raise ValueError("At least one seller ID must be provided")
        return v


@plan_blueprint.get('', tags=[sales_plan_tag], responses={200: SalesPlanListResponse})
@validate_token
def get_sales_plans():
    """Get all sales plans"""
    sales_plans = GetAllSalesPlansCommand().execute()

    return SalesPlanListResponse(
        items=[
            SalesPlanResponse(
                id=plan.id,
                name=plan.name,
                description=plan.description,
                target_amount=plan.target_amount,
                start_date=plan.start_date,
                end_date=plan.end_date,
                sellers=[
                    SellerResponse(
                        id=seller.id,
                        name=seller.name,
                        seller_id=seller.seller_id
                    ) for seller in plan.sellers
                ]
            ) for plan in sales_plans
        ]
    ).model_dump()


@plan_blueprint.get('/<plan_id>', tags=[sales_plan_tag], responses={200: SalesPlanResponse, 404: ErrorResponse})
@validate_token
def get_sales_plan(path: SalesPlanPath):
    """Get a specific sales plan by ID"""
    sales_plan = GetSalesPlanCommand(path.plan_id).execute()

    return SalesPlanResponse(
        id=sales_plan.id,
        name=sales_plan.name,
        description=sales_plan.description,
        target_amount=sales_plan.target_amount,
        start_date=sales_plan.start_date,
        end_date=sales_plan.end_date,
        sellers=[
            SellerResponse(
                id=seller.id,
                name=seller.name,
                seller_id=seller.seller_id
            ) for seller in sales_plan.sellers
        ]
    ).model_dump()


@plan_blueprint.post('', tags=[sales_plan_tag],
                     responses={201: SalesPlanResponse, 400: ErrorResponse, 403: ErrorResponse})
@validate_token
@director_required
def create_sales_plan(body: SalesPlanCreate):
    """Create a new sales plan - requires Director role"""
    sales_plan = CreateSalesPlanCommand(body.model_dump()).execute()

    response = SalesPlanResponse(
        id=sales_plan.id,
        name=sales_plan.name,
        description=sales_plan.description,
        target_amount=sales_plan.target_amount,
        start_date=sales_plan.start_date,
        end_date=sales_plan.end_date,
        sellers=[
            SellerResponse(
                id=seller.id,
                name=seller.name,
                seller_id=seller.seller_id
            ) for seller in sales_plan.sellers
        ]
    )

    return response.model_dump(), 201


@plan_blueprint.put('/<plan_id>', tags=[sales_plan_tag],
                    responses={200: SalesPlanResponse, 400: ErrorResponse, 403: ErrorResponse, 404: ErrorResponse})
@validate_token
@director_required
def update_sales_plan(path: SalesPlanPath, body: SalesPlanUpdate):
    """Update an existing sales plan - requires Director role"""
    id = path.plan_id
    sales_plan = UpdateSalesPlanCommand(id, body.model_dump(exclude_none=True)).execute()

    return SalesPlanResponse(
        id=sales_plan.id,
        name=sales_plan.name,
        description=sales_plan.description,
        target_amount=sales_plan.target_amount,
        start_date=sales_plan.start_date,
        end_date=sales_plan.end_date,
        sellers=[
            SellerResponse(
                id=seller.id,
                name=seller.name,
                seller_id=seller.seller_id
            ) for seller in sales_plan.sellers
        ]
    ).model_dump()


@plan_blueprint.delete('/<plan_id>', tags=[sales_plan_tag],
                       responses={204: None, 403: ErrorResponse, 404: ErrorResponse})
@validate_token
@director_required
def delete_sales_plan(path: SalesPlanPath):
    """Delete a sales plan - requires Director role"""
    id = path.plan_id
    DeleteSalesPlanCommand(id).execute()

    return '', 204
