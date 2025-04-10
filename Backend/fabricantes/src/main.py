import os

from flask import Flask, jsonify
from flask_cors import CORS

from src.db.base import Base
# DB
from src.db.session import engine, get_inspector
# CONFIG
from src.utils.config import get_config
# ROUTES
from .blueprints.operations import operations_blueprint
# ERRORS
from .errors.errors import ApiError

# ENVIRONMENT VARIABLES

def create_app(env_name='development'):
    get_config(env_name)
    app = Flask(__name__)
    CORS(
        app,
        resources={
            r"*": {
                "origins": [
                    os.environ.get("FRONTEND_URL")
                ],  # Solo permite el frontend Angular
                "methods": ["GET", "POST", "PUT", "DELETE", "OPTIONS"],
                "allow_headers": ["Content-Type", "Authorization"],
                "supports_credentials": True
            }
        }
    )
    app.register_blueprint(operations_blueprint, url_prefix='/fabricantes')

    # Initialize the database
    initialize_database()

    @app.errorhandler(ApiError)
    def handle_exception(err):
        response = err.to_dict() if err else None

        return jsonify(response), err.code

    return app

# Crear las tablas automáticamente al iniciar la aplicación
def initialize_database():
    inspector = get_inspector(engine)
    tables = inspector.get_table_names()
    if not tables:
        Base.metadata.create_all(bind=engine)
        print('Tablas Creadas.')
    else:
        print('Las tablas ya existen en la base de datos.')

if __name__ == "__main__":
    app = create_app()
    app.run(debug=True)
