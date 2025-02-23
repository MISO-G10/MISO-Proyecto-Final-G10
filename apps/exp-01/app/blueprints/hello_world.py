from flask import jsonify
from flask_jwt_extended import jwt_required, create_access_token, current_user
from app.models.user import User
from . import api
from app.schemas.user import UserSchema


@api.route('/hello', methods=['GET'])
def hello():
    return jsonify({
        'message': 'Hello, world!'
    })


@api.route('/hello-protected', methods=['GET'])
@jwt_required()
def hello_protected():
    user_schema = UserSchema()
    return user_schema.dump(current_user)


@api.route('/hello-create-jwt', methods=['POST'])
def hello_create_jwt():
    user = User.query.first()

    access_token = create_access_token(identity=user)

    return jsonify({'access_token': access_token})
