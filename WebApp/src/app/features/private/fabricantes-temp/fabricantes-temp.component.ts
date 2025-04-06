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
    selector: 'app-fabricantes-temp',
    imports: [
        CommonModule,
        MatTableModule,
        MatInputModule,
        MatIconModule,
        MatButtonModule,
        MatTooltipModule,
        FormsModule
    ],
    templateUrl: './fabricantes-temp.component.html',
    styleUrl: './fabricantes-temp.component.scss'
})
export class FabricantesTempComponent {
    private dialog = inject(MatDialog);
    private router = inject(Router);

    displayedColumns: string[] = ['name', 'email', 'phone', 'address', 'productos', 'actions'];
    fabricantes = signal<Fabricante[]>([
        { id: '1', name: 'Industrias XYZ', email: 'contacto@xyz.com', phone: '0991234567', address: 'Calle Principal 123' },
        { id: '2', name: 'Fabricantes ABC', email: 'info@abc.com', phone: '0987654321', address: 'Avenida Central 456' },
        { id: '3', name: 'Manufacturas DEF', email: 'ventas@def.com', phone: '0976543210', address: 'Boulevard Norte 789' },
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
                fabricante.email.toLowerCase().includes(term) ||
                fabricante.phone.includes(term) ||
                fabricante.address.toLowerCase().includes(term)
            )
        );
    }

    navigateToAddProduct(fabricanteId: string) {
        this.router.navigate(['/private/crear-producto', fabricanteId]);
    }
}
