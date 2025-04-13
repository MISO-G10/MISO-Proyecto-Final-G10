from flask import jsonify, request, Blueprint, g
from ..commands.create import Create
from ..commands.clean import Clean
from src.utils.validate_token import token_required

from ..commands.delete import Delete
from ..commands.list import List
from ..commands.show import Show

operations_blueprint = Blueprint('visitas', __name__)

@operations_blueprint.route('', methods=['POST'])
@token_required
def create_visita():
    json = request.get_json()
    current_usuario = g.current_usuario

    result = Create(current_usuario, json).execute()

    return jsonify(result), 201


@operations_blueprint.route('', methods=['GET'])
@token_required
def list_visitas():
    current_usuario = g.current_usuario

    service = List(
        usuario=current_usuario,
        data=request.args.to_dict()
    )

    visitas = service.execute()

    return jsonify(visitas), 200


@operations_blueprint.route('/<string:id>', methods=['GET'])
@token_required
def get_visita(id):
    result = Show(id).execute()

    return jsonify(result), 200


@operations_blueprint.route('/<string:id>', methods=['DELETE'])
@token_required
def delete_visita(id):
    Delete(id).execute()

    return jsonify({
        "msg": "la visita fue eliminada"
    }), 200

# Consulta de salud del servicio
@operations_blueprint.route("/ping", methods=['GET'])
def health_check():
    return "success", 200


# Restablecer base de datos
@operations_blueprint.route("/reset", methods=['POST'])
def reset_visita_database():
    result = Clean().execute()

    return jsonify(result), 200
