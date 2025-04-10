from flask_openapi3 import Tag
from . import api

from app.responses.health import HealthResponse

# Define API tag
health_tag = Tag(name="Health", description="API health check operations")


@api.get('/ping', tags=[health_tag], security=[{"bearerAuth": []}])
def health():
    """Health check endpoint to verify the API is running"""
    return HealthResponse(status="ok").model_dump()
