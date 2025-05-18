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
- `POST /usuarios/login` - Iniciar sesión
- `POST /usuarios/logout` - Cerrar sesión
- `POST /usuarios/signup` - Registrar nuevo usuario
- `GET /usuarios` - Listar todos los usuarios
- `GET /usuarios/{id}` - Obtener usuario por ID
- `PUT /usuarios/{id}` - Actualizar usuario
- `DELETE /usuarios/{id}` - Eliminar usuario
- `GET /usuarios/batch` - Obtener información de múltiples usuarios
- `GET /usuarios/roles` - Obtener roles disponibles
- `POST /usuarios/reset-password` - Solicitar reset de contraseña

### Servicio de Visitas
- `GET /asignaciones/mis-tenderos` - Obtener tenderos asignados a un vendedor
- `POST /asignaciones` - Crear nueva asignación vendedor-tendero
- `PUT /asignaciones/{id}` - Actualizar asignación
- `DELETE /asignaciones/{id}` - Eliminar asignación
- `GET /visitas` - Listar visitas
- `POST /visitas` - Registrar nueva visita
- `GET /visitas/{id}` - Obtener detalles de una visita
- `GET /visitas/vendedor/{id}` - Obtener visitas por vendedor
- `GET /visitas/tendero/{id}` - Obtener visitas por tendero
- `GET /visitas/estadisticas` - Obtener estadísticas de visitas

### Servicio de Ventas
- `GET /pedidos` - Listar todos los pedidos
- `POST /pedidos` - Crear nuevo pedido
- `GET /pedidos/{id}` - Obtener detalles de pedido
- `PUT /pedidos/{id}` - Actualizar estado de pedido
- `GET /pedidos/vendedor/{id}` - Obtener pedidos por vendedor
- `GET /pedidos/tendero/{id}` - Obtener pedidos por tendero
- `GET /ventas` - Listar todas las ventas
- `POST /ventas` - Registrar nueva venta
- `GET /ventas/estadisticas` - Obtener estadísticas de ventas
- `GET /ventas/reporte` - Generar reporte de ventas

### Servicio de Inventarios
- `GET /productos` - Listar todos los productos
- `POST /productos` - Crear nuevo producto
- `POST /productos/bulk` - Crear múltiples productos
- `GET /productos/{id}` - Obtener detalles de producto
- `PUT /productos/{id}` - Actualizar producto
- `DELETE /productos/{id}` - Eliminar producto
- `GET /bodegas` - Listar todas las bodegas
- `POST /bodegas` - Crear nueva bodega
- `GET /bodegas/{id}` - Obtener detalles de bodega
- `POST /bodegas/{id}/productos` - Asignar producto a bodega
- `GET /inventario` - Obtener estado del inventario
- `GET /inventario/productos/{id}` - Obtener inventario por producto
- `GET /inventario/bodegas/{id}` - Obtener inventario por bodega
- `GET /rutas` - Listar rutas de distribución

### Servicio de Fabricantes
- `GET /fabricantes` - Listar todos los fabricantes
- `POST /fabricantes` - Registrar nuevo fabricante
- `GET /fabricantes/{id}` - Obtener detalles de fabricante
- `PUT /fabricantes/{id}` - Actualizar información de fabricante
- `DELETE /fabricantes/{id}` - Eliminar fabricante
- `GET /fabricantes/{id}/productos` - Obtener productos por fabricante

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
