"""SalesPlanSeller response models."""
from typing import List
from pydantic import BaseModel, Field


class SellerPath(BaseModel):
    """Path parameters for Seller routes"""
    seller_id: int = Field(..., description="Seller ID")


class SellerResponse(BaseModel):
    """Schema for returning a Seller"""
    id: int
    name: str
    seller_id: int


class SellerListResponse(BaseModel):
    """Schema for returning multiple Sellers"""
    items: List[SellerResponse]
