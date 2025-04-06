export default{
  'email': [
    { type: 'required', message: 'El correo electronico es requerido' },
    { type: 'pattern', message: 'El usuario debe ser un email' }
  ],
  'password': [
    { type: 'required', message: 'La contraseña es requerida' },
]
}