
import os
from flask_sqlalchemy import SQLAlchemy
from app.models.user import User
from flask import Flask, jsonify, request
from flask_jwt_extended import create_access_token, current_user, jwt_required, JWTManager
from precarga_datos import precarga_usuarios 

app = Flask(__name__)
app.config["JWT_SECRET_KEY"] = os.getenv('JWT_SECRET_KEY')
app.config["SQLALCHEMY_DATABASE_URI"] = os.getenv('DATABASE_URL')


#Se inicializa el manager de jwt
jwt = JWTManager(app)
#Se inicializa la base de datos
db = SQLAlchemy(app)
db_path = os.path.join(os.path.dirname(__file__), 'usuarios.db')


#Devuelve el usuario de la BD de acuerdo al identificador en el token de acceso
@jwt.user_lookup_loader
def user_lookup_callback(_jwt_header, jwt_data):
    identity = jwt_data["sub"]
    return User.query.filter_by(uuid=int(identity)).one_or_none()

#Devuelve el identificador del usuario
@jwt.user_identity_loader
def user_identity_lookup(user):
    return str(user.uuid)


'''Método para que un usuario haga login en la aplicación, requiere una combinación usuario y 
contraseña que exista en la base de datos para generar un token de acceso'''
@app.route("/login", methods=["POST"])
def login():
    username = request.json.get("username", None)
    password = request.json.get("password", None)

    # Se verifica si la combinación usuario y contraseña proporcionada existe en la bd
    usuario = User.query.filter_by(usuario=usuario, password=password).first()
    if usuario:
        access_token = create_access_token(identity=userid)
        return jsonify({"token": access_token}), 200
    else:
        return jsonify({"mensaje": "Credenciales inválidas"}), 401    


'''Método para verificar que el token de acceso exista en la consulta y sea de un usuario válido
si es válido retorna las reglas de acceso de dicho usuario'''
@app.route("/validar_token", methods=["GET"])
@jwt_required()
def validar_token():
    return jsonify(
        uuid=current_user.uuid,
        reglas=current_user.reglas
    )
    

if __name__ == "__main__":
    #Se inicializan las bases de datos con data precargada en caso de estar vacía
    with app.app_context():
        db.create_all()
        precarga_usuarios() 
    app.run()
