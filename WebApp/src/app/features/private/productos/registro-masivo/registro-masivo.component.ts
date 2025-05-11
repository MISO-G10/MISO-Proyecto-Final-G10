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
  private snackBar = inject(MatSnackBar);
  private productoService = inject(ProductoService);
  
  fabricanteId: string;
  selectedFile: File | null = null;
  isUploading: boolean = false;
  uploadProgress: number = 0;
  
  // Campos requeridos para validación del csv
  requiredFields = [
    'nombre', 
    'valorUnidad', 
    'fechaVencimiento', 
    'tiempoEntrega', 
    'descripcion', 
    'condicionAlmacenamiento', 
    'reglasComerciales', 
    'reglasTributarias',
    'perecedero',
    'categoria',
    'reglasLegales'
  ];

  constructor(
    public dialogRef: MatDialogRef<RegistroMasivoComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { fabricanteId: string }
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
      if (file.type !== 'text/csv' && !file.name.endsWith('.csv')) {
        this.showError('El archivo debe ser de tipo CSV');
        this.selectedFile = null;
        return;
      }
      
      this.selectedFile = file;
    }
  }

  uploadFile(): void {
    if (!this.selectedFile) {
      this.showError('Debe seleccionar un archivo con formato CSV');
      return;
    }

    this.isUploading = true;
    this.uploadProgress = 0;

    // Simular progreso de carga
    const interval = setInterval(() => {
      this.uploadProgress += 10;
      if (this.uploadProgress >= 100) {
        clearInterval(interval);
        this.processCSVFile();
      }
    }, 200);
  }

  processCSVFile(): void {
    if (!this.selectedFile) return;

    const reader = new FileReader();
    
    reader.onload = (e) => {
      try {
        const content = e.target?.result as string;
        const productos = this.parseCSV(content);
        
        if (productos.length === 0) {
          this.showError('El archivo CSV no contiene datos válidos');
          this.isUploading = false;
          return;
        }

        console.log('Productos a registrar:', productos);
        
        
      } catch (error) {
        console.error('Error al procesar el archivo CSV:', error);
        this.showError('Error al procesar el archivo CSV: ' + (error as Error).message);
        this.isUploading = false;
      }
    };

    reader.onerror = () => {
      this.showError('Error al leer el archivo');
      this.isUploading = false;
    };

    reader.readAsText(this.selectedFile);
  }

  parseCSV(content: string): Producto[] {
    const lines = content.split('\n');
    if (lines.length <= 1) {
      throw new Error('El archivo no contiene datos suficientes');
    }

    // Se obtienen los encabezados y valida que estén los campos requeridos
    const headers = lines[0].split(',').map(header => header.trim().toLowerCase());
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
      const producto: Producto = {
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
        fabricanteId: this.fabricanteId
      };

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
