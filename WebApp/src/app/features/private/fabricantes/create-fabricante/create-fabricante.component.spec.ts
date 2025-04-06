import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { SnackbarService } from '../../../../shared/ui/snackbar.service';
import { CreateFabricanteComponent } from './create-fabricante.component';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { FabricantesService } from '../fabricantes.service';
import { PhoneFormatDirective } from '../../../../shared/directives/phone-format.directive';

describe('CreateFabricanteComponent', () => {
  let component: CreateFabricanteComponent;
  let fixture: ComponentFixture<CreateFabricanteComponent>;
  let routerSpy = { navigate: jasmine.createSpy('navigate') };
  let snackbarServiceSpy = jasmine.createSpyObj('SnackbarService', ['success', 'error']);
  let fabricantesServiceSpy = jasmine.createSpyObj('FabricantesService', ['createFabricante']);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule,
        MatButtonModule,
        BrowserAnimationsModule,
        PhoneFormatDirective
      ],
      providers: [
        { provide: Router, useValue: routerSpy },
        { provide: SnackbarService, useValue: snackbarServiceSpy },
        { provide: FabricantesService, useValue: fabricantesServiceSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(CreateFabricanteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the form with empty fields', () => {
    expect(component.fabricanteForm.get('name')?.value).toBe('');
    expect(component.fabricanteForm.get('legalRepresentative')?.value).toBe('');
    expect(component.fabricanteForm.get('phone')?.value).toBe('');
  });

  it('should validate required fields', () => {
    const nameControl = component.fabricanteForm.get('name');
    const legalRepControl = component.fabricanteForm.get('legalRepresentative');
    const phoneControl = component.fabricanteForm.get('phone');

    nameControl?.setValue('');
    legalRepControl?.setValue('');
    phoneControl?.setValue('');

    expect(nameControl?.valid).toBeFalsy();
    expect(legalRepControl?.valid).toBeFalsy();
    expect(phoneControl?.valid).toBeFalsy();
    expect(component.fabricanteForm.valid).toBeFalsy();
  });

  it('should validate phone format', () => {
    const phoneControl = component.fabricanteForm.get('phone');

    phoneControl?.setValue('invalid-phone');
    expect(phoneControl?.valid).toBeFalsy();

    phoneControl?.setValue('+(57) 311 205-4567');
    expect(phoneControl?.valid).toBeTruthy();
  });

  it('should validate maxlength for name and legalRepresentative', () => {
    const nameControl = component.fabricanteForm.get('name');
    const legalRepControl = component.fabricanteForm.get('legalRepresentative');

    const longString = 'a'.repeat(51); // 51 characters, exceeding the 50 max

    nameControl?.setValue(longString);
    expect(nameControl?.valid).toBeFalsy();
    expect(nameControl?.errors?.['maxlength']).toBeTruthy();

    legalRepControl?.setValue(longString);
    expect(legalRepControl?.valid).toBeFalsy();
    expect(legalRepControl?.errors?.['maxlength']).toBeTruthy();
  });

  it('should clear field when clearField method is called', () => {
    const nameControl = component.fabricanteForm.get('name');
    nameControl?.setValue('Test Value');
    expect(nameControl?.value).toBe('Test Value');

    component.clearField('name');
    expect(nameControl?.value).toBe('');
  });

  it('should not call service when form is invalid', () => {
    // Make form invalid
    component.fabricanteForm.get('name')?.setValue('');
    component.fabricanteForm.get('legalRepresentative')?.setValue('John Doe');
    component.fabricanteForm.get('phone')?.setValue('+(57) 311 205-4567');

    component.onCreateFabricante();

    expect(fabricantesServiceSpy.createFabricante).not.toHaveBeenCalled();
    expect(routerSpy.navigate).not.toHaveBeenCalled();
  });

  it('should get validation errors correctly', () => {
    const nameControl = component.fabricanteForm.get('name');
    const errors = component.getValidationErrors(nameControl, 'name');

    expect(errors.length).toBeGreaterThan(0);
    expect(errors[0].type).toBe('required');
    expect(errors[0].message).toBe('El nombre es requerido');

    // Test with null control
    const nullErrors = component.getValidationErrors(null, 'name');
    expect(nullErrors.length).toBe(0);
  });

  it('should call fabricantesService.createFabricante and navigate on form submit', () => {
    const nameControl = component.fabricanteForm.get('name');
    const legalRepControl = component.fabricanteForm.get('legalRepresentative');
    const phoneControl = component.fabricanteForm.get('phone');

    nameControl?.setValue('Test Company');
    legalRepControl?.setValue('John Doe');
    phoneControl?.setValue('+(57) 311 205-4567');

    component.onCreateFabricante();

    expect(fabricantesServiceSpy.createFabricante).toHaveBeenCalledWith({
      nombre: 'Test Company',
      numeroTel: '573112054567',
      representante: 'John Doe'
    });
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/private/fabricantes']);
  });

  it('should navigate to fabricantes page on cancel', () => {
    component.onCancelCreate();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/private/fabricantes']);
  });
});
