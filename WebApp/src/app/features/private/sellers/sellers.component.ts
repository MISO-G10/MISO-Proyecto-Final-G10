import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import {  RouterModule } from '@angular/router';
import { SellerService } from '../../../core/services/seller.services';
import { User } from '../../auth/login/models/user';

@Component({
    selector: 'app-sellers',
    imports: [
        CommonModule,
        RouterModule,
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
    private readonly dialog = inject(MatDialog);
    private readonly sellerService= inject(SellerService);
    
    
    displayedColumns: string[] = ['nombre', 'username', 'telefono', 'direccion', 'actions'];
    sellers = signal<User[]>([]);
    searchTerm = signal('');
    filteredSellers = signal<User[]>([]);
    
    ngOnInit() {
      this.loadSellers();
    }

    loadSellers() {
      this.sellerService.listSeller().subscribe((users: User[]) => {
        const sellerFilter = users.filter(user => user.rol === 'VENDEDOR').reverse();
        this.sellers.set(sellerFilter);
        this.filterSellers();
      });
    }

    filterSellers() {
      const term = this.searchTerm().toLowerCase();
  
      if (!term) {
        this.filteredSellers.set(this.sellers());
        return;
      }
  
      this.filteredSellers.set(
        this.sellers().filter(user =>
          user.nombre.toLowerCase().includes(term) ||
          user.username.toLowerCase().includes(term) ||
          (user.telefono?.toLowerCase().includes(term) ?? false)
        )
      );
    }
  

}
