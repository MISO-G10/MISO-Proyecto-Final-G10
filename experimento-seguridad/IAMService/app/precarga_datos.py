from app import db
from app.models import User

def precarga_usuarios():
    """ Inserta datos iniciales en la base de datos si está vacía. """
    if not User.query.first():  # Verifica si la tabla está vacía
        usuarios = [
            User(name="Bruce Wayne", uuid="6d124b4a-e110-4ad9-a230-cd890605d64b", username="batman", password="batman", accesslevel="batman"),
            User(name="Ann Takamaki", uuid="770aef5c-6114-4ad5-b0f1-b69b73961b18", username="panther", password="batman", accesslevel="batman"),
            User(name="Jester Lavore", uuid="14cafdcf-ce82-4c7e-a412-d1ad12abb36f", username="little_sapphire", password="batman", accesslevel="batman")
        ]
        
        db.session.bulk_save_objects(usuarios)  # Inserta múltiples registros a la vez
        db.session.commit()
        print("Se insertaron usuarios en la base de datos")
    else:
        print("Ya existen usuarios en la base de datos")
