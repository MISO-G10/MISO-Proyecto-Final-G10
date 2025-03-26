# Servicio de Gestión de Inventarios

Esta es una API desarrollada en **Flask**. Permite la consulta de inventarios para los productos disponibles.

## Características

- Conexión con el servicio de control de indentidad y acceso para proveer una capa de seguridad.
- Base de datos gestionada con **SQLAlchemy**.
- Endpoints para verificar estado del servicio y consultar inventarios de determinados productos.
- Soporte para inicialización y reseteo de la base de datos.

## Requisitos

- Python 3.9 o superior
- Flask
- SQLAlchemy
- Un gestor de base de datos compatible (por defecto SQLite)

## Instalación

1. Clonar este repositorio:
   ```bash
   git clone <URL_DEL_REPOSITORIO>
   cd <NOMBRE_DEL_REPOSITORIO>
   ```

2. Crear un entorno virtual y activarlo:
   ```bash
   python -m venv venv
   source venv/bin/activate  # En Linux/Mac
   venv\Scripts\activate  # En Windows
   ```

3. Instalar las dependencias:
   ```bash
   pip install -r requirements.txt
   ```

## Configuración

Este servicio utiliza variables de entorno para definir la configuración de la base de datos y el puerto en el que corre la aplicación.

### Variables de entorno

- `DATABASE_URL`: URL de la base de datos (por defecto, SQLite `sqlite:///db.sqlite3`)
- `FLASK_RUN_HOST`: Host en el que corre Flask (por defecto `0.0.0.0`)
- `FLASK_RUN_PORT`: Puerto en el que corre Flask (por defecto `5000`)

## Uso

### 1. Iniciar el servicio

Ejecuta el siguiente comando:
```bash
flask run
```
Por defecto, el servicio estará disponible en `http://localhost:5000`.

### 2. Endpoints disponibles

#### Verificar estado del servicio
```http
GET /health
```
**Respuesta:**
```json
{"status": "ok gestionInventarioService"}
```

#### Consultar inventario de productos
```http
GET /consulta-productos
```
**Respuesta:**
```json
[
  {
    "producto_id": 1,
    "sku": "123456",
    "nombre": "Laptop Dell",
    "descripcion_producto": "Laptop de alta gama",
    "inventario": 10,
    "ubicacion_inventario": "Bodega Central"
  }
]
```

#### Inicializar la base de datos con datos de prueba
```http
POST /init_seeder
```
**Respuesta:**
```json
{"message": "Productos e inventarios insertados correctamente."}
```

#### Resetear la base de datos
```http
POST /reset_db
```
**Respuesta:**
```json
{"message": "Base de datos reiniciada correctamente"}
```

## Docker

### Construcción de la imagen Docker
```bash
docker build -t gestion_inventario .
```

### Ejecución del contenedor
```bash
docker run -p 5001:5001 gestion_inventario
```

El servicio estará disponible en `http://localhost:5001`.