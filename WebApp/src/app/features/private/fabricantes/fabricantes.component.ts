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
        { id: '1', name: 'Industrias XYZ', phone: '0991234567', legalRepresentative: 'Juan Perez' },
        { id: '2', name: 'Fabricantes ABC', phone: '0987654321', legalRepresentative: 'Andres Garcia' },
        { id: '3', name: 'Manufacturas DEF', phone: '0976543210', legalRepresentative: 'Pedro Ramirez' },
    ]);

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
        this.router.navigate(['/private/add-product', fabricanteId]);
    }

    navigateToCreateFabricante() {
        this.router.navigate(['/private/fabricantes/create']);
    }
}
