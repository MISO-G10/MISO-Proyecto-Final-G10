from src.db.session import SessionLocal
from src.models.ruta import Ruta
from sqlalchemy import select

def initialize_rutas():
    """
    Inicializa las rutas predeterminadas en la base de datos si no existen.
    """
    session = SessionLocal()
    
    # Definir las rutas predeterminadas
    default_rutas = [
        {
            "nombre": "Ruta A",
            "placa": "GLO-887",
            "conductor": "Pedro Ruiz"
        },
        {
            "nombre": "Ruta B",
            "placa": "CXA-456", 
            "conductor": "Jimena Gutierrez"
        },
        {
            "nombre": "Ruta C",
            "placa": "BDP-307",
            "conductor": "Hernando Garcia"
        }
    ]
    
    try:
        # Verificar cuáles rutas ya existen
        for ruta_data in default_rutas:
            # Buscar si la ruta ya existe por su nombre y placa
            stmt = select(Ruta).where(
                Ruta.nombre == ruta_data["nombre"],
                Ruta.placa == ruta_data["placa"]
            )
            existing_ruta = session.execute(stmt).scalar_one_or_none()
            
            # Si no existe, crearla
            if not existing_ruta:
                new_ruta = Ruta(**ruta_data)
                session.add(new_ruta)
                print(f"Ruta creada: {ruta_data['nombre']}")
        
        # Guardar cambios en la base de datos
        session.commit()
        print("Inicialización de rutas completada")
    
    except Exception as e:
        session.rollback()
        print(f"Error al inicializar rutas: {str(e)}")
    
    finally:
        session.close()
