import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { getErrorMessages } from '../../../shared/validators/error-messages';
import validaciones from '../../../shared/validators/error_validators/login-validator';
import { AuthService } from '../../../core/auth/auth.service';
import { localeProvider } from '../../../core/locale/locale.provider';
import { setLocaleData } from '../../../core/locale/set-locale-data';
import { Locale } from '../../../core/locale/locale';
import { localeStorage } from '../../../core/locale/locale.storage';
import { MatSelectModule } from '@angular/material/select';

@Component({
  selector: 'app-login',
  imports: [CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    FormsModule,  
    MatSelectModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  readonly translations = {
    usuario: $localize`:@@login.usuario:Usuario`,
    contrasena: $localize`:@@login.contrasena:Contraseña`,
    ingresar: $localize`:@@login.ingresar:Ingresar`,
    olvido_contrasena: $localize`:@@login.olvido_contrasena:¿Olvidó su contraseña?`
  };

  readonly currentLocale = localeStorage.getLocale();

  readonly locales: { id: 'en' | 'es'; label: string }[] = [
    { id: 'en', label: 'English' },
    { id: 'es', label: 'Español' }
  ];

  getErrorMessages = getErrorMessages;
  validaciones: { [key: string]: { type: string; message: string }[] } = validaciones();

  hide = true;
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

  loginForm = this.fb.group({
    email: ['', [Validators.required,
      Validators.pattern(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/)]
    ],
    password: ['', [Validators.required]]
  });

  async handleLocaleChange(locale: Locale) {
    localeProvider.state = 'loading';

    await setLocaleData(locale);

    // Reload the current route to recalculate translations.
    await this.router.navigateByUrl(this.router.url, {
      onSameUrlNavigation: 'reload'
    });

    console.log({ locale });
    localeProvider.state = 'translated';
  }

  onLogin() {
    if (this.loginForm.valid) {
      const { email, password } = this.loginForm.value;
      this.authService.login(email!, password!);
    }
  }
}
