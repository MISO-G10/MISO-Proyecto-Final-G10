export default () => ({
  name: [
    { type: 'required', message: $localize`:@@private.seller.create.form.f1.e1:El nombre es requerido` },
    { type: 'maxlength', message: $localize`:@@private.seller.create.form.f1.e2:Este campo admite hasta 50 caracteres` }
  ],
  lastName: [
    { type: 'required', message: $localize`:@@private.seller.create.form.f2.e1:El apellido es requerido` },
    { type: 'maxlength', message: $localize`:@@private.seller.create.form.f2.e2:Este campo admite hasta 50 caracteres` }
  ],
  email: [
    { type: 'required', message: $localize`:@@private.seller.create.form.f3.e1:El correo es requerido` },
    { type: 'pattern', message: $localize`:@@private.seller.create.form.f3.e2:El correo es inválido` },
    { type: 'maxlength', message: $localize`:@@private.seller.create.form.f3.e3:Este campo admite hasta 50 caracteres` }
  ],
  password: [
    { type: 'required', message: $localize`:@@private.seller.create.form.f4.e1:La contraseña es requerida` },
    { type: 'passwordRequirements', message: $localize`:@@private.seller.create.form.f4.e2:La contraseña debe tener al menos 8 caracteres y una letra mayúscula` }
  ],
  confirmPassword: [
    { type: 'required', message: $localize`:@@private.seller.create.form.f5.e1:Debe confirmar su contraseña` },
    { type: 'passwordMismatch', message: $localize`:@@private.seller.create.form.f5.e2:Las contraseñas no coinciden` }
  ]
});
