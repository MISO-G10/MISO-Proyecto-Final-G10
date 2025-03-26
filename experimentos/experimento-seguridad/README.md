# Experimento de Seguridad - IAM & Gestión de Inventario

Este proyecto realiza el experimento de seguridad en un entorno de autenticación basado en **JWT** y un servicio de gestión de inventario. Se prueba el acceso con tokens válidos, expirados e inválidos para evaluar la seguridad del sistema y también se tiene una base de datos con usuarios precargados para verificar que únicamente los usuarios con el rol gestion_inventario tengan acceso a las API de consulta de inventario.

## Servicios

El entorno incluye los siguientes servicios:

- **IAMService** (`iamservice`): Servicio de autenticación basado en JWT.
- **GestionInventarioService** (`invservice`): Servicio de gestión de inventario protegido por autenticación JWT.
- **Logger** (`experimento`): Ejecuta las pruebas de seguridad sobre los servicios anteriores.

Todos los servicios se comunican a través de la red `seg-network` y están definidos en `docker-compose.yml`.

---

## ⚙️ **Requisitos**
Antes de comenzar, asegúrate de tener instalado:

- **Docker** ([descargar aquí](https://www.docker.com/get-started))
- **Docker Compose** ([instrucciones aquí](https://docs.docker.com/compose/install/))

---

## Pasos para levantar los contenedores**
Ejecuta el siguiente comando para construir y levantar los servicios en **modo background**:


docker-compose up --build -d

Luego, se debe ejecutar manualmente el experimento una vez los contenedores están corriendo

docker exec -it Logger python /app/app.py

Los resultados del experimento, guardados en el archivo log se pueden copiar en la máquina con el siguiente comando para poderlos visualizar fácilmente

docker cp Logger:/app/log_experimento.json .


Para detener los contenedores se debe usar el siguiente comando

docker-compose down


## Pruebas realizadas en el experimento

- **Autenticación con diferentes usuarios.**  
- **Validación de tokens válidos, inválidos y expirados.**  
- **Acceso con y sin token a `GestionInventarioService`.**  
- **Registro detallado de respuestas en `log_experimento.json`.**  
