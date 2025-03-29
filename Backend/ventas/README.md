# CCP Gestión - Miso Grupo 10

## Estructura de archivos

```
.
└── apps (microservices
    └── sales
```

## Quick Start

Este proyecto usa [`uv`](https://docs.astral.sh/uv/) como manejador de paquetes.

Primero, debes instalar `uv` en tu sistema. Para hacerlo, sigue las instrucciones en
la [documentación oficial](https://docs.astral.sh/uv/getting-started/installation).

Luego, clona este repositorio y ejecuta el siguiente comando en la raíz del proyecto:

```bash
uv run sync
```

Esto, solo instalará las dependencias a nivel del root del proyecto. Para instalar las dependencias de cada
microservicio, debes ingresar a la carpeta de cada uno y ejecutar el mismo comando. (Esto gracias a que por lo visto
no hay muy buen soporte para monorepos en python sin tantas complicaciones)

## Estructura de los microservicios

Idealmente, cada microservicio debería tener la siguiente estructura:

```
.
└── apps
    └── miso (microservicio)
        ├── .venv
        ├── app
        │   ├── blueprints
        │   │   ├── __init__.py
        │   │   └── health.py
        │   ├── config
        │   │   ├── __init__.py
        │   │   └── application.py
        │   ├── models
        │   │   └── __init__.py
        │   ├── schemas
        │   ├── __init__.py
        │   ├── lib (helpers)
        │   │   ├── __init__.py
        │   │   ├── database.py
        │   │   ├── errors.py
        │   │   └── schema.py
        │   └── app.py
        ├── migrations
        ├── tests
        ├── .env.example
        ├── Dockerfile
        ├── compose.dev.yaml
        ├── compose.prod.yaml
        └── pyproject.toml
```

* Se omiten algunos archivos y carpetas para mantener la simplicidad del ejemplo.

## Configuración

### Variables de entorno

Cada microservicio debe tener un archivo `.env.example` que contenga las variables de entorno necesarias para su
correcto funcionamiento. Simplemente, copia este archivo y renómbralo a `.env` y modifica las variables según sea
necesario.

### Configuración de la aplicación

La configuración de la aplicación se encuentra en el archivo `app/config/application.py`. Aquí se definen las variables,
como el nombre de la aplicación, el puerto en el que se ejecutará, etc. Estas son luego utilizadas por Flask para
configurar SQLAlchemy, y otras librerías. Ten encuenta que primero debes definir las variables de entorno en el archivo
`.env` y posteriormente importarlas en el archivo `application.py` en caso de que desees usarlas.

### Docker

Cada microservicio proveera de un archivo `Dockerfile`, `compose.dev.yaml` y `compose.prod.yaml` para facilitar el
despliegue de la aplicación en diferentes entornos.

#### Ejemplo de ejecución

```bash
docker-compose -f apps/<nombre_del_microservicio>/compose.dev.yaml up
```

Dependiendo del microservicio, puede que sea necesario modificar el archivo `compose.dev.yaml` para ajustar los puertos.

## Ejecución

### Desarrollo

Dado que usamos `uv` no es necesario, como tal, hacer source de un entorno virtual, pero si es necesario tenerlo y `uv`
se encargaría de crearlo y activarlo. Pero, si de verse necesidad de hacerlo, puedes activar el entorno virtual de la
siguiente manera:

```bash
cd apps/<nombre_del_microservicio>
source .venv/bin/activate
```             

Para ejecutar el microservicio en modo desarrollo, sin necesidad de Docker, debes primero ir a la carpeta del
microservicio:

```bash
cd apps/<nombre_del_microservicio>
```

Luego, ejecuta el siguiente comando:

```bash
uv run -- flask run
```

Gracias a que Flask provee la capacidad de adicionar comandos adicionales a su CLI, existen dos comandos adicionales:

```bash
uv run -- flask db
uv run -- flask mg
```

#### db command

`db` es el encargado principalmente de ejecutar seeders.

Contiene los siguientes comandos:

- `seed`: Ejecuta los seeders definidos dentro del archivo `blueprints/commands.py` en la función `seed`.

#### mg command

`mg` provine de de Flask-Migrate y es el encargado de ejecutar las migraciones, como crear tablas, actualizarlas, basado
en cambios en los modelos.

[Documentación oficial](https://flask-migrate.readthedocs.io/en/latest/)


### Pruebas
Usamos `pytest` para correr las pruebas. Para correr las pruebas, dado la naturalidad del proyecto, debes ir a la
carpeta del microservicio y ejecutar el siguiente comando:

Ingresar a la carpeta del microservicio:
```bash
cd apps/<nombre_del_microservicio>
```

Luego, ejecuta el siguiente comando:
```bash
uv run -- pytest
```

## Creación de un nuevo microservicio

Para crear un nuevo microservicio, proveemos de un pequeño CLI que facilita la creación de estos basados en una
plantilla
predefinida en conjunto de cookiescutter.

Para crear un nuevo microservicio, ejecuta el siguiente comando en la carpeta raíz del proyecto:

```bash
uv run cli.py project create <nombre_del_microservicio>
```

`cookiecutter` te hará algunas preguntas, como el nombre del microservicio, la descripción, etc. Una vez respondidas,
la carpeta del microservicio será creada dentro de la carpeta `apps`, y sus dependencias como el entorno virtual,
Serán creadas automáticamente.

En caso de que uses el nombre de un microservicio que ya existe, el CLI arrojará un error.


## Despliegue

WIP
