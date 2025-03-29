from flask import Blueprint

api = Blueprint('sales', __name__)

from . import sales_plan, sellers
