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
  
  it('should load and filter sellers by role VENDEDOR', () => {
    const sellers = component.sellers();
    expect(sellers.length).toBe(1); // Solo uno tiene rol VENDEDOR
    expect(sellers[0].rol).toBe('VENDEDOR');
    expect(sellers[0].nombre).toBe('Tendero');
  });

  it('should filter sellers by search term', () => {
    component.searchTerm.set('ten');
    component.filterSellers();
    const filtered = component.filteredSellers();
    expect(filtered.length).toBe(1);
    expect(filtered[0].username).toContain('tendero');
  });

  it('should return all sellers if search term is empty', () => {
    component.searchTerm.set('');
    component.filterSellers();
    const filtered = component.filteredSellers();
    expect(filtered.length).toBe(1); // Solo hay 1 con rol VENDEDOR
  });

  it('should update filtered sellers when searchTerm changes', () => {
    component.searchTerm.set('123456789');
    component.filterSellers();
    const filtered = component.filteredSellers();
    expect(filtered.length).toBe(1);
    expect(filtered[0].telefono).toBe('123456789');
  });
});


