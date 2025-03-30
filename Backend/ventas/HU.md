# HUW22-Registrar planes de venta

## Descripción

Como Directora de ventas quiero registrar los planes de venta y asociarlos a los vendedores para hacer un mejor
seguimiento de rendimiento de los vendedores y el cumplimiento de objetivos de la organización.
**Criterios de aceptación**

- **Dado** que el usuario tiene permisos de Directora de Ventas, **cuando** navega al módulo de Planes de Venta, *
  *entonces**debe visualizar la opción “Registrar plan de venta”.
- **Al hacer clic** en “Registrar plan de venta”, el sistema debe mostrar un formulario con los siguientes campos
  obligatorios:
    - Nombre del plan
    - Descripción
    - Objetivo de ventas (monto o unidades)
    - Fecha de inicio
    - Fecha de finalización
    - Vendedores relacionado a el plan
- **Si el usuario deja alguno de los campos obligatorios en blanco**, entonces el sistema debe mostrar un mensaje de
  error indicando que el campo es requerido (por ejemplo, “El nombre del plan es requerido”).
- **Si el usuario ingresa una fecha de finalización anterior a la fecha de inicio**, el sistema debe mostrar un mensaje
  de error “La fecha de finalización debe ser posterior a la fecha de inicio”.
- **Si el usuario selecciona un vendedor que ya está asociado a otro plan activo**, el sistema debe mostrar un mensaje
  de advertencia indicando que el vendedor ya está asignado a otro plan.
- **Si todos los campos se completan correctamente y se hace clic en “Registrar plan de venta”**, el sistema debe:
    - Guardar la información del plan de venta en la base de datos.
    - Mostrar un mensaje de confirmación “Plan de venta registrado exitosamente”.
    - Redirigir al usuario a la lista de planes de venta, donde el nuevo plan debe aparecer en la lista.
- **Al hacer clic en “Cancelar”**, el sistema debe cerrar el formulario o redirigir al usuario a la pantalla anterior
  sin guardar cambios.
