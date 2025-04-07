"""Health response models."""
from pydantic import BaseModel


class HealthResponse(BaseModel):
    """Health response schema"""
    status: str