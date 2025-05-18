import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
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
    private readonly router = inject(Router);
    
    readonly translations = {
      search_placeholder: $localize`:@@private.seller.search.placeholder:Nombre, email o teléfono`,
      search_tittle: $localize`:@@private.seller.search.tittle:Buscar vendedor`,
      new_seller: $localize`:@@private.seller.button.new_seller:Nuevo vendedor`,
      table_col1: $localize`:@@private.seller.table.col1:Nombre`,
      table_col2: $localize`:@@private.seller.table.col2:Usuario`,  
      table_col3: $localize`:@@private.seller.table.col3:Teléfono`,
      table_col4: $localize`:@@private.seller.table.col4:Dirección`,
      table_col5: $localize`:@@private.seller.table.col5:Acciones`,
      tooltip_detail: $localize`:@@private.seller.tooltip.detail:Ver detalles`,
      tooltip_edit: $localize`:@@private.seller.tooltip.edit:Editar`,
      sales_report_button: $localize`:@@private.seller.sales.report.button:Informe de Ventas`,
      sales_report_tooltip: $localize`:@@private.seller.sales.report.tooltip:Ver informe de ventas`
    };
    
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

    navigateToSalesReport(seller: User) {
      // Verificamos que el vendedor tenga un ID
      if (!seller.id) {
        console.error('El vendedor no tiene ID');
        return;
      }
      
      // Navegamos a la ruta del informe de ventas pasando el vendedor en el estado
      this.router.navigateByUrl('/private/sellers/sales-report/' + seller.id, { 
        state: { sellerData: seller }
      });
    }
    navigateToEdit(user: User): void {
      console.log(user);
      this.router.navigate(['/private/sellers/edit'], { state: { user } });
}
  

}
