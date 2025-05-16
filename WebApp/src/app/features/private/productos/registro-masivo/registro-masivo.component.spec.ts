import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RegistroMasivoComponent } from './registro-masivo.component';
import { ProductoService } from '../../../../core/services/productos.services';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, throwError } from 'rxjs';

describe('RegistroMasivoComponent', () => {
  let component: RegistroMasivoComponent;
  let fixture: ComponentFixture<RegistroMasivoComponent>;
  let productoService: jasmine.SpyObj<ProductoService>;
  let dialogRef: jasmine.SpyObj<MatDialogRef<RegistroMasivoComponent>>;
  let snackBar: jasmine.SpyObj<MatSnackBar>;

  beforeEach(async () => {
    const productoServiceSpy = jasmine.createSpyObj('ProductoService', ['crearProductosMasivo']);
    const dialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['close']);
    const snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);

    await TestBed.configureTestingModule({
      imports: [ RegistroMasivoComponent ],
      providers: [
        { provide: ProductoService, useValue: productoServiceSpy },
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: MatSnackBar, useValue: snackBarSpy },
        { provide: MAT_DIALOG_DATA, useValue: { fabricanteId: '12345' } }
      ]
    }).compileComponents();

    productoService = TestBed.inject(ProductoService) as jasmine.SpyObj<ProductoService>;
    dialogRef = TestBed.inject(MatDialogRef) as jasmine.SpyObj<MatDialogRef<RegistroMasivoComponent>>;
    snackBar = TestBed.inject(MatSnackBar) as jasmine.SpyObj<MatSnackBar>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegistroMasivoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should process CSV file correctly', (done) => {
    const csvContent = `nombre,valorUnidad,fechaVencimiento,tiempoEntrega,descripcion,condicionAlmacenamiento,reglasComerciales,reglasTributarias,perecedero,categoria,reglasLegales
Leche,3500,2025-12-31,2025-05-20,Leche entera,Refrigerado,No devoluciones,IVA 19%,true,ALIMENTOS Y BEBIDAS,INVIMA 123`;

    const file = new File([csvContent], 'test.csv', { type: 'text/csv' });
    const mockFileList = {
      0: file,
      length: 1,
      item: (index: number) => file
    } as FileList;

    const successResponse = {
      products: [{ sku: 'TEST123', createdAt: new Date().toISOString() }],
      total: 1,
      message: 'Productos creados exitosamente'
    };

    productoService.crearProductosMasivo.and.returnValue(of(successResponse));

    // Simular la lectura del archivo
    const reader = new FileReader();
    spyOn(window, 'FileReader').and.returnValue(reader);

    component.onFileSelected({ target: { files: mockFileList } } as any);
    component.uploadFile();

    setTimeout(() => {
      Object.defineProperty(reader, 'result', { value: csvContent });
      reader.onload?.({ target: reader } as ProgressEvent<FileReader>);

      setTimeout(() => {
        expect(productoService.crearProductosMasivo).toHaveBeenCalled();
        expect(dialogRef.close).toHaveBeenCalledWith(true);
        expect(snackBar.open).toHaveBeenCalled();
        done();
      }, 100);
    });
  });

  it('should handle validation errors', (done) => {
    const csvContent = `nombre,valorUnidad,fechaVencimiento,tiempoEntrega,descripcion,condicionAlmacenamiento,reglasComerciales,reglasTributarias,perecedero,categoria,reglasLegales
Leche,3500,2025-12-31,2025-05-20,Leche entera,Refrigerado,No devoluciones,IVA 19%,true,CATEGORIA_INVALIDA,INVIMA 123`;

    const file = new File([csvContent], 'test.csv', { type: 'text/csv' });
    const mockFileList = {
      0: file,
      length: 1,
      item: (index: number) => file
    } as FileList;

    const errorResponse = {
      error: 'Errores de validación',
      details: [
        { index: 0, error: { categoria: ['Categoría inválida'] } }
      ]
    };

    productoService.crearProductosMasivo.and.returnValue(throwError(() => errorResponse));

    // Simular la lectura del archivo
    const reader = new FileReader();
    spyOn(window, 'FileReader').and.returnValue(reader);

    component.onFileSelected({ target: { files: mockFileList } } as any);
    component.uploadFile();

    setTimeout(() => {
      Object.defineProperty(reader, 'result', { value: csvContent });
      reader.onload?.({ target: reader } as ProgressEvent<FileReader>);

      setTimeout(() => {
        expect(productoService.crearProductosMasivo).toHaveBeenCalled();
        expect(snackBar.open).toHaveBeenCalledWith(
          jasmine.stringMatching(/error/i),
          'Cerrar',
          jasmine.any(Object)
        );
        done();
      }, 100);
    });
  });

  it('should handle non-CSV files', () => {
    const file = new File(['not a csv'], 'test.txt', { type: 'text/plain' });
    const mockFileList = {
      0: file,
      length: 1,
      item: (index: number) => file
    } as FileList;

    component.onFileSelected({ target: { files: mockFileList } } as any);

    expect(productoService.crearProductosMasivo).not.toHaveBeenCalled();
    expect(snackBar.open).toHaveBeenCalledWith(
      jasmine.stringMatching(/csv/i),
      'Cerrar',
      jasmine.any(Object)
    );
  });

  it('should handle empty file selection', () => {
    const mockFileList = {
      length: 0,
      item: (index: number) => null
    } as FileList;

    component.onFileSelected({ target: { files: mockFileList } } as any);

    expect(productoService.crearProductosMasivo).not.toHaveBeenCalled();
  });
});
