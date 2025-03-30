"""Sales plan response models."""
from typing import List
from pydantic import BaseModel, Field

from app.responses.seller import SellerResponse


class SalesPlanPath(BaseModel):
    """Path parameters for SalesPlan routes"""
    id: int = Field(..., description="Sales plan ID")


class SalesPlanResponse(BaseModel):
    """Schema for returning a SalesPlan"""
    id: int
    name: str
    description: str
    target_amount: float
    start_date: str
    end_date: str
    sellers: List[SellerResponse]


class SalesPlanListResponse(BaseModel):
    """Schema for returning multiple SalesPlans"""
    items: List[SalesPlanResponse]
