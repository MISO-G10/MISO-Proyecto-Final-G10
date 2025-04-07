import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SellersComponent } from './sellers.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { SellerService } from '../../../core/services/seller.services';
import { provideLocationMocks } from '@angular/common/testing';
describe('SellersComponent', () => {
  let component: SellersComponent;
  let fixture: ComponentFixture<SellersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SellersComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        provideLocationMocks(),
        { provide: SellerService, useValue: {} }, // Mock básico
        { provide: ActivatedRoute, useValue: {} } // Mock para ActivatedRoute
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SellersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});


