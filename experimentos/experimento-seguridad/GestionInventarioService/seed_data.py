from database import db
from models import Producto, InventarioBodega
from sqlalchemy.exc import SQLAlchemyError
from faker import Faker


def seed_productos_inventario(n):
    fake = Faker()
    if not db.session.query(Producto.id).first(): 
        print("No hay registros en la BD.")

        try:
            for _ in range(n):
                # Se crea un producto
                producto = Producto(
                    sku=fake.unique.ean13(), 
                    nombre=fake.word(),
                    descripcion=fake.sentence()
                )
                db.session.add(producto)
                db.session.flush()  # Asegura que el producto tenga un ID antes de usarlo en InventarioBodega

                # Se crea el registro para asociar un producto con su inventario en bodega
                inventario = InventarioBodega(
                    producto_id=producto.id,
                    cantidad=fake.random_int(min=1, max=100),  
                    ubicacion = fake.address()
                )
                db.session.add(inventario)

            db.session.commit()  
            print(f"{n} productos e inventarios insertados en la BD.")

        except SQLAlchemyError as e:
            db.session.rollback()  
            print(f"Error al insertar registros: {str(e)}")

        finally:
            db.session.close()  # Cierra la sesión para liberar recursos
    
    else:
        print("La BD ya tiene registros, no se insertaron nuevos.")

    