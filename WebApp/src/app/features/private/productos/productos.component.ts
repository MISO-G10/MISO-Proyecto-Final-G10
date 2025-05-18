import { Component, inject, LOCALE_ID, OnInit, signal } from '@angular/core';
import { CommonModule, registerLocaleData } from '@angular/common';
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
import { Router, RouterModule } from '@angular/router';
import { Producto } from './models/producto';
import localeEs from '@angular/common/locales/es';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable
} from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { ProductoService } from '../../../core/services/productos.services';

// Registrar el locale español
registerLocaleData(localeEs);

// Formato de fecha personalizado
export const MY_DATE_FORMATS = {
  parse: {
    dateInput: 'DDMMYYYY'
  },
  display: {
    dateInput: 'DDMMYYYY',
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY'
  }
};

@Component({
  selector: 'app-productos',
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
    MatTable,
    MatColumnDef,
    MatHeaderCell,
    MatCell,
    MatHeaderCellDef,
    MatCellDef,
    MatTooltip,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRowDef,
    MatRow
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'es-ES' },
    { provide: MAT_DATE_FORMATS, useValue: MY_DATE_FORMATS },
    { provide: LOCALE_ID, useValue: 'es-ES' }
  ],
  templateUrl: './productos.component.html',
  styleUrls: ['./productos.component.scss']
})
export class ProductosComponent implements OnInit {
  private readonly router = inject(Router);
  private readonly productosService = inject(ProductoService);

  filteredProductos = signal<Producto[]>([]);
  productos = signal<Producto[]>([]);

  ngOnInit() {
    this.loadProductos();
  }

  loadProductos() {
    this.productosService.getProductos().subscribe({
      next: (response) => {
        console.log({ response });
        this.productos.set(response);
        this.filterProductos();
      },
      error: (error) => {
        console.error('Error fetching productos:', error);
      }
    });
  }

  displayedColumns: string[] = ['name', 'actions'];
  readonly translations = {
    search_label: $localize`:@@private.product.list.search.label:Buscar productos`,
    search_placeholder: $localize`:@@private.product.list.search.placeholder:Nombre o codigo`,
    col_name: $localize`:@@private.product.list.table.col.name:Nombre`,
    col_actions: $localize`:@@private.product.list.table.col.actions:Acciones`,
    tooltip_details: $localize`:@@private.product.list.table.col.tooltip_details:Ver detalles`
  };

  searchTerm = '';


  filterProductos() {
    const term = this.searchTerm.toLowerCase();

    if (!term) {
      this.filteredProductos.set(this.productos());
      return;
    }

    this.filteredProductos.set(
      this.productos().filter(producto =>
        producto.nombre.toLowerCase().includes(term)
      )
    );
  }

  navigateToDetails(productoId: string) {
    this.router.navigate(['/private/productos/', productoId]);
  }
}
