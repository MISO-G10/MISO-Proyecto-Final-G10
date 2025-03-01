# Experimento de Disponibilidad: Ping-Echo entre Monitor y InventarioService

Este experimento tiene como objetivo evaluar la disponibilidad y la latencia del servicio **InventarioService** mediante la implementación del patrón **ping-echo**. El componente **Monitor** envía pings periódicos a InventarioService utilizando Celery y Redis para la comunicación asíncrona, y mide el tiempo de respuesta con `time.perf_counter()`. Se espera detectar fallos o demoras en la respuesta en menos de 2 segundos.

## Instalación

- Asegúrese de tener Docker instalado en su máquina
- Ejecute: ```docker-compose up --build````
- El servicio InventarioService correrá en el puerto 3001 y el servicio Monitor en el puerto 3002

Puede consultar los servicios de cada microservicio:

```Bash
http://localhost:3001/inventario
```

```Bash
http://localhost:3002/monitor
```

## Generalidades

El sistema está compuesto por los siguientes microservicios y componentes:

- **InventarioService**:
  - Implementado con Flask y Flask-SQLAlchemy.
  - Gestiona una base de datos SQLite (con una tabla `productos`) y precarga 20 registros de prueba mediante `seed_data.py`.
  - Define una tarea Celery (`obtener_estado`) para responder a los pings.
- **InventarioWorker**:
  - Se ejecuta desde el mismo código que InventarioService.
  - Levanta un worker de Celery que procesa las tareas encoladas por Monitor.
- **MonitorService**:
  - Implementado con Flask.
  - Envía pings periódicos (usando un hilo en background) a InventarioService a través de Celery y mide el tiempo de respuesta.
  - Además, expone un endpoint `/monitor` para ver manualmente el estado del InventarioService.
- **Redis**:
  - Se utiliza como broker y backend para Celery.
- **Locust**:
  - Herramienta para realizar pruebas de carga simulando múltiples usuarios.
- **Redis Insight**:
  - Para monitorear en tiempo real la instancia de Redis (opcional para análisis).

## Estructura del Proyecto

```plaintext
experimento-disponibilidad/
├── docker-compose.yml
├── README.md
├── api-gateway-config.yaml    # (Configuración para despliegue en GCP, opcional)
├── InventarioService/
│   ├── Dockerfile
│   ├── requirements.txt
│   ├── app.py                 # Contiene la aplicación Flask, configuración de Celery y la inicialización (db.create_all() + seed_database())
│   ├── models.py              # Definición del modelo Producto
│   ├── database.py            # Contiene la instancia: db = SQLAlchemy()
│   ├── seed_data.py           # Función seed_database() para precargar 20 productos
├── MonitorService/
│   ├── Dockerfile
│   ├── requirements.txt
│   └── app.py                 # Implementa el endpoint /monitor y el hilo que envía pings continuamente
└── locustfile.py              # Script para ejecutar pruebas de carga con Locust
```

## Instrucciones para Ejecutar Pruebas de Carga con Locust, y ver sus respectivas métricas

El experimento se centra en garantizar que el componente Monitor reciba y procese el estado del InventarioService en tiempo real, detectando fallas en menos de 2 segundos. Por ello, las pruebas de carga se enfocan en:

1. El mecanismo ping-echo implementado en Monitor:

- Objetivo: Verificar que, bajo alta frecuencia o demanda de pings, el Monitor pueda seguir enviando peticiones, recibir las respuestas de InventarioService y medir la latencia sin que se exceda el umbral de 2 segundos.
- Pruebas: Simular múltiples pings concurrentes para evaluar la capacidad de respuesta y la estabilidad del proceso de verificación de salud.

2. El comportamiento de InventarioService bajo carga:

- Objetivo: Asegurarse de que InventarioService mantenga su capacidad de respuesta ante múltiples peticiones simultáneas, ya que un retraso en este servicio repercutiría directamente en la capacidad del Monitor para detectar fallas rápidamente.

- Pruebas: Generar una carga elevada sobre el endpoint de InventarioService para medir tiempos de respuesta y detectar posibles cuellos de botella.

Esta herramienta se utilizará para simular pruebas de carga sobre el proceso de ping-echo del Monitor (ya que es el encargado de iniciar las peticiones y evaluar la disponibilidad) y, de forma complementaria, sobre InventarioService para evaluar cómo impacta su rendimiento en la detección de fallos. Esto permitirá obtener un análisis integral de la capacidad del sistema para responder en tiempo real bajo condiciones de alta demanda.

## Requisitos Previos

- **Python 3.9 o superior**
- Acceso a la terminal (Linux, Mac o Windows con Git Bash, PowerShell, etc.)
- El proyecto contiene un archivo `locustfile.py` en la raíz, configurado para simular las peticiones.

## Configuración del Entorno Virtual y Locust

Para evitar conflictos con otras dependencias y asegurar que se instale Locust en un entorno aislado, se recomienda utilizar un entorno virtual.

### 1. Crear y Activar el Entorno Virtual

Abre una terminal en la raíz del proyecto y ejecuta:

```bash
python3 -m venv venv
source venv/bin/activate
# con el entorno virtual activo, instala Locust ejecutando:
pip install --upgrade pip
pip install locust
# ejecutar Locust:
locust -f locustfile.py --host http://localhost:3002
```

### 2. Acceder a la interfaz Web de Locust

Una vez iniciado, Locust mostrará un mensaje indicando que la interfaz web está disponible en <http://localhost:8089>.

Abre tu navegador y visita <http://localhost:8089>.

Desde allí podrás:

- Configurar el número de usuarios concurrentes.
- Establecer la tasa de spawn (usuarios por segundo).
- Iniciar y detener la prueba de carga.
- Ver métricas en tiempo real (tiempo de respuesta, tasa de fallos, etc.).

**Nota:** Se ha creado un archivo `run-locust.sh` en la raiz del experimento para simplificar el proceso.
Para ejecutarlo debes darle permisos de acceso con:

```bash
chmod +x run-locust.sh
```

Luego ejecuta el script:

```bash
./run_locust.sh
```

## Instrucciones para revisar métricas con Redis Insight

El propósito de Redis Insight en este experimento es revisar que las colas de mensajes se estén ejecutando correctamente y no existan problemas de congestionamiento.

- Descargar Redis Insight: <https://redis.io/insight/>
- Correr el ejecutable para instalarlo, y luego abre la aplicación
- Asegurarse de que se está corriendo en el puerto por defecto: 6379

**Nota:**

También se puede monitorear los pings y sus respuestas mediante la consola de logs de docker-compose:

e.g ejecutando:

```bash
docker-compose logs -f monitor-service
```

obtendrá respuestas con si respectiva latencia en segundos:

```bash
monitor-service | [OK] Ping recibido en 0.005 s. Respuesta: InventarioService responde correctamente - OK
