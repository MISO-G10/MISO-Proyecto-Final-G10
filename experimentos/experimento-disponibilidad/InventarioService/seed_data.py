from models import Producto
from database import db
from sqlalchemy.exc import IntegrityError

# Crear 20 productos de prueba si la tabla está vacía
def seed_database():
    if Producto.query.count() == 0:
        for i in range(1, 21):
            prod = Producto(nombre=f"Producto{i}", stock=100)
            db.session.add(prod)
        try:
            db.session.commit()
            print("Base de datos inicializada con datos de prueba.")
        except IntegrityError:
            db.session.rollback()