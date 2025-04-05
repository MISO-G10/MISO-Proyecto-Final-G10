import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateSellerComponent } from './create-seller.component';
import { SnackbarService } from '../../../../shared/ui/snackbar.service';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { SellerService } from '../../../../core/services/seller.services';

describe('CreateSellerComponent', () => {
  let component: CreateSellerComponent;
  let fixture: ComponentFixture<CreateSellerComponent>;
  let mockSnackbarService: jasmine.SpyObj<SnackbarService>;
  
  beforeEach(async () => {
    mockSnackbarService = jasmine.createSpyObj('SnackbarService', ['success', 'error']);
    await TestBed.configureTestingModule({
      imports: [CreateSellerComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        FormBuilder,
        { provide: SellerService, useValue: {} }, // Mock básico
        { provide: SnackbarService, useValue: mockSnackbarService }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateSellerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
