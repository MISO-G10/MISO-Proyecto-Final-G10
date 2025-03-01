import os
from flask import Flask, jsonify
from celery import Celery
from database import db  # Importa db desde database.py
from models import Producto
from seed_data import seed_database

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = os.environ.get('DATABASE_URL', 'sqlite:///data/inventario.db')

# Crear directorio para almacenar la base de datos SQLite
os.makedirs("data", exist_ok=True)

db.init_app(app)

# Configurar Celery
celery = Celery(app.name,
                broker=os.environ.get('CELERY_BROKER_URL', 'redis://localhost:6379/0'),
                backend=os.environ.get('CELERY_RESULT_BACKEND', 'redis://localhost:6379/0'))


# Ejemplo: tarea Celery para obtener estado (ping) del servicio
@celery.task
def obtener_estado():
    # Esta tarea podría chequear la base de datos o variables internas
    return "OK"

# Endpoint Flask para listar productos (sin usar Celery, acceso directo a DB)
@app.route("/inventario", methods=["GET"])
def listar_productos():
    productos = Producto.query.all()
    data = [{"id": p.id, "nombre": p.nombre, "stock": p.stock} for p in productos]
    return jsonify(data), 200

if __name__ == "__main__":
    with app.app_context():
        db.create_all()
        seed_database()
    app.run(host="0.0.0.0", port=5000)