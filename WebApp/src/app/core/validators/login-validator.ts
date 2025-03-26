export default{
  'email': [
    { type: 'required', message: 'El correo electronico es requerido' },
    { type: 'pattern', message: 'El email no es valido' }
  ],
  'password': [
    { type: 'required', message: 'La contraseña es requerida' },
]
}