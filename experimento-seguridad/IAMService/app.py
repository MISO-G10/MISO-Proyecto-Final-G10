import os
from flask import Flask, jsonify, request, current_app
from flask_jwt_extended import create_access_token, current_user, jwt_required, JWTManager
from database import db  
from models import User
from seed_data import seed_users


app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = os.environ.get('DATABASE_URL', 'sqlite:///db.sqlite3')
app.config['JWT_SECRET_KEY'] = "secret"
db.init_app(app)
jwt = JWTManager(app)

'''Método para que el JWT Manager identifique la identidad en el token'''
@jwt.user_lookup_loader
def load_user_callback(_jwt_header, jwt_data):
    identity = jwt_data["sub"]
    return User.query.filter_by(uuid=identity).first()

'''Método para verificar el estado del servicio'''
@app.route('/health', methods=['GET'])
def health():
    return {'status': 'ok'}

'''Método para obtener un usuario aleatorio de la bdd. Si se proporciona un rol en el request, se filtra tambièn por ese rol'''
@app.route("/fetch_users", methods=["GET"])
def check_users():
    rol = request.args.get("rol")
    with current_app.app_context():
        query = db.session.query(User)

        if rol:  # Si se proporciona un rol se filtra por ese rol
            query = query.filter(User.accesslevel == rol)

        user = query.order_by(db.func.random()).first()    
        user = db.session.query(User).order_by(db.func.random()).first()  

        if user:
            return jsonify({
                "uuid": user.uuid,
                "username": user.username,
                "password": user.password,
                "accesslevel": user.accesslevel  
            })
        else:
            return jsonify({"message": "No se encontraron usuarios en la base de datos"}), 404
        

'''Método para que un usuario haga login en la aplicación, requiere una combinación usuario y 
contraseña que exista en la base de datos para generar un token de acceso'''
@app.route("/login", methods=["POST"])
def login():
    username = request.json.get("username", None)
    password = request.json.get("password", None)

    # Se verifica si la combinación usuario y contraseña proporcionada existe en la bd
    usuario = User.query.filter_by(username=username, password=password).first()
    if usuario:
        access_token = create_access_token(identity=usuario.uuid)
        return jsonify({"token": access_token}), 200
    else:
        return jsonify({"mensaje": "Credenciales inválidas"}), 401    


'''Método para verificar que el token de acceso exista en la consulta y sea de un usuario válido
si es válido retorna las reglas de acceso de dicho usuario'''
@app.route("/check_token", methods=["POST"])
@jwt_required()
def validar_token():
    if current_user is None:
        return jsonify({"msj": "Usuario no encontrado"}), 401
    return jsonify(
        
        uuid=current_user.uuid,
        rol=current_user.accesslevel
    )

'''Método para inicializar la bdd y cargar usuarios de prueba'''
@app.route("/init_seeder", methods=["POST"])
def seeder():
    with app.app_context():  
        db.create_all()  
        print("Tablas creadas en la BD.")
        seed_users(100)  
        return jsonify({"message": "Usuarios insertados correctamente."})
        
'''Método para limpiar la base de datos y volver a crear las tablas'''
@app.route("/reset_db", methods=["POST"])
def reset_db():
    with current_app.app_context():
        try:
            db.drop_all()  # Eliminar todas las tablas
            db.create_all()  # Volver a crear las tablas
            db.session.commit()
            return jsonify({"message": "Base de datos reiniciada correctamente"}), 200
        
        except Exception as e:
            db.session.rollback()
            return jsonify({"error": f"Error al reiniciar la base de datos: {str(e)}"}), 500       

if __name__ == "__main__":
    with app.app_context():
        db.create_all()
        print("Tablas creadas")        
    app.run(host="0.0.0.0", port=5000, debug=True)