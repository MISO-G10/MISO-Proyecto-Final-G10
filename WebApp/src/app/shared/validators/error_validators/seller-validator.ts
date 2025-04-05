export const  validaciones = {
  name: [
    { type: 'required', message: 'El nombre es requerido' },
    { type: 'maxlength', message: 'Máximo 50 caracteres' }
  ],
  lastName: [
    { type: 'required', message: 'El apellido es requerido' },
    { type: 'maxlength', message: 'Máximo 50 caracteres' }
  ],
  email: [
    { type: 'required', message: 'El email es requerido' },
    { type: 'pattern', message: 'Ingrese un email válido' },
    { type: 'maxlength', message: 'Máximo 50 caracteres' }
  ],
  password: [
    { type: 'required', message: 'La contraseña es requerida' },
    { type: 'passwordRequirements', message: 'La contraseña debe tener al menos 8 caracteres y una mayúscula'}
  ],
  confirmPassword: [
    { type: 'required', message: 'Confirme su contraseña' },
    { type: 'passwordMismatch', message: 'Las contraseñas no coinciden' }
  ]
};