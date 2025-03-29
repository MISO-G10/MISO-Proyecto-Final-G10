from flask import Flask, jsonify, json
from werkzeug.exceptions import HTTPException
from marshmallow import ValidationError

from app.blueprints import api
from app.blueprints.commands import commands
from app.config.application import ApplicationConfig
from app.lib.database import db, migrate
from app.lib.schema import marshmallow
from app.lib.errors import ApiError


def create_app():
    app = Flask(__name__)
    app.config.from_object(ApplicationConfig)

    """
    The following code snippet registers the error handlers for the application.
    """

    @app.errorhandler(ApiError)
    def handle_api_error(error):
        app.logger.error(f"{error}")
        return jsonify(error.to_dict()), error.status_code

    @app.errorhandler(ValidationError)
    def handle_validation_error(error):
        app.logger.error(f"Validation error: {error}")
        return jsonify({"error": error.messages}), 400

    @app.errorhandler(ValueError)
    def handle_value_error(error):
        app.logger.error(f"Value error: {error}")
        return jsonify({"error": str(error)}), 400

    @app.errorhandler(HTTPException)
    def handle_exception(e):
        app.logger.error(f"HTTP exception: {e}")
        response = e.get_response()
        response.data = json.dumps({
            "message": e.description,
        })
        response.content_type = "application/json"
        return response

    @app.errorhandler(Exception)
    def handle_unexpected_error(e):
        app.logger.error(f"Unexpected error: {e}")
        return jsonify({"error": "An unexpected error occurred", "details": str(e)}), 500

    """
    The following code snippet initializes the application with the necessary extensions.
    """
    db.init_app(app)
    migrate.init_app(app, db, command="mg")
    marshmallow.init_app(app)

    """
    The following code snippet registers the blueprints for the application.
    """
    app.register_blueprint(api, url_prefix='/sales')
    app.register_blueprint(commands)

    """
    The following code snippet initializes the application context and imports the models.
    """
    with app.app_context():
        from app.models import user

    return app
