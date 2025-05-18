# MISO Proyecto Final - Grupo 10

Sistema de gestión para la empresa CCP (Compañía Comercializadora de Productos), que facilita la administración de vendedores, tenderos, productos,visitas,inventarios, fabricantes y clientes.

## Equipo de Desarrollo

| Nombre | Correo |
|--------|---------|
| Oscar Andrés García | oa.garcia2@uniandes.edu.co |
| Felipe Valencia | jf.valencia23@uniandes.edu.co |
| Juan Jiménez | jm.jimenezc12@uniandes.edu.co |
| Paula Alejandra Bohorquez Alfonso | p.bohorqueza@uniandes.edu.co |

## Arquitectura del Sistema

### Visión General

El sistema está construido siguiendo una arquitectura de microservicios, con tres componentes principales:
- Backend (Microservicios)
- Aplicación Móvil (Android)
- Aplicación Web (Angular)


## Componentes del Sistema

### Backend (Microservicios)

El backend está compuesto por varios microservicios independientes:

1. **Servicio de Usuarios**
   - Gestión de usuarios (mediante asignación de roles)
   - Autenticación y autorización mediante tokens de acceso

2. **Servicio de Visitas**
   - Asignaciones vendedor-tendero
   - Registro y consulta de visitas
   - Estadísticas de visitas por vendedor

3. **Servicio de Inventarios**
   - Creación de productos individual y masivo
   - Control de inventario
   - Catálogo de productos
   - Creación de Bodegas
   - Asignación de productos a bodegas
   - Gestión de pedidos

4. **Servicio de Ventas**
   - Gestión de pedidos y ventas
   - Registro de transacciones
   - Historial de ventas

5. **Servicio de Fabricantes**
   - Registro de fabricantes
   - Listado de fabricantes

### Mobile App (Android - Kotlin)

Aplicación móvil para vendedores que incluye:

- Registro de tenderos
- Inicio de sesión para vendedores, tenderos, administradores del sistema
- Listado de tenderos asignados a un vendedorcon información detallada:
  - Datos de contacto
  - Ubicación
  - Historial de visitas
  - Estadísticas
- Registro de visitas de vendedores a tenderos
- Gestión de pedidos para realizar seguimiento
- Catálogo de productos

Tecnologías principales:
- Kotlin
- Retrofit para comunicación con API
- Arquitectura MVVM

### Web App (Angular)

Aplicación web administrativa que permite:

- Gestión de usuarios (mediante asignación de roles)
- Registro de vendedores
- Registro de fabricantes
- Asignación de tenderos a vendedores
- Registro individual y masivo de productos
- Visualización de reportes de venta
- Seguimiento de visitas y ventas

Tecnologías principales:
- Angular 16+
- TypeScript
- Material Design
- NgRx para gestión de estado

## APIs y Endpoints Principales

### Servicio de Usuarios
- `POST /usuarios` - Crear nuevo usuario
- `PATCH /usuarios/{id}` - Actualizar usuario
- `POST /usuarios/auth` - Iniciar sesión
- `GET /usuarios/me` - Validar usuario actual
- `GET /usuarios/{id}` - Obtener usuario por ID
- `GET /usuarios` - Listar usuarios (filtrado por rol según el usuario)
- `POST /usuarios/batch` - Obtener información de múltiples usuarios
- `GET /usuarios/ping` - Health check
- `POST /usuarios/reset` - Restablecer base de datos

### Servicio de Visitas
- `POST /visitas` - Crear nueva visita
- `PUT /visitas/{id}` - Actualizar visita
- `GET /visitas` - Listar visitas
- `GET /visitas/{id}` - Obtener detalles de visita
- `DELETE /visitas/{id}` - Eliminar visita
- `POST /visitas/asignaciones` - Crear asignación vendedor-tendero
- `GET /visitas/asignaciones` - Listar asignaciones
- `PUT /visitas/asignaciones/{id}` - Actualizar asignación
- `GET /visitas/asignaciones/mis-tenderos` - Obtener tenderos asignados al vendedor actual
- `GET /visitas/asignaciones/tenderos/{id}` - Obtener tenderos asignados a un vendedor
- `GET /visitas/asignaciones/vendedor` - Obtener ID del vendedor asignado
- `GET /visitas/ping` - Health check
- `POST /visitas/reset` - Restablecer base de datos

### Servicio de Fabricantes
- `POST /fabricantes` - Crear nuevo fabricante
- `GET /fabricantes` - Listar fabricantes
- `GET /fabricantes/{id}` - Obtener detalles de fabricante
- `DELETE /fabricantes/{id}` - Eliminar fabricante
- `GET /fabricantes/ping` - Health check
- `POST /fabricantes/reset` - Restablecer base de datos

### Colección de Postman
La colección postman se encuentra en el archivo postman_collection.json en este repositorio.

## Infraestructura y Despliegue

### Tecnologías de Infraestructura
- Backend desplegado en contenedores Docker
- Frontend web desplegado en GCP VM con Nginx
- CI/CD automatizado con GitHub Actions
- CORS configurado para permitir comunicación segura entre componentes

### Seguridad
- JWT para autenticación
- CORS configurado para endpoints
- HTTPS en todas las comunicaciones
- Roles y permisos por usuario

### Base de Datos
- PostgreSQL para persistencia de datos
- Esquemas independientes por microservicio
- Backups automatizados

### Monitoreo
- Logs centralizados
- Métricas de rendimiento
- Alertas automáticas
- Dashboard de estado del sistema
