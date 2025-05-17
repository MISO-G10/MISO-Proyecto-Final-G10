import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { Fabricante } from './models/fabricante';
import { Router } from '@angular/router';

@Component({
  selector: 'app-fabricantes',
  imports: [
    CommonModule,
    MatTableModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatTooltipModule,
    FormsModule
  ],
  templateUrl: './fabricantes.component.html',
  styleUrl: './fabricantes.component.scss'
})
export class FabricantesComponent {
  private dialog = inject(MatDialog);
  private router = inject(Router);

  displayedColumns: string[] = ['name', 'phone', 'legalRepresentative', 'products', 'actions'];
  fabricantes = signal<Fabricante[]>([
    { id: 'fab-001', name: 'Industrias XYZ', phone: '0991234567', legalRepresentative: 'Juan Perez' },
    { id: 'fab-002', name: 'Fabricantes ABC', phone: '0987654321', legalRepresentative: 'Andres Garcia' },
    { id: 'fab-003', name: 'Manufacturas DEF', phone: '0976543210', legalRepresentative: 'Pedro Ramirez' }
  ]);

  // Traducciones
  readonly translations = {
    search_label: $localize`:@@private.manufacturer.list.search.label:Buscar fabricantes`,
    search_placeholder: $localize`:@@private.manufacturer.list.search.placeholder:Nombre, teléfono o representante legal`,
    button_new: $localize`:@@private.manufacturer.list.button.new:Nuevo fabricante`,
    col_name: $localize`:@@private.manufacturer.list.table.col.name:Nombre`,
    col_phone: $localize`:@@private.manufacturer.list.table.col.phone:Teléfono`,
    col_products: $localize`:@@private.manufacturer.list.table.col.products:Productos`,
    col_legalRepresentative: $localize`:@@private.manufacturer.list.table.col.legalRepresentative:Representante Legal`,
    col_actions: $localize`:@@private.manufacturer.list.table.col.actions:Acciones`,
    button_addProduct: $localize`:@@private.manufacturer.list.button.addProduct:Agregar producto`,
    tooltip_edit: $localize`:@@private.manufacturer.list.tooltip.edit:Editar`,
    tooltip_delete: $localize`:@@private.manufacturer.list.tooltip.delete:Eliminar`
  };

  searchTerm = '';
  filteredFabricantes = signal<Fabricante[]>([]);

  constructor() {
    this.filterFabricantes();
  }

  filterFabricantes() {
    const term = this.searchTerm.toLowerCase();
    if (!term) {
      this.filteredFabricantes.set(this.fabricantes());
      return;
    }

    this.filteredFabricantes.set(
      this.fabricantes().filter(fabricante =>
        fabricante.name.toLowerCase().includes(term) ||
        fabricante.phone.includes(term) ||
        fabricante.legalRepresentative.toLowerCase().includes(term)
      )
    );
  }

  navigateToAddProduct(fabricanteId: string) {
    console.log('Navegando a crear producto con fabricanteId:', fabricanteId);
    this.router.navigate(['/private/crear-producto', fabricanteId]);
  }

  navigateToCreateFabricante() {
    this.router.navigate(['/private/fabricantes/create']);
  }
}
