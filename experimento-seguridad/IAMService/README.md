# API de Autenticación y Gestión de Usuarios

Esta es una API desarrollada en **Flask** con autenticación basada en **JWT** (JSON Web Tokens). Permite la gestión de usuarios, autenticación y consulta de información de acceso.

## Características

- Autenticación con **JWT**.
- Base de datos gestionada con **SQLAlchemy**.
- Endpoints para verificar estado del servicio, autenticación y validación de usuarios.
- Soporte para inicialización y reseteo de la base de datos.

## Requisitos

- **Python 3.9+**
- **pip**
- **Docker** (opcional, para despliegue con contenedores)

## Instalación

1. Clona el repositorio:

   ```sh
   git clone <repositorio>
   cd <directorio>
   ```

2. Crea un entorno virtual (opcional pero recomendado):

   ```sh
   python -m venv venv
   source venv/bin/activate  # En Windows: venv\Scripts\activate
   ```

3. Instala las dependencias:

   ```sh
   pip install -r requirements.txt
   ```

4. Ejecuta la aplicación:

   ```sh
   python app.py
   ```

La API correrá en `http://localhost:5000`.

## Uso de la API

### 1. Verificar el estado del servicio

```sh
GET /health
```

**Respuesta:**

```json
{ "status": "ok" }
```

### 2. Obtener un usuario aleatorio

```sh
GET /fetch_users
```

Parámetro opcional: `?rol=<rol>` para filtrar por rol.

### 3. Login y obtención de token

```sh
POST /login
```

**Body:**

```json
{
  "username": "usuario",
  "password": "contraseña"
}
```

**Respuesta:**

```json
{ "token": "<jwt_token>" }
```

### 4. Validar token y obtener permisos

```sh
POST /check_token
```

**Headers:**

```
Authorization: Bearer <jwt_token>
```

**Respuesta:**

```json
{
  "uuid": "uuid_usuario",
  "rol": "rol_usuario"
}
```

### 5. Inicializar la base de datos con datos de prueba

```sh
POST /init_seeder
```

Inserta 100 usuarios de prueba en la base de datos.

### 6. Resetear la base de datos

```sh
POST /reset_db
```

## Despliegue con Docker

### Construir la imagen:

```sh
 docker build -t iamsrv .
```

### Ejecutar el contenedor en el puerto 5001:

```sh
docker run -p 5001:5000 iamsrv
```

La API estará disponible en `http://localhost:5001`.

## Configuración

La aplicación utiliza variables de entorno para la configuración:

- **DATABASE\_URL**: URL de la base de datos (por defecto usa SQLite `sqlite:///db.sqlite3`)
- **JWT\_SECRET\_KEY**: Clave secreta para firmar los tokens JWT

Puedes definirlas en un archivo `.env` o exportarlas manualmente:

```sh
export DATABASE_URL="sqlite:///db.sqlite3"
export JWT_SECRET_KEY="supersecreto"
```

## Tecnologías Utilizadas

- **Flask**: Framework web en Python
- **Flask-JWT-Extended**: Manejo de autenticación con JWT
- **SQLAlchemy**: ORM para manejar la base de datos
- **Docker**: Para contenedorización de la aplicación

