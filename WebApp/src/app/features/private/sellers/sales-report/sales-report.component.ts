import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, registerLocaleData } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { User } from '../../../auth/login/models/user';
import localeEs from '@angular/common/locales/es-CO';

interface SaleReport {
  id: string;
  date: Date;
  amount: number;
}

// Registrar el locale para español de Colombia
registerLocaleData(localeEs);

@Component({
  selector: 'app-sales-report',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule
  ],
  templateUrl: './sales-report.component.html',
  styleUrls: ['./sales-report.component.scss']
})
export class SalesReportComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private location = inject(Location);

  sellerId = '';
  seller: User | null = null;
  salesData = signal<SaleReport[]>([]);
  displayedColumns: string[] = ['date', 'amount'];
  totalSales = signal(0);

  readonly translations = {
    title: $localize`:@@private.seller.sales.report.title:Informe de Ventas para {0}`,
    date: $localize`:@@private.seller.sales.report.table.col.date:Fecha`,
    amount: $localize`:@@private.seller.sales.report.table.col.amount:Monto`,
    total: $localize`:@@private.seller.sales.report.total:Total de Ventas`,
    back: $localize`:@@private.seller.sales.report.back:Volver a Vendedores`
  };

  ngOnInit() {
    this.sellerId = this.route.snapshot.paramMap.get('id') || '';
    
    // Obtener datos del vendedor
    const navigation = this.router.getCurrentNavigation();
    const state = history.state;
    
    if (state && state.sellerData) {
      this.seller = state.sellerData;
    } else {
      // Si no hay datos en state, deberíamos cargar el vendedor por su ID
      // Por ahora podemos crear un vendedor ficticio para pruebas
      this.seller = {
        id: this.sellerId,
        nombre: 'Juan',
        apellido: 'Pérez',
        username: 'juanperez@mail.com',
        rol: 'VENDEDOR'
      };
    }

    this.loadSalesData();
  }

  loadSalesData() {
    // Por ahora usaremos datos de ejemplo
    const mockSales: SaleReport[] = [
      { id: 'sale1', date: new Date(2025, 3, 15), amount: 1200000 },
      { id: 'sale2', date: new Date(2025, 3, 20), amount: 850000 },
      { id: 'sale3', date: new Date(2025, 4, 2), amount: 1750000 },
      { id: 'sale4', date: new Date(2025, 4, 10), amount: 920000 },
      { id: 'sale5', date: new Date(2025, 4, 18), amount: 1350000 }
    ];
    
    this.salesData.set(mockSales);
    this.calculateTotal();
  }

  calculateTotal() {
    const total = this.salesData().reduce((sum, sale) => sum + sale.amount, 0);
    this.totalSales.set(total);
  }

  goBack() {
    this.location.back();
  }

  getFormattedTitle(): string {
    if (!this.seller) return this.translations.title.replace('{0}', '');
    
    const nombreCompleto = [this.seller.nombre, this.seller.apellido]
      .filter(Boolean)
      .join(' ');
      
    return this.translations.title.replace('{0}', nombreCompleto || 'Vendedor');
  }
}
