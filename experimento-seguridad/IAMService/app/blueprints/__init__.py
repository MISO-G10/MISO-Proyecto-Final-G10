from flask import Blueprint

api = Blueprint('iam-service', __name__)

from . import hello_world
