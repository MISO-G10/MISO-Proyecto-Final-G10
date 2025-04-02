from flask import Flask, jsonify, request, Blueprint, g
from ..commands.create_producto import Create
from ..commands.clean import Clean
from src.utils.validate_token import token_required
import os


operations_blueprint = Blueprint('inventarios', __name__)


# Consulta de salud del servicio
@operations_blueprint.route("/ping", methods=['GET'])
def health_check():
    return "success", 200


# Restablecer base de datos
@operations_blueprint.route("/reset", methods=['POST'])
def reset_fabricante_database():
    result = Clean().execute()

    return jsonify(result), 200

# Crear productos asociados a un fabricante
@operations_blueprint.route("/create", methods=['POST'])
@token_required
def create_producto():
    json = request.get_json()
    if not json_data:
        return jsonify({"error": "El cuerpo de la solicitud no puede estar vacío"}), 400
    
    if "fabricante_id" not in json_data:
        return jsonify({"error": "Requerido valor fabricante_id para crear un producto"}), 400
    current_usuario = g.current_usuario
    result = Create(current_usuario, json).execute()

    return jsonify(result), 201