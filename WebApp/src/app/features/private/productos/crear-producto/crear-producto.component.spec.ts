import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProductoService, BulkProductoResponse } from '../../../../core/services/productos.services';
import { environment } from '../../../../../environment/environment';
import { Categoria, Producto } from '../models/producto';

type ProductoTest = Omit<Producto, 'fechaVencimiento'> & { fechaVencimiento: Date | null };

describe('ProductoService', () => {
  let service: ProductoService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProductoService]
    });
    service = TestBed.inject(ProductoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('crearProductosMasivo', () => {
    it('should send bulk products correctly', () => {
      const mockProducts: ProductoTest[] = [
        {
          nombre: 'Producto 1',
          descripcion: 'Descripción 1',
          perecedero: true,
          fechaVencimiento: new Date('2025-12-31T00:00:00'),
          valorUnidad: 1000,
          tiempoEntrega: new Date('2025-06-01T00:00:00'),
          condicionAlmacenamiento: 'Refrigerado',
          reglasLegales: 'Reglas 1',
          reglasComerciales: 'Reglas 2',
          reglasTributarias: 'IVA 19%',
          categoria: Categoria.ALIMENTOS_BEBIDAS,
          fabricante_id: '12345',
          sku: 'TEST123',
          createdAt: new Date().toISOString()
        }
      ];

      const expectedResponse: BulkProductoResponse = {
        products: [{ sku: 'TEST123', createdAt: new Date().toISOString() }],
        total: 1,
        message: 'Productos creados exitosamente'
      };

      service.crearProductosMasivo(mockProducts as unknown as Producto[]).subscribe(response => {
        expect(response).toEqual(expectedResponse);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}:${environment.endpointInventario}/productos/bulk`);
      expect(req.request.method).toBe('POST');

      // Verificar que los datos se transforman correctamente
      const requestBody = req.request.body;
      expect(requestBody.productos[0].fabricante_id).toBe(mockProducts[0].fabricante_id);
      expect(requestBody.productos[0].categoria).toBe('ALIMENTOS_BEBIDAS');

      req.flush(expectedResponse);
    });

    it('should handle validation errors', () => {
      const mockProducts: ProductoTest[] = [
        {
          nombre: 'Producto 1',
          descripcion: 'Descripción 1',
          perecedero: true,
          fechaVencimiento: null,  // Error: producto perecedero sin fecha
          valorUnidad: 1000,
          tiempoEntrega: new Date('2025-06-01T00:00:00'),
          condicionAlmacenamiento: 'Refrigerado',
          reglasLegales: 'Reglas 1',
          reglasComerciales: 'Reglas 2',
          reglasTributarias: 'IVA 19%',
          categoria: Categoria.ALIMENTOS_BEBIDAS,
          fabricante_id: '12345',
          sku: 'TEST123',
          createdAt: new Date().toISOString()
        }
      ];

      const errorResponse = {
        error: 'Errores de validación',
        details: [
          { index: 0, error: { fechaVencimiento: ['Se requiere fecha de vencimiento para productos perecederos'] } }
        ]
      };

      service.crearProductosMasivo(mockProducts as unknown as Producto[]).subscribe({
        next: () => fail('Should have failed with validation error'),
        error: (error) => {
          expect(error.error).toEqual(errorResponse);
        }
      });

      const req = httpMock.expectOne(`${environment.apiUrl}:${environment.endpointInventario}/productos/bulk`);
      expect(req.request.method).toBe('POST');
      req.flush(errorResponse, { status: 400, statusText: 'Bad Request' });
    });

    it('should handle non-perishable products without expiry date', () => {
      const mockProducts: ProductoTest[] = [
        {
          nombre: 'Producto 1',
          descripcion: 'Descripción 1',
          perecedero: false,
          fechaVencimiento: null,
          valorUnidad: 1000,
          tiempoEntrega: new Date('2025-06-01T00:00:00'),
          condicionAlmacenamiento: 'Ambiente',
          reglasLegales: 'Reglas 1',
          reglasComerciales: 'Reglas 2',
          reglasTributarias: 'IVA 19%',
          categoria: Categoria.LIMPIEZA_HOGAR,
          fabricante_id: '12345',
          sku: 'TEST123',
          createdAt: new Date().toISOString()
        }
      ];

      const expectedResponse: BulkProductoResponse = {
        products: [{ sku: 'TEST123', createdAt: new Date().toISOString() }],
        total: 1,
        message: 'Productos creados exitosamente'
      };

      service.crearProductosMasivo(mockProducts as unknown as Producto[]).subscribe(response => {
        expect(response).toEqual(expectedResponse);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}:${environment.endpointInventario}/productos/bulk`);
      expect(req.request.method).toBe('POST');
      req.flush(expectedResponse);
    });
  });
});
