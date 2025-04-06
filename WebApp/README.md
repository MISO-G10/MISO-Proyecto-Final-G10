# WebApp - CCP Gestión

Este proyecto fue generado con [Angular CLI](https://github.com/angular/angular-cli) versión 18.2.9.

## Requisitos previos

Antes de comenzar, asegúrate de tener instalado lo siguiente en tu sistema:

- [Node.js](https://nodejs.org/) (recomendado: versión 18.x o superior)
- [npm](https://www.npmjs.com/) o [yarn](https://yarnpkg.com/)
- Angular CLI (puedes instalarlo globalmente con: `npm install -g @angular/cli`)

## Instalación

Para instalar todas las dependencias del proyecto, ejecuta en la raíz del proyecto:

```bash
npm install
```

Este comando instalará todas las dependencias definidas en el archivo `package.json`, incluyendo las de desarrollo necesarias para compilar, probar y ejecutar la aplicación.

## Servidor de desarrollo

Para iniciar el servidor de desarrollo y ver la aplicación en el navegador:

```bash
ng serve
```

Luego, abre tu navegador y navega a:

```bash
http://localhost:4200/
```

El servidor recargará automáticamente la aplicación si realizas cambios en los archivos fuente.

## Generación de código

Para generar nuevos componentes, directivas, servicios, módulos, etc., utiliza el comando:

```bash
ng generate <tipo> <nombre>
```

Ejemplos:

```bash
ng generate component mi-componente
ng generate service servicios/usuarios
ng generate module shared
```

Consulta `ng help generate` para más opciones.

## Compilación del proyecto

Para compilar el proyecto para producción:

```bash
ng build
```

Los archivos de salida se almacenarán en el directorio `dist/`. Puedes usar el flag `--configuration production` para una compilación optimizada.

## Pruebas unitarias

Para ejecutar las pruebas unitarias usando [Karma](https://karma-runner.github.io/):

```bash
ng test
```

Para generar un reporte de cobertura:

```bash
ng test --code-coverage
```

El reporte se guardará normalmente en `coverage/`.

## Pruebas end-to-end (e2e)

Si deseas ejecutar pruebas end-to-end, primero debes instalar un framework compatible (como Cypress o Protractor) y configurarlo.

Una vez instalado, puedes ejecutar:

```bash
ng e2e
```

## Ayuda adicional

Para más información sobre los comandos de Angular CLI:

```bash
ng help
```

O visita la [documentación oficial de Angular CLI](https://angular.dev/tools/cli).
