from sqlalchemy import create_engine, inspect
from sqlalchemy.orm import sessionmaker, scoped_session
from src.utils.config import get_config
import os

env_name = os.getenv('FLASK_ENV', 'development')
config = get_config(env_name)

# Configurar la URL de conexión
if env_name == 'test':
    DATABASE_URL = 'sqlite:///:memory:'
else:
    DATABASE_URL = f"postgresql+psycopg2://{config.DB_USER}:{config.DB_PASSWORD}@{config.DB_HOST}:{config.DB_PORT}/{config.DB_NAME}"

print(f"Connecting to database: {DATABASE_URL}")

# Crear el engine con configuración de pool
engine = create_engine(
    DATABASE_URL,
    pool_size=10,        # Aumenta tamaño del pool
    max_overflow=20,     # Permite hasta 20 conexiones adicionales temporales
    pool_timeout=30,     # Espera hasta 30 segundos antes de lanzar TimeoutError
    pool_recycle=1800    # Recicla conexiones para evitar expiración (opcional pero recomendado)
)

def get_inspector(engine):
    return inspect(engine)

# Crear el sessionmaker y el scoped_session
session_factory = sessionmaker(autocommit=False, autoflush=False, bind=engine)
SessionLocal = scoped_session(session_factory)

# Método para obtener la sesión
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()