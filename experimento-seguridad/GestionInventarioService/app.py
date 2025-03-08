import os
from flask import Flask, jsonify, request, current_app
from database import db  
from models import Producto, InventarioBodega
from seed_data import seed_productos_inventario


app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = os.environ.get('DATABASE_URL', 'sqlite:///db.sqlite3')
host = os.environ.get("FLASK_RUN_HOST", "0.0.0.0")  
port = int(os.environ.get("FLASK_RUN_PORT", 5000)) 
db.init_app(app)
        
'''Método para verificar el estado del servicio'''
@app.route('/health', methods=['GET'])
def health():
    return {'status': 'ok gestionInventarioService'}

'''Método para consultar inventario de productos de la base de datos'''
@app.route("/consulta-productos", methods=["GET"])
def consulta_inventario_productos():
    try:
        # Consultar todos los productos con su inventario
        inventario = db.session.query(InventarioBodega, Producto).join(Producto).all()

        
        res = [
            {
                "producto_id": producto.id,
                "sku": producto.sku,
                "nombre": producto.nombre,
                "descripcion_producto": producto.descripcion,
                "inventario": inventario.cantidad,
                "ubicacion_inventario": inventario.ubicacion  
            }
            for inventario, producto in inventario
        ]

        return jsonify(res), 200

    except Exception as e:
        return jsonify({"error": f"Error al consultar inventario: {str(e)}"}), 500


'''Método para inicializar la bdd y cargar datos de prueba'''
@app.route("/init_seeder", methods=["POST"])
def seeder():
    with app.app_context():  
        db.create_all()  
        print("Tablas creadas en la BD.")
        seed_productos_inventario(10)  
        return jsonify({"message": "Productos e inventarios insertados correctamente."})
        
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
    app.run(host=host, port=port, debug=True)