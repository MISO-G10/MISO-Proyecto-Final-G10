import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SellersComponent } from './sellers.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { SellerService } from '../../../core/services/seller.services';
import { provideLocationMocks } from '@angular/common/testing';
import { of } from 'rxjs';
describe('SellersComponent', () => {
  let component: SellersComponent;
  let fixture: ComponentFixture<SellersComponent>;

  const mockSellers = [
    {
      id: '1',
      nombre: 'Tendero',
      apellido: 'Prueba',
      username: 'tendero@gmail.com',
      rol: 'VENDEDOR',
      telefono: '123456789',
      direccion: 'Calle falsa 123'
    },
    {
      id: '2',
      nombre: 'Otro',
      apellido: 'Usuario',
      username: 'otro@gmail.com',
      rol: 'TENDERO',
    }
  ];

  const sellerServiceMock = {
    listSeller: jasmine.createSpy('listSeller').and.returnValue(of(mockSellers))
  };
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SellersComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        provideLocationMocks(),
        { provide: SellerService, useValue: sellerServiceMock  }, // Mock básico
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


