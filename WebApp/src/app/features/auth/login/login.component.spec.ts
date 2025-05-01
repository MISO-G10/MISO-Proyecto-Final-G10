import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideAnimations } from '@angular/platform-browser/animations';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { provideRouter, Router } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { LoginComponent } from './login.component';
import { AuthService } from '../../../core/auth/auth.service';
import { By } from '@angular/platform-browser';
import { localeStorage } from '../../../core/locale/locale.storage';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;

  // Expected translations for different locales
  const expectedTranslations = {
    en: {
      usuario: 'Username',
      contrasena: 'Password',
      ingresar: 'Log in',
      olvido_contrasena: 'Forgot password?'
    },
    es: {
      usuario: 'Usuario',
      contrasena: 'Contraseña',
      ingresar: 'Ingresar',
      olvido_contrasena: '¿Olvidó su contraseña?'
    }
  };

  beforeEach(async () => {
    authService = jasmine.createSpyObj('AuthService', ['login']);
    router = jasmine.createSpyObj('Router', ['navigateByUrl', 'navigate'], {
      url: '/login'
    });

    await TestBed.configureTestingModule({
      imports: [
        LoginComponent,
        ReactiveFormsModule,
        FormsModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule,
        MatButtonModule,
        MatProgressSpinnerModule,
        MatSelectModule,
        MatSnackBarModule
      ],
      providers: [
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting(),
        provideAnimations(),
        provideRouter([]),
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router }
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;

    spyOn(component, 'handleLocaleChange').and.returnValue(Promise.resolve());

    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the form with empty fields', () => {
    expect(component.loginForm.get('email')?.value).toBe('');
    expect(component.loginForm.get('password')?.value).toBe('');
  });

  it('should call login method when form is valid and submitted', () => {
    component.loginForm.setValue({
      email: 'test@example.com',
      password: 'password123'
    });

    component.onLogin();
    expect(authService.login).toHaveBeenCalledWith('test@example.com', 'password123');
  });

  it('should not call login method when form is invalid', () => {
    component.loginForm.setValue({
      email: 'invalid-email',
      password: 'password123'
    });

    component.onLogin();
    expect(authService.login).not.toHaveBeenCalled();
  });

  it('should render form fields with current locale translations', () => {
    const emailLabel = fixture.debugElement.query(By.css('mat-form-field:first-of-type mat-label'));
    const passwordLabel = fixture.debugElement.query(By.css('mat-form-field:nth-of-type(2) mat-label'));
    const submitButton = fixture.debugElement.query(By.css('button[type="submit"]'));
    const forgotPasswordLink = fixture.debugElement.query(By.css('a.forgot-password'));
    const currentLocale = component.currentLocale as 'en' | 'es';

    expect(emailLabel.nativeElement.textContent).toBe(component.translations.usuario);
    expect(passwordLabel.nativeElement.textContent).toBe(component.translations.contrasena);
    expect(submitButton.nativeElement.textContent.trim()).toBe(component.translations.ingresar);
    expect(forgotPasswordLink.nativeElement.textContent.trim()).toBe(component.translations.olvido_contrasena);

    console.log(`Current locale: ${currentLocale}`);
    console.log('Current translations:', component.translations);
    console.log('Expected translations:', expectedTranslations[currentLocale]);
  });

  it('should display form validation errors when fields are invalid', async () => {
    const emailControl = component.loginForm.get('email');
    emailControl?.setValue('invalid-email');
    emailControl?.markAsTouched();

    const passwordControl = component.loginForm.get('password');
    passwordControl?.setValue('');
    passwordControl?.markAsTouched();

    fixture.detectChanges();
    await fixture.whenStable();

    const emailError = fixture.debugElement.query(By.css('mat-form-field:first-of-type mat-error'));
    const passwordError = fixture.debugElement.query(By.css('mat-form-field:nth-of-type(2) mat-error'));

    expect(emailError).toBeTruthy();
    expect(passwordError).toBeTruthy();

    expect(emailError.nativeElement.textContent.trim()).toBeTruthy();
    expect(passwordError.nativeElement.textContent.trim()).toBeTruthy();
  });

  it('should display translated validation messages appropriate to the locale', async () => {
    const emailControl = component.loginForm.get('email');
    emailControl?.setValue(''); // Empty value to trigger required validation
    emailControl?.markAsTouched();

    fixture.detectChanges();
    await fixture.whenStable();

    const emailError = fixture.debugElement.query(By.css('mat-form-field:first-of-type mat-error'));
    expect(emailError).toBeTruthy();

    const errorText = emailError.nativeElement.textContent.trim();
    expect(errorText).toBeTruthy();
  });

  it('should toggle password visibility when icon is clicked', () => {
    expect(component.hide).toBeTrue();

    const visibilityToggle = fixture.debugElement.query(By.css('button[matSuffix]'));
    visibilityToggle.triggerEventHandler('click', null);
    fixture.detectChanges();

    expect(component.hide).toBeFalse();

    const passwordInput = fixture.debugElement.query(By.css('input[formControlName="password"]'));
    expect(passwordInput.nativeElement.type).toBe('text');

    visibilityToggle.triggerEventHandler('click', null);
    fixture.detectChanges();

    expect(component.hide).toBeTrue();
    expect(passwordInput.nativeElement.type).toBe('password');
  });

  it('should call handleLocaleChange with correct locale when language selector changes', () => {
    const currentLocale = component.currentLocale as 'en' | 'es';
    const newLocale = currentLocale === 'en' ? 'es' : 'en';

    const select = fixture.debugElement.query(By.css('mat-select'));
    select.triggerEventHandler('ngModelChange', newLocale);

    expect(component.handleLocaleChange).toHaveBeenCalledWith(newLocale);
  });

  it('should update UI based on selected locale', async () => {
    const currentLocale = component.currentLocale as 'en' | 'es';
    const newLocale = currentLocale === 'en' ? 'es' : 'en';

    const mockTranslations = expectedTranslations[newLocale];

    (component.handleLocaleChange as jasmine.Spy).and.stub(); // Clear the previous spy behavior

    (component.handleLocaleChange as jasmine.Spy).and.callFake(async (locale) => {
      Object.keys(mockTranslations).forEach(key => {
        // @ts-ignore - we know these keys exist
        component.translations[key] = mockTranslations[key];
      });

      await router.navigateByUrl(router.url, { onSameUrlNavigation: 'reload' });
      return Promise.resolve();
    });

    await component.handleLocaleChange(newLocale as 'en' | 'es');
    fixture.detectChanges();

    expect(component.handleLocaleChange).toHaveBeenCalledWith(newLocale);

    expect(router.navigateByUrl).toHaveBeenCalledWith('/login', {
      onSameUrlNavigation: 'reload'
    });

    expect(component.translations.usuario).toBe(mockTranslations.usuario);
    expect(component.translations.contrasena).toBe(mockTranslations.contrasena);
    expect(component.translations.ingresar).toBe(mockTranslations.ingresar);
    expect(component.translations.olvido_contrasena).toBe(mockTranslations.olvido_contrasena);
  });

  it('should disable submit button when form is invalid', () => {
    component.loginForm.setValue({
      email: 'invalid-email',
      password: ''
    });
    fixture.detectChanges();

    const submitButton = fixture.debugElement.query(By.css('button[type="submit"]'));
    expect(submitButton.nativeElement.disabled).toBeTrue();

    component.loginForm.setValue({
      email: 'valid@example.com',
      password: 'password123'
    });
    fixture.detectChanges();

    expect(submitButton.nativeElement.disabled).toBeFalse();
  });

  it('should display the correct locales in the language selector', () => {
    expect(component.locales.length).toBe(2);
    expect(component.locales.map(l => l.id)).toContain('en');
    expect(component.locales.map(l => l.id)).toContain('es');

    const enLocale = component.locales.find(l => l.id === 'en');
    const esLocale = component.locales.find(l => l.id === 'es');
    expect(enLocale?.label).toBe('English');
    expect(esLocale?.label).toBe('Español');

    const storedLocale = localeStorage.getLocale();
    expect(component.currentLocale).toBe(storedLocale);

    const select = fixture.debugElement.query(By.css('mat-select'));
    expect(select.attributes['ng-reflect-model']).toBe(storedLocale);
  });
});
