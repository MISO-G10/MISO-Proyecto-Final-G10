from database import db
from models import User
from sqlalchemy.exc import SQLAlchemyError
from faker import Faker


def seed_users(n):
    fake = Faker()
    if not db.session.query(User.id).first():  # Verifica si la BD ya tiene usuarios
        print("No hay usuarios en la BD.")

        try:
            for _ in range(n):
                user = User(
                    uuid=fake.uuid4(),
                    name=fake.name(),
                    username=fake.user_name(),
                    password=fake.password(),
                    accesslevel=fake.random_element(elements=('admin', 'gestor_inventario', 'cliente', 'viewer'))
                )
                db.session.add(user)  # Agrega cada usuario individualmente
            
            db.session.commit()  # Guarda los cambios en la BD
            print(f"{n} usuarios insertados en la BD.")
        
        except SQLAlchemyError as e:
            db.session.rollback()  # Revierte los cambios en caso de error
            print("Error al insertar usuarios en la BD:", str(e))
        
        finally:
            db.session.close()  # Cierra la sesión para liberar recursos
    
    else:
        print("La BD ya tiene usuarios, no se insertaron nuevos.")

    