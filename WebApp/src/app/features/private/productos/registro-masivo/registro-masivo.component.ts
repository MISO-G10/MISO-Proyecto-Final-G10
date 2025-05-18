import { Component, Inject, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Producto, Categoria } from '../models/producto';
import { ProductoService } from '../../../../core/services/productos.services';

@Component({
  selector: 'app-registro-masivo',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule
  ],
  templateUrl: './registro-masivo.component.html',
  styleUrls: ['./registro-masivo.component.scss']
})
export class RegistroMasivoComponent implements OnInit {
  fabricanteId: string;
  selectedFile: File | null = null;
  isUploading: boolean = false;
  uploadProgress: number = 0;

  // Campos requeridos para validación del csv
  requiredFields = [
    'nombre',
    'valorunidad',
    'fechavencimiento',
    'tiempoentrega',
    'descripcion',
    'condicionalmacenamiento',
    'reglascomerciales',
    'reglastributarias',
    'perecedero',
    'categoria',
    'reglaslegales'
  ];

  constructor(
    public dialogRef: MatDialogRef<RegistroMasivoComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { fabricanteId: string },
    private snackBar: MatSnackBar,
    private productoService: ProductoService
  ) {
    this.fabricanteId = data.fabricanteId;
  }

  ngOnInit(): void {
    console.log('Fabricante ID para carga masiva:', this.fabricanteId);
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];

      // Se valida que sea un archivo formato csv
      if (file.type !== 'text/csv' && !file.name.toLowerCase().endsWith('.csv')) {
        this.showError('El archivo debe tener formato CSV');
        this.selectedFile = null;
        return;
      }

      this.selectedFile = file;
    }
  }

  async uploadFile(): Promise<void> {
    if (!this.selectedFile) {
      this.showError('Debe seleccionar un archivo con formato CSV');
      throw new Error('No hay archivo seleccionado');
    }

    this.isUploading = true;
    this.uploadProgress = 0;

    return new Promise<void>((resolve, reject) => {
      const interval = setInterval(() => {
        this.uploadProgress += 10;
        if (this.uploadProgress >= 100) {
          clearInterval(interval);
          this.processCSVFile()
            .then(() => resolve())
            .catch(error => {
              console.error('Error al procesar el archivo:', error);
              reject(error);
            });
        }
      }, 200);
    });
  }

  async processCSVFile(): Promise<void> {
    if (!this.selectedFile) {
      throw new Error('No hay archivo seleccionado');
    }

    return new Promise((resolve, reject) => {
      const reader = new FileReader();

      reader.onload = (e) => {
        try {
          const content = e.target?.result as string;
          const productos = this.parseCSV(content);

          if (productos.length === 0) {
            this.showError('El archivo CSV no contiene datos válidos');
            this.isUploading = false;
            reject(new Error('El archivo CSV no contiene datos válidos'));
            return;
          }

          console.log('Productos a registrar:', productos);

          // Servicio registro masivo
          this.productoService.crearProductosMasivo(productos).subscribe({
            next: (response) => {
              console.log('Respuesta del servidor:', response);

              if (response.error) {

                if (response.details) {
                  const errorMessages = response.details
                    .map(detail => `Línea ${detail.index + 2}: ${JSON.stringify(detail.error)}`)
                    .join('\n');
                  this.showError(`Errores de validación:\n${errorMessages}`);
                } else {
                  this.showError(response.error);
                }
                this.isUploading = false;
                reject(new Error('Error de validación'));
                return;
              }

              // Mensaje de éxito si se crearon los productos en el back
              if (response.products && response.products.length > 0) {
                this.showSuccess('Productos registrados exitosamente');
                this.dialogRef.close(true);
                this.isUploading = false;
                resolve();
                this.showSuccess(`Se han creado ${response.products.length} productos exitosamente`);
                this.dialogRef.close(true);
              } else {
                this.showError('No se recibió confirmación de productos creados');
              }

              this.isUploading = false;
            },
            error: (error) => {
              console.error('Error al registrar productos:', error);
              this.showError('Error al registrar productos: ' +
                (error.error?.error || error.error?.message || error.message || 'Error desconocido'));
              this.isUploading = false;
              reject(error);
            }
          });

        } catch (error) {
          console.error('Error al procesar el archivo CSV:', error);
          this.showError('Error al procesar el archivo CSV: ' + (error as Error).message);
          this.isUploading = false;
          reject(error);
        }
      };

      reader.onerror = () => {
        this.showError('Error al leer el archivo');
        this.isUploading = false;
        reject(new Error('Error al leer el archivo'));
      };

      reader.readAsText(this.selectedFile as File);
    });
  }

  parseCSV(content: string): Producto[] {
    const lines = content.split('\n');
    if (lines.length <= 1) {
      throw new Error('El archivo no contiene datos suficientes');
    }

    // Se obtienen los encabezados y valida que estén los campos requeridos
    const headers = lines[0].split(',').map(header => header.trim().toLowerCase());
    console.log('Headers encontrados:', headers);
    console.log('Headers requeridos:', this.requiredFields);
    for (const field of this.requiredFields) {
      if (!headers.includes(field)) {
        throw new Error(`El archivo CSV no contiene el campo requerido: ${field}`);
      }
    }

    const productos: Producto[] = [];

    // Procesar cada línea (excepto la de encabezados)
    for (let i = 1; i < lines.length; i++) {
      if (!lines[i].trim()) continue; // Saltar líneas vacías

      const values = this.parseCSVLine(lines[i]);
      if (values.length !== headers.length) {
        throw new Error(`La línea ${i + 1} no tiene el formato correcto`);
      }

      // Se crea un objeto con los valores
      const productData: any = {};
      headers.forEach((header, index) => {
        productData[header] = values[index];
      });

      // Se convierten los valores al formato del modelo de producto
      const producto = {
        nombre: productData.nombre,
        descripcion: productData.descripcion,
        perecedero: productData.perecedero.toLowerCase() === 'true' || productData.perecedero === '1',
        fechaVencimiento: new Date(productData.fechavencimiento),
        valorUnidad: parseFloat(productData.valorunidad),
        tiempoEntrega: new Date(productData.tiempoentrega),
        condicionAlmacenamiento: productData.condicionalmacenamiento,
        reglasLegales: productData.reglaslegales,
        reglasComerciales: productData.reglascomerciales,
        reglasTributarias: productData.reglastributarias,
        categoria: this.mapCategoria(productData.categoria),
        fabricante_id: this.fabricanteId
      } as Producto;

      productos.push(producto);
    }

    return productos;
  }

  // Funcion para manejar correctamente las comillas en CSV
  parseCSVLine(line: string): string[] {
    const result: string[] = [];
    let inQuotes = false;
    let currentValue = '';

    for (let i = 0; i < line.length; i++) {
      const char = line[i];

      if (char === '"') {
        inQuotes = !inQuotes;
      } else if (char === ',' && !inQuotes) {
        result.push(currentValue.trim());
        currentValue = '';
      } else {
        currentValue += char;
      }
    }

    // Añadir el último valor
    result.push(currentValue.trim());

    return result;
  }

  mapCategoria(categoriaStr: string): Categoria {
    const categoriaUpper = categoriaStr.toUpperCase();

    switch (categoriaUpper) {
      case 'ALIMENTOS Y BEBIDAS':
      case 'ALIMENTOS_BEBIDAS':
        return Categoria.ALIMENTOS_BEBIDAS;
      case 'CUIDADO PERSONAL':
      case 'CUIDADO_PERSONAL':
        return Categoria.CUIDADO_PERSONAL;
      case 'LIMPIEZA Y HOGAR':
      case 'LIMPIEZA_HOGAR':
        return Categoria.LIMPIEZA_HOGAR;
      case 'BEBÉS':
      case 'BEBES':
        return Categoria.BEBES;
      case 'MASCOTAS':
        return Categoria.MASCOTAS;
      default:
        throw new Error(`Categoría no válida: ${categoriaStr}`);
    }
  }

  closeDialog(): void {
    this.dialogRef.close(false);
  }

  private showSuccess(message: string): void {
    this.snackBar.open(message, 'Cerrar', {
      duration: 3000,
      panelClass: ['success-snackbar']
    });
  }

  private showError(message: string): void {
    this.snackBar.open(message, 'Cerrar', {
      duration: 5000,
      panelClass: ['error-snackbar']
    });
  }
}
