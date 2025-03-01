import os
from flask import Flask, jsonify
from celery import Celery


app = Flask(__name__)

# Configurar Celery para que apunte al mismo broker (Redis)
celery = Celery(__name__,
                broker=os.environ.get('CELERY_BROKER_URL', 'redis://localhost:6379/0'),
                backend=os.environ.get('CELERY_RESULT_BACKEND', 'redis://localhost:6379/0'))

# Endpoint que solicita el estado del InventarioService
@app.route("/monitor", methods=["GET"])
def verificar_inventario():
    # Enviar tarea asincrónica al InventarioService para obtener estado
    result = celery.send_task("app.obtener_estado")  # nombre de tarea registrada en InventarioService
    estado = result.get(timeout=5)  # espera hasta 5 segundos por la respuesta
    return jsonify({"estado_inventario": estado}), 200