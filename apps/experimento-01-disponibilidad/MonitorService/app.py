import os
import time
import threading
from flask import Flask, jsonify
from celery import Celery

app = Flask(__name__)

# Configurar Celery para que apunte al mismo broker (Redis)
celery = Celery(__name__,
                broker=os.environ.get('CELERY_BROKER_URL', 'redis://localhost:6379/0'),
                backend=os.environ.get('CELERY_RESULT_BACKEND', 'redis://localhost:6379/0'))

# Esta función envía el ping y mide el tiempo de respuesta
def ping_inventario():
    while True:
        start = time.perf_counter()
        try:
            # Enviar tarea asíncrona al InventarioService
            result = celery.send_task("app.obtener_estado")
            respuesta = result.get(timeout=5)  # espera hasta 5 segundos por la respuesta
            elapsed = time.perf_counter() - start
            if elapsed > 2:
                print(f"[ALERTA] El ping tardó {elapsed:.3f} s (umbral superado). Respuesta: {respuesta}")
            else:
                print(f"[OK] Ping recibido en {elapsed:.3f} s. Respuesta: {respuesta}")
        except Exception as e:
            print(f"[ERROR] No se obtuvo respuesta en tiempo: {e}")
        time.sleep(1)  # esperar 1 segundo antes de enviar el siguiente ping

# Hilo de fondo para enviar pings continuamente
def start_ping_thread():
    thread = threading.Thread(target=ping_inventario, daemon=True)
    thread.start()

# Endpoint para consulta manual (opcional)
@app.route("/monitor", methods=["GET"])
def verificar_inventario():
    try:
        result = celery.send_task("app.obtener_estado")
        estado = result.get(timeout=5)
        return jsonify({"estado_inventario": estado}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    # Inicia el hilo de ping en background
    start_ping_thread()
    app.run(host="0.0.0.0", port=5000)