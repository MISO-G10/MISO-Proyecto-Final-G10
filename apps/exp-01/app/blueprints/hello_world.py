from flask import jsonify
from app.models.user import User
from app.schemas.user import UserSchema

from . import api


@api.route('/hello', methods=['GET'])
def hello():
    users = User.query.all()
    user_schema = UserSchema(many=True)

    return jsonify(user_schema.dump(users))
