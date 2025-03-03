from flask import Flask, jsonify, json
from werkzeug.exceptions import HTTPException

from app.blueprints import api
from app.blueprints.commands import commands
from app.config.application import ApplicationConfig
from app.lib.database import db, migrate
from app.lib.schema import marshmallow
from app.lib.errors import ApiError
from app.lib.jwt import jwt


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

    @app.errorhandler(HTTPException)
    def handle_exception(e):
        response = e.get_response()
        response.data = json.dumps({
            "message": e.description,
        })
        response.content_type = "application/json"
        return response

    """
    The following code snippet initializes the application with the necessary extensions.
    """
    db.init_app(app)
    migrate.init_app(app, db, command="mg")
    marshmallow.init_app(app)
    jwt.init_app(app)

    """
    The following code snippet registers the blueprints for the application.
    """
    app.register_blueprint(api, url_prefix='/exp-01')
    app.register_blueprint(commands)

    """
    The following code snippet initializes the application context and imports the models.
    """
    with app.app_context():
        from app.models import user

    return app
