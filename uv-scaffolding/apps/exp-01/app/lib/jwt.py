from flask_jwt_extended import JWTManager

jwt = JWTManager()


@jwt.user_lookup_loader
def user_lookup_callback(_jwt_header, jwt_data):
    from app.models.user import User

    identity = jwt_data["sub"]
    return User.query.filter_by(id=int(identity)).one_or_none()


@jwt.user_identity_loader
def user_identity_lookup(user):
    return str(user.id)
