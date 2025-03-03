from flask_jwt_extended import JWTManager,get_jwt_identity,create_access_token
from flask import jsonify
from app.models.user import User

jwt = JWTManager()

'''@jwt.token_in_request_loader
def check_token_exists():
    try:
        verify_jwt_in_request()  # Verifica automáticamente la existencia y formato del token
        return True, None  # Token válido, sin errores
    except Exception as e:
        return False, jsonify({"error": f"Ha ocurrido un error: {str(e)}"}), 401'''
    
#Devuelve el usuario de la BD de acuerdo al identificador en el token de acceso
@jwt.user_lookup_loader
def user_lookup_callback(_jwt_header, jwt_data):
    identity = jwt_data["sub"]
    return User.query.filter_by(uuid=int(identity)).one_or_none()


@jwt.user_identity_loader
def user_identity_lookup(user):
    return str(user.uuid)

def create_token(userid):
    access_token = create_access_token(identity=userid)
    return jsonify(access_token=access_token)