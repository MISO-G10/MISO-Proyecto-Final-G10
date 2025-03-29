from flask import Blueprint
from flask_openapi3 import APIBlueprint

# Register Flask-OpenAPI3 APIBlueprint for documented routes
api = APIBlueprint('sales', __name__)

# Keep a traditional Blueprint for commands that don't need documentation
command_bp = Blueprint('commands', __name__)

# Import route modules after blueprint creation
from . import sales_plan, sellers, health
