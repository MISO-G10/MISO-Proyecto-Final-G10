import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog } from '@angular/material/dialog';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import {Seller} from './models/seller';
import { FormsModule } from '@angular/forms';

@Component({
    selector: 'app-sellers',
    imports: [
        CommonModule,
        MatTableModule,
        MatInputModule,
        MatIconModule,
        MatButtonModule,
        MatTooltipModule,
        FormsModule
      ],
    templateUrl: './sellers.component.html',
    styleUrl: './sellers.component.scss'
})
export class SellersComponent {
    private dialog = inject(MatDialog);

    displayedColumns: string[] = ['name', 'email', 'phone', 'joinDate', 'actions'];
    sellers = signal<Seller[]>([
        { id: '1', name: 'María González', email: 'maria@example.com', phone: '0991234567', joinDate: new Date('2023-01-15') },
        { id: '2', name: 'Carlos Rodríguez', email: 'carlos@example.com', phone: '0987654321', joinDate: new Date('2023-03-22') },
        { id: '3', name: 'Ana Martínez', email: 'ana@example.com', phone: '0976543210', joinDate: new Date('2023-05-10') },
    ]);

    searchTerm = signal('');
    filteredSellers = signal<Seller[]>([]);

    constructor() {
        this.filterSellers();
    }

    filterSellers() {
        const term = this.searchTerm().toLowerCase();
        if (!term) {
          this.filteredSellers.set(this.sellers());
          return;
        }
        
        this.filteredSellers.set(
          this.sellers().filter(seller => 
            seller.name.toLowerCase().includes(term) || 
            seller.email.toLowerCase().includes(term) ||
            seller.phone.includes(term)
        ))
      }

     
  

  deleteSeller(id: string): void {
    this.sellers.update(list => list.filter(seller => seller.id !== id));
    this.filterSellers();
  }

}
