import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PrivateComponent } from './private.component';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from '../../core/auth/auth.service';
import { MatSnackBarModule } from '@angular/material/snack-bar';

describe('PrivateComponent', () => {
  let component: PrivateComponent;
  let fixture: ComponentFixture<PrivateComponent>;

  // Mock para ActivatedRoute
  const mockActivatedRoute = {
    snapshot: {
      paramMap: new Map(),
      queryParamMap: new Map(),
      data: {}
    }
  };

  // Mock básico para AuthService
  // Cambia las funciones de jest por las de jasmine
const authServiceMock = {
  isAuthenticated: jasmine.createSpy('isAuthenticated').and.returnValue(true),
  getCurrentUser: jasmine.createSpy('getCurrentUser').and.returnValue({
    nombre: 'Test',
    apellido: 'User',
    rol: 'ADMINISTRADOR'
  }),
  logout: jasmine.createSpy('logout')
};

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        PrivateComponent, // Tu componente standalone
        MatSnackBarModule
      ],
      providers: [
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: AuthService, useValue: authServiceMock }, // Usamos el mock
        provideRouter([]),
        provideHttpClient(), // Provee HttpClient
        provideHttpClientTesting() // Para testing de HTTP
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(PrivateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // Puedes agregar más pruebas aquí
  describe('Cuando el usuario cierra sesión', () => {
    it('debería llamar al método logout del AuthService', () => {
      component.logOut();
      expect(authServiceMock.logout).toHaveBeenCalled();
    });
  });
});