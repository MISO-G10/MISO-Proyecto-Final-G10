import { Component, inject, LOCALE_ID, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ProductoConUbicaciones } from '../models/producto';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MAT_DATE_FORMATS, MAT_DATE_LOCALE, MatNativeDateModule } from '@angular/material/core';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSelectModule } from '@angular/material/select';
import { MY_DATE_FORMATS } from '../productos.component';
import { ProductoService } from '../../../../core/services/productos.services';

// Define the interfaces to match the actual API response

interface MovimientoInventario {
  id: string;
  tipo: 'INGRESO' | 'TRASLADO' | 'RETIRO';
  cantidad: number;
  fecha: Date;
  bodegaId: string;
  bodegaNombre: string;
  descripcion: string;
}

@Component({
  selector: 'app-producto-detalle',
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatCheckboxModule,
    MatSelectModule,
    FormsModule,
    DatePipe
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'es-ES' },
    { provide: MAT_DATE_FORMATS, useValue: MY_DATE_FORMATS },
    { provide: LOCALE_ID, useValue: 'es-ES' }
  ],
  templateUrl: './producto-detalle.component.html',
  styleUrls: ['./producto-detalle.component.scss']
})
export class ProductoDetalleComponent implements OnInit {
  private readonly router = inject(Router);
  private readonly productosService = inject(ProductoService);
  private readonly route = inject(ActivatedRoute);

  producto = signal<ProductoConUbicaciones | null>(null);

  // Inventory form fields
  cantidadProducto: string = '0';
  bodegaSeleccionada: string = 'N/A';
  direccionBodega: string = 'N/A';
  ciudadBodega: string = 'N/A';
  paisBodega: string = 'N/A';

  // Inventory history
  movimientosInventario: MovimientoInventario[] = [
    {
      id: '1',
      tipo: 'RETIRO',
      cantidad: 2,
      fecha: new Date('2025-01-03'),
      bodegaId: '2',
      bodegaNombre: 'Bodega 2',
      descripcion: 'Retiro de 2 productos'
    },
    {
      id: '2',
      tipo: 'TRASLADO',
      cantidad: 5,
      fecha: new Date('2025-01-02'),
      bodegaId: '1',
      bodegaNombre: 'Bodega 1',
      descripcion: 'Traslado de productos a bodega 1'
    },
    {
      id: '3',
      tipo: 'INGRESO',
      cantidad: 12,
      fecha: new Date('2025-01-01'),
      bodegaId: '2',
      bodegaNombre: 'Bodega 2',
      descripcion: 'Ingreso 12 productos a la Bodega 2'
    }
  ];

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('productoId');

    if (id) {
      this.onGetProducto(id);
    } else {
      this.router.navigate(['/private/productos']);
    }
  }

  onGetProducto(id: string) {
    this.productosService.getProducto(id).subscribe({
      next: (response) => {
        this.updateFormFields(response);
        this.loadInventoryMovements(id);
      },
      error: (error) => {
        this.router.navigate(['/private/productos']);
        console.error('Error fetching producto:', error);
      }
    });
  }

  updateFormFields(_producto: ProductoConUbicaciones) {
    if (_producto?.ubicaciones && _producto.ubicaciones.length > 0) {
      const ubicacion = _producto.ubicaciones[0];

      this.cantidadProducto = ubicacion.cantidad?.toString() || '0';
      this.bodegaSeleccionada = ubicacion.nombre_bodega || 'N/A';
      this.direccionBodega = ubicacion.direccion || 'N/A';

      // City and country aren't in the API, so use defaults
      this.ciudadBodega = ubicacion.ciudad || 'N/A';
      this.paisBodega = ubicacion.pais || 'N/A';
    }
  }

  loadInventoryMovements(productoId: string) {
    console.log(`Loading inventory movements for product ${productoId}`);
  }
}
