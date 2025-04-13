export default () => ({
  'email': [
    { type: 'required', message: $localize`:@@login.email_requerido:El correo electronico es requerido` },
    { type: 'pattern', message: $localize`:@@login.email_invalido:El usuario debe ser un email` }
  ],
  'password': [
    { type: 'required', message: $localize`:@@login.contrasena_requerida:La contraseña es requerida` }
  ]
});
