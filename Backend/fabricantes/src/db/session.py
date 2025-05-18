from sqlalchemy import create_engine, inspect
from sqlalchemy.orm import sessionmaker, scoped_session
from contextlib import contextmanager
from src.utils.config import get_config
import os

env_name = os.getenv('FLASK_ENV', 'development')
config = get_config(env_name)

# motor de base de datos
print(f"env_name: {env_name}")
if env_name == 'test':
    DATABASE_URL = 'sqlite:///:memory:'  # Usar SQLite en memoria para pruebas
    print(f"Connecting to database: {DATABASE_URL}")
    # SQLite en memoria no soporta los mismos parámetros de pool que PostgreSQL
    engine = create_engine(
        DATABASE_URL,
        # Solo parámetros compatibles con SQLite
        pool_pre_ping=True  # Verificar que la conexión sigue viva antes de usarla
    )
else:
    DATABASE_URL = f"postgresql+psycopg2://{config.DB_USER}:{config.DB_PASSWORD}@{config.DB_HOST}:{config.DB_PORT}/{config.DB_NAME}"
    print(f"Connecting to database: {DATABASE_URL}")
    # Configuración completa del pool de conexiones para PostgreSQL
    engine = create_engine(
        DATABASE_URL,
        pool_size=5,  # Número de conexiones permanentes
        max_overflow=10,  # Número máximo de conexiones adicionales
        pool_timeout=30,  # Tiempo de espera para obtener una conexión
        pool_recycle=3600,  # Reciclar conexiones después de una hora
        pool_pre_ping=True  # Verificar que la conexión sigue viva antes de usarla
    )

def get_inspector(engine):
    return inspect(engine)

# Crear una fábrica de sesiones base
session_factory = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# Crear una sesión con ámbito que se limpiará automáticamente
ScopedSession = scoped_session(session_factory)

# Clase original SessionLocal para mantener compatibilidad
OriginalSessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# Para mantener compatibilidad con el código existente que usa SessionLocal
class SessionLocal:
    """Wrapper para la clase SessionLocal que gestiona automáticamente las conexiones."""
    def __new__(cls):
        # Obtener una sesión del scoped_session
        return ScopedSession()
    
    @staticmethod
    def remove():
        """Elimina todas las sesiones del registro actual."""
        ScopedSession.remove()

# Función para obtener una sesión en un bloque with
@contextmanager
def get_db_context():
    """Proporciona una sesión de base de datos en un contexto que se cierra automáticamente."""
    db = ScopedSession()
    try:
        yield db
    finally:
        db.close()
        ScopedSession.remove()

# Para compatibilidad con el código existente
def get_db():
    db = ScopedSession()
    try:
        yield db
    finally:
        db.close()
        ScopedSession.remove()

# Función para limpiar todas las sesiones
def cleanup_db_sessions():
    """Limpia todas las sesiones activas en el registro."""
    ScopedSession.remove()
