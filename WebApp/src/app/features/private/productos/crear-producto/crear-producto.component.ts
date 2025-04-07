import { Component, OnInit, inject, LOCALE_ID } from '@angular/core';
import { CommonModule, registerLocaleData } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule, MAT_DATE_FORMATS, MAT_DATE_LOCALE } from '@angular/material/core'; 
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSelectModule } from '@angular/material/select';
import { ActivatedRoute, Router } from '@angular/router';
import { Producto, Categoria } from '../models/producto';
import { getErrorMessages } from '../../../../shared/validators/error-messages';
import { validaciones } from '../../../../shared/validators/error_validators/producto-validator';
import localeEs from '@angular/common/locales/es';
import { ProductoService } from '../../../../core/services/productos.services';

// Registrar el locale español
registerLocaleData(localeEs);

// Formato de fecha personalizado
export const MY_DATE_FORMATS = {
  parse: {
    dateInput: 'DDMMYYYY',
  },
  display: {
    dateInput: 'DDMMYYYY',
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY',
  },
};

@Component({
  selector: 'app-crear-producto',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatCheckboxModule,
    MatSelectModule
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'es-ES' },
    { provide: MAT_DATE_FORMATS, useValue: MY_DATE_FORMATS },
    { provide: LOCALE_ID, useValue: 'es-ES' }
  ],
  templateUrl: './crear-producto.component.html',
  styleUrls: ['./crear-producto.component.scss']
})
export class CrearProductoComponent implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);
  private productoService = inject(ProductoService);
  getErrorMessages = getErrorMessages;
  validaciones=validaciones;
  productoForm!: FormGroup;
  fabricanteId!: string;
  
  // Exponer el enum Categoria para usar en la plantilla
  Categoria = Categoria;
  // Obtener los valores del enum para el select
  categorias = Object.values(Categoria);

  // Función para validar que la fecha sea posterior a hoy
  fechaPosteriorValidator() {
    return (control: any) => {
      if (!control.value) return null;
      
      const fechaSeleccionada = new Date(control.value);
      const hoy = new Date();
      
      // Resetear las horas para comparar solo las fechas
      hoy.setHours(0, 0, 0, 0);
      fechaSeleccionada.setHours(0, 0, 0, 0);
      
      return fechaSeleccionada > hoy ? null : { 'fechaInvalida': true };
    };
  }

  ngOnInit(): void {
    this.fabricanteId = this.route.snapshot.paramMap.get('fabricanteId') || '';
    console.log('Fabricante ID recibido:', this.fabricanteId);
    
    if (!this.fabricanteId) {
      this.showError('ID de fabricante no válido');
      this.router.navigate(['/private/fabricantes']);
      return;
    }

    this.initForm();
    this.setupPerecederoDependency();
  }

  private initForm(): void {
    this.productoForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(3)]],
      descripcion: ['', [Validators.required, Validators.minLength(5)]],
      valorUnidad: [0, [Validators.required, Validators.min(0.01)]],
      perecedero: [false, [Validators.required]],
      fechaVencimiento: [{value: new Date(), disabled: true}, [Validators.required, this.fechaPosteriorValidator()]],
      reglasLegales: ['', [Validators.required]],
      tiempoEntrega: [new Date(), [Validators.required]],
      condicionAlmacenamiento: ['', [Validators.required]],
      categoria: ['', [Validators.required]],
      reglasComerciales: ['', [Validators.required]],
      reglasTributarias: ['', [Validators.required]]
    });
  }

  private setupPerecederoDependency(): void {
    // Observar cambios en el campo perecedero
    this.productoForm.get('perecedero')?.valueChanges.subscribe(esPerecedero => {
      const fechaVencimientoControl = this.productoForm.get('fechaVencimiento');
      
      if (esPerecedero) {
        fechaVencimientoControl?.enable();
        fechaVencimientoControl?.updateValueAndValidity();
      } else {
        fechaVencimientoControl?.disable();
      }
    });
  }

  onSubmit(): void {
    if (this.productoForm.invalid) {
      this.productoForm.markAllAsTouched();
      return;
    }

    // Asegurarse de incluir los valores de los controles deshabilitados
    const formValues = this.productoForm.getRawValue();
    
    const producto: Producto = {
      ...formValues,
      fabricanteId: this.fabricanteId
    };

    console.log('Enviando producto con fabricanteId:', this.fabricanteId);
    console.log('Datos del producto a enviar:', producto);

    this.productoService.crearProducto(producto).subscribe({
      next: (response) => {
        console.log('Producto creado:', response);
        this.showSuccess('Producto registrado con éxito');
        this.router.navigate(['/private/fabricantes']);
      },
      error: (error) => {
        console.error('Error al crear producto:', error);
        this.showError('Error al crear el producto: ' + (error.error?.message || error.message || 'Error desconocido'));
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/private/fabricantes']);
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
