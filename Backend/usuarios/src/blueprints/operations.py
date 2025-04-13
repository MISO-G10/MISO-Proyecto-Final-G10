from flask import Flask, jsonify, request, Blueprint
from ..commands.create import Create
from ..commands.update import Update
from ..commands.login import Login
from ..commands.clean import Clean
from ..commands.list import List
from ..commands.validate import Validate
from ..models.usuario import Usuario  # Assuming the Usuario model is defined in this file
import os

operations_blueprint = Blueprint('usuarios', __name__)

@operations_blueprint.route('', methods = ['POST'])
def create_usuario():
    json = request.get_json()
    result = Create(json).execute()
    return jsonify(result), 201


@operations_blueprint.route('/<uuid:id>', methods = ['PATCH'])
def update_usuario(id):
    json = request.get_json()
    result = Update(id, json).execute()
    return jsonify(result), 200

@operations_blueprint.route('/auth', methods = ['POST'])
def login_usuario():
    json = request.get_json()
    result = Login(json).execute()
    return jsonify(result), 200


@operations_blueprint.route('/me', methods = ['GET'])
def validate_usuario():
    #Obtener el token de la cabecera
    auth_header = request.headers.get('Authorization')
    result = Validate(auth_header).execute()
    return jsonify(result), 200

# Consulta de salud del servicio
@operations_blueprint.route("/ping", methods = ['GET'])
def health_check():
    return "success", 200

# Restablecer base de datos
@operations_blueprint.route("/reset", methods = ['POST'])
def reset_usuario_database():
    result = Clean().execute()
    return jsonify(result), 200

@operations_blueprint.route('', methods=['GET'])
def list_usuarios():
    # Obtener el token de la cabecera
    auth_header = request.headers.get('Authorization')
    if not auth_header:
        return jsonify({'error': 'Token is missing!'}), 401

    # Simulate token validation and role extraction
    current_user = Validate(auth_header).execute()  # This should return the role
    user_role = current_user['rol']

    # Role-based access control
    if user_role in ['ADMINISTRADOR', 'VENDEDOR', 'DIRECTOR_VENTAS']:
        # Use the List command to get the users
        usuarios = List(current_user, request.args.to_dict()).execute()
    else:
        # No access for other roles
        return jsonify({'error': 'Access denied!'}), 403

    # Since usuarios is already serialized, return it directly
    return jsonify(usuarios), 200