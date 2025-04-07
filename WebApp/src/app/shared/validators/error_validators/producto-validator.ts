export const  validaciones = {
    nombre: [
      { type: 'required', message: 'El nombre es requerido' },
      { type: 'maxlength', message: 'Máximo 50 caracteres' }
    ],
    descripcion: [
      { type: 'required', message: 'La descripción es requerida' },
      { type: 'maxlength', message: 'Máximo 50 caracteres' }
    ],
    perecedero: [
      { type: 'required', message: 'Indique si el producto es perecedero' }
    ],
    fechaVencimiento: [
      { type: 'required', message: 'La fecha de vencimiento es requerida' },
      { type: 'fechaInvalida', message: 'La fecha debe ser posterior a hoy' }
    ],
    valorUnidad: [
      { type: 'required', message: 'El valor unitario es requerido' }
    ],
    tiempoEntrega: [
      { type: 'required', message: 'El tiempo de entrega es requerido' }
    ],
    condicionAlmacenamiento: [
      { type: 'required', message: 'La condición de almacenamiento es requerida' }
    ],
    reglasLegales: [
      { type: 'required', message: 'Las reglas legales son requeridas' }
    ],
    reglasComerciales: [
      { type: 'required', message: 'Las reglas comerciales son requeridas' }
    ],
    reglasTributarias: [
      { type: 'required', message: 'Las reglas tributarias son requeridas' }
    ],
    categoria: [
      { type: 'required', message: 'La categoría es requerida' }
    ]
  };