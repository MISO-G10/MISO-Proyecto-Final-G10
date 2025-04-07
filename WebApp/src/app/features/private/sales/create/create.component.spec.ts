import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatChipsModule } from '@angular/material/chips';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { SalesService } from '../sales.service';
import { CreateComponent } from './create.component';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

describe('CreateComponent', () => {
  let component: CreateComponent;
  let fixture: ComponentFixture<CreateComponent>;
  let routerSpy = { navigate: jasmine.createSpy('navigate') };
  let salesServiceSpy = jasmine.createSpyObj('SalesService', ['createSale']);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        FormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule,
        MatButtonModule,
        MatDatepickerModule,
        MatNativeDateModule,
        MatAutocompleteModule,
        MatChipsModule,
        BrowserAnimationsModule
      ],
      providers: [
        { provide: Router, useValue: routerSpy },
        { provide: SalesService, useValue: salesServiceSpy },
        provideHttpClient(), // Base HTTP provider
        provideHttpClientTesting() // Testing extensions
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(CreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the form with empty fields', () => {
    expect(component.createForm.get('nombre')?.value).toBe('');
    expect(component.createForm.get('descripcion')?.value).toBe('');
    expect(component.createForm.get('valor_objetivo')?.value).toBe('');
    expect(component.createForm.get('fecha_inicio')?.value).toBe('');
    expect(component.createForm.get('fecha_fin')?.value).toBe('');
    expect(component.sellerIds.length).toBe(0);
  });

  it('should validate required fields', () => {
    const nombreControl = component.createForm.get('nombre');
    const descripcionControl = component.createForm.get('descripcion');
    const valorObjetivoControl = component.createForm.get('valor_objetivo');
    const fechaInicioControl = component.createForm.get('fecha_inicio');
    const fechaFinControl = component.createForm.get('fecha_fin');

    nombreControl?.setValue('');
    descripcionControl?.setValue('');
    valorObjetivoControl?.setValue('');
    fechaInicioControl?.setValue('');
    fechaFinControl?.setValue('');

    expect(nombreControl?.valid).toBeFalsy();
    expect(descripcionControl?.valid).toBeFalsy();
    expect(valorObjetivoControl?.valid).toBeFalsy();
    expect(fechaInicioControl?.valid).toBeFalsy();
    expect(fechaFinControl?.valid).toBeFalsy();
    expect(component.createForm.valid).toBeFalsy();
  });

  it('should validate valor_objetivo numeric format', () => {
    const valorObjetivoControl = component.createForm.get('valor_objetivo');

    valorObjetivoControl?.setValue('invalid-value');
    expect(valorObjetivoControl?.valid).toBeFalsy();

    valorObjetivoControl?.setValue('0');
    expect(valorObjetivoControl?.valid).toBeFalsy();

    valorObjetivoControl?.setValue('100');
    expect(valorObjetivoControl?.valid).toBeTruthy();

    valorObjetivoControl?.setValue('100.50');
    expect(valorObjetivoControl?.valid).toBeTruthy();
  });

  it('should format date correctly', () => {
    const testDate = new Date(2023, 5, 15); // June 15, 2023
    const formattedDate = component.formatDate(testDate);
    expect(formattedDate).toBe('2023-06-15');
  });

  it('should add and remove seller correctly', () => {
    const vendor = { id: 1, seller_id: 1, nombre: 'Vendedor 1' };

    // Add a seller
    component.addSeller(vendor);
    expect(component.sellerIds.length).toBe(1);
    expect(component.sellerIds.at(0).value).toBe(1);

    // Check if vendor is detected as selected
    expect(component.isVendorSelected(1)).toBeTrue();
    expect(component.isVendorSelected(2)).toBeFalse();

    // Remove the seller
    component.removeSeller(0);
    expect(component.sellerIds.length).toBe(0);
  });

  it('should get vendor name correctly', () => {
    expect(component.getVendorName(1)).toBe('Vendedor 1');
    expect(component.getVendorName(999)).toBe(999); // Non-existent vendor
  });

  it('should not call service when form is invalid', () => {
    // Reset the form to ensure clean state
    component.createForm.reset();

    // Set up a form with only one field - clearly incomplete
    component.createForm.get('nombre')?.setValue('Test Plan');
    // Leave all other required fields empty

    // Explicitly verify the form is invalid before testing
    expect(component.createForm.valid).toBeFalsy();

    // Reset spy call counters before the actual test
    salesServiceSpy.createSale.calls.reset();
    routerSpy.navigate.calls.reset();

    // Execute the method being tested
    component.onCreate();

    // Check that services weren't called with invalid form
    expect(salesServiceSpy.createSale).not.toHaveBeenCalled();
    expect(routerSpy.navigate).not.toHaveBeenCalled();
  });

  it('should call salesService.createSale and navigate on valid form submit', () => {
    const today = new Date();
    const endDate = new Date();
    endDate.setMonth(today.getMonth() + 1);

    component.createForm.get('nombre')?.setValue('Test Plan');
    component.createForm.get('descripcion')?.setValue('Test Description');
    component.createForm.get('valor_objetivo')?.setValue('1000');
    component.createForm.get('fecha_inicio')?.setValue(today);
    component.createForm.get('fecha_fin')?.setValue(endDate);

    // Add a seller to make the form valid
    const vendor = { id: 1, seller_id: 1, nombre: 'Vendedor 1' };
    component.addSeller(vendor);

    component.onCreate();

    expect(salesServiceSpy.createSale).toHaveBeenCalledWith({
      nombre: 'Test Plan',
      descripcion: 'Test Description',
      valor_objetivo: 1000,
      fecha_inicio: component.formatDate(today),
      fecha_fin: component.formatDate(endDate),
      seller_ids: [1]
    });
    expect(routerSpy.navigate).toHaveBeenCalledWith(['private/sales']);
  });

  it('should reset form on cancel', () => {
    component.createForm.get('nombre')?.setValue('Test Plan');
    component.addSeller({ id: 1, seller_id: 1, nombre: 'Vendedor 1' });

    component.onCancel();

    expect(component.createForm.get('nombre')?.value).toBeFalsy();
    expect(component.sellerIds.length).toBe(0);
  });
});
