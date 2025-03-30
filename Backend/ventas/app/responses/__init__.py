"""Response models for API endpoints."""
from typing import Optional
from pydantic import BaseModel


# Common response models
class ErrorResponse(BaseModel):
    """Schema for error responses"""
    error: str
    details: Optional[str] = None
