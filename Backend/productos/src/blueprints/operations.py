from flask import Flask, jsonify, request, Blueprint, g
from ..commands.create import Create
from ..commands.clean import Clean
from src.utils.validate_token import token_required
import os


operations_blueprint = Blueprint('productos', __name__)


# Consulta de salud del servicio
@operations_blueprint.route("/ping", methods=['GET'])
def health_check():
    return "success", 200


# Restablecer base de datos
@operations_blueprint.route("/reset", methods=['POST'])
def reset_fabricante_database():
    result = Clean().execute()

    return jsonify(result), 200
