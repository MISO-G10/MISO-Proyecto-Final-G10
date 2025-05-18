from flask import Flask, jsonify, request, Blueprint, g
from ..commands.create_producto import Create
from ..commands.create_producto_bulk import CreateProductoBulkCommand
from ..commands.listar_productos import ListProductos
from ..commands.clean import Clean
from src.utils.validate_token import token_required
from ..commands.create_bodega import CreateBodega
from ..commands.get_producto_ubicacion import GetProductoUbicacion
from ..commands.assign_producto_bodega import AssignProductoBodega
from ..commands.create_pedido import CreatePedido
from ..commands.listar_rutas import ListRutas
from ..commands.listar_bodegas import ListBodegas
from ..commands.get_pedido import GetPedido
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
@operations_blueprint.route("/createproduct", methods=['POST'])
@token_required
def create_producto():
    json_data = request.get_json()
    if not json_data:
        return jsonify({"error": "El cuerpo de la solicitud no puede estar vacío"}), 400
    
    if "fabricante_id" not in json_data:
        return jsonify({"error": "Requerido valor fabricante_id para crear un producto"}), 400
    current_usuario = g.current_usuario
    result = Create(current_usuario, json_data).execute()
    
    # Verificar si el resultado es una tupla (respuesta, código)
    if isinstance(result, tuple) and len(result) == 2:
        return jsonify(result[0]), result[1]
    
    # Si no es una tupla, es una respuesta exitosa
    return jsonify(result), 201


@operations_blueprint.route("/productos/ubicacion", methods=['GET'])
@token_required
def get_producto_ubicacion():
    producto_id = request.args.get('producto')

    if not producto_id:
        return jsonify({"error": "Se requiere el parámetro 'producto' para consultar la ubicación"}), 400

    result = GetProductoUbicacion(producto_id).execute()

    # Check if we got an error response (tuple with response and status code)
    if isinstance(result, tuple) and len(result) == 2:
        return jsonify(result[0]), result[1]

    return jsonify(result), 200


@operations_blueprint.route("/bodegas", methods=['POST'])
@token_required
def create_bodega():
    json_data = request.get_json()
    if not json_data:
        return jsonify({"error": "El cuerpo de la solicitud no puede estar vacío"}), 400

    result = CreateBodega(json_data).execute()

    # Check if we got an error response (tuple with response and status code)
    if isinstance(result, tuple) and len(result) == 2:
        return jsonify(result[0]), result[1]

    # The result is already a dictionary from the command
    return jsonify(result), 201


@operations_blueprint.route("/bodegas/<bodega_id>/productos", methods=['POST'])
@token_required
def add_producto_to_bodega(bodega_id):
    json_data = request.get_json()
    if not json_data:
        return jsonify({"error": "El cuerpo de la solicitud no puede estar vacío"}), 400

    if "producto_id" not in json_data:
        return jsonify({"error": "Requerido valor producto_id para agregar un producto a la bodega"}), 400

    if "cantidad" not in json_data:
        return jsonify({"error": "Requerido valor cantidad para agregar un producto a la bodega"}), 400

    result = AssignProductoBodega(bodega_id, json_data).execute()

    # Check if we got an error response (tuple with response and status code)
    if isinstance(result, tuple) and len(result) == 2:
        return jsonify(result[0]), result[1]

    # The result is already a dictionary from the command
    return jsonify(result), 201
    
# Listar todos los productos
@operations_blueprint.route("/productos", methods=['GET'])
@token_required
def list_productos():
    result = ListProductos().execute()
    return jsonify(result), 200

# Crear productos en bulk con Pub/Sub
@operations_blueprint.route("/productos/bulk", methods=['POST'])
@token_required
def create_productos_bulk():
    try:
        json_data = request.get_json()
        productos = json_data.get('productos', [])
        
        if not productos or not isinstance(productos, list):
            return jsonify({"error": "Se requiere una lista de productos"}), 400

        current_usuario = g.current_usuario
        command = CreateProductoBulkCommand(current_usuario, productos)
        result = command.execute()
        
        # Si el resultado es una tupla, contiene un código de estado
        if isinstance(result, tuple):
            return jsonify(result[0]), result[1]
            
        # Si no es una tupla, es una respuesta exitosa
        return jsonify(result), 201

    except Exception as e:
        return jsonify({"error": str(e)}), 500


@operations_blueprint.route("/pedidos", methods=['POST'])
@token_required
def create_pedido():
    try:
        json_data = request.get_json()
        if not json_data:
            return jsonify({"error": "El cuerpo de la solicitud no puede estar vacío"}), 400

        current_usuario = g.current_usuario
        result = CreatePedido(current_usuario, json_data).execute()

        if isinstance(result, tuple) and len(result) == 2:
            return jsonify(result[0]), result[1]

        return jsonify(result), 201
    except Exception as e:
        print(f"Error al crear pedido: {str(e)}")
        return jsonify({"error": str(e)}), 500

@operations_blueprint.route("/pedidos", methods=['GET'])
@token_required
def get_pedidos():
    user_id = request.args.get('user_id')
    tipo_usuario = request.args.get('tipo_usuario')
    fecha_entrega = request.args.get('fecha_entrega')
    pedido_id = request.args.get('pedido_id')

    result = GetPedido(
        pedido_id=pedido_id,
        user_id=user_id,
        tipo_usuario=tipo_usuario,
        fecha_entrega=fecha_entrega
    ).execute()
    
    if isinstance(result, tuple) and len(result) == 2:
        return jsonify(result[0]), result[1]

    return jsonify(result), 200

@operations_blueprint.route("/pedidos/<pedido_id>", methods=['GET'])
@token_required
def get_pedido(pedido_id):
    result = GetPedido(pedido_id=pedido_id).execute()
    
    if isinstance(result, tuple) and len(result) == 2:
        return jsonify(result[0]), result[1]

    return jsonify(result), 200

# Listar todas las rutas
@operations_blueprint.route("/rutas", methods=['GET'])
@token_required
def list_rutas():
    """
    Endpoint para obtener todas las rutas disponibles
    """
    result = ListRutas().execute()
    
    # Verificar si el resultado es una tupla (respuesta, código)
    if isinstance(result, tuple) and len(result) == 2:
        return jsonify(result[0]), result[1]
    
    return jsonify(result), 200


# Listar todas las bodegas
@operations_blueprint.route("/bodegas", methods=['GET'])
@token_required
def list_bodegas():
    """
    Endpoint para obtener todas las bodegas disponibles
    """
    result = ListBodegas().execute()
    
    # Verificar si el resultado es una tupla (respuesta, código)
    if isinstance(result, tuple) and len(result) == 2:
        return jsonify(result[0]), result[1]
    
    return jsonify(result), 200
