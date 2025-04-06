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

describe('CreateFabricanteComponent', () => {
  let component: CreateFabricanteComponent;
  let fixture: ComponentFixture<CreateFabricanteComponent>;
  let routerSpy = { navigate: jasmine.createSpy('navigate') };
  let snackbarServiceSpy = jasmine.createSpyObj('SnackbarService', ['success', 'error']);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule,
        MatButtonModule,
        BrowserAnimationsModule
      ],
      providers: [
        { provide: Router, useValue: routerSpy },
        { provide: SnackbarService, useValue: snackbarServiceSpy }
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
    expect(component.fabricanteForm.get('email')?.value).toBe('');
    expect(component.fabricanteForm.get('legalRepresentative')?.value).toBe('');
    expect(component.fabricanteForm.get('address')?.value).toBe('');
    expect(component.fabricanteForm.get('phone')?.value).toBe('');
  });

  it('should validate required fields', () => {
    const nameControl = component.fabricanteForm.get('name');
    const emailControl = component.fabricanteForm.get('email');
    const legalRepControl = component.fabricanteForm.get('legalRepresentative');
    const addressControl = component.fabricanteForm.get('address');
    
    nameControl?.setValue('');
    emailControl?.setValue('');
    legalRepControl?.setValue('');
    addressControl?.setValue('');
    
    expect(nameControl?.valid).toBeFalsy();
    expect(emailControl?.valid).toBeFalsy();
    expect(legalRepControl?.valid).toBeFalsy();
    expect(addressControl?.valid).toBeFalsy();
    expect(component.fabricanteForm.valid).toBeFalsy();
  });

  it('should validate email format', () => {
    const emailControl = component.fabricanteForm.get('email');
    
    emailControl?.setValue('invalid-email');
    expect(emailControl?.valid).toBeFalsy();
    
    emailControl?.setValue('valid@email.com');
    expect(emailControl?.valid).toBeTruthy();
  });

  it('should navigate to fabricantes page on cancel', () => {
    component.onCancelCreate();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/private/fabricantes']);
  });
});
