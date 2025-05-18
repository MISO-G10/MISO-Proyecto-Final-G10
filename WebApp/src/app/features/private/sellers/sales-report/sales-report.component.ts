import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, registerLocaleData } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { User } from '../../../auth/login/models/user';
import localeEs from '@angular/common/locales/es-CO';
import { Pedido } from '../../pedidos/models/pedido';
import { PedidoService } from '../../../../core/services/pedidos.services'

interface SaleReport {
  id: string;
  createdAt: Date;
  valor: number;
}

// Registrar el locale para español
registerLocaleData(localeEs);

@Component({
  selector: 'app-sales-report',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './sales-report.component.html',
  styleUrls: ['./sales-report.component.scss']
})
export class SalesReportComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private location = inject(Location);
  private readonly pedidoService = inject(PedidoService);

  sellerId = '';
  seller: User | null = null;
  salesData = signal<SaleReport[]>([]);
  displayedColumns: string[] = ['date', 'amount'];
  totalSales = signal(0);
  loading = signal(false);
  error = signal<string | null>(null);

  readonly translations = {
    title: $localize`:@@private.seller.sales.report.title:Informe de Ventas para {0}`,
    date: $localize`:@@private.seller.sales.report.table.col.date:Fecha`,
    amount: $localize`:@@private.seller.sales.report.table.col.amount:Monto`,
    total: $localize`:@@private.seller.sales.report.total:Total de Ventas`,
    back: $localize`:@@private.seller.sales.report.back:Volver a Vendedores`,
    loading: $localize`:@@private.seller.sales.report.loading:Cargando datos de ventas...`,
    no_data: $localize`:@@private.seller.sales.report.no_data:No hay datos de ventas disponibles para este vendedor.`
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
    // Establecer estado de carga
    this.loading.set(true);
    this.error.set(null);
    
    // Obtenemos los datos reales de pedidos desde el servicio
    this.pedidoService.listPedidos().subscribe({
      next: (pedidos) => {
        //Filtramos los pedidos que corresponden al vendedor seleccionado
        const pedidosDelVendedor = pedidos.filter(pedido => 
          pedido.vendedor_id === this.sellerId
        );

       //const pedidosDelVendedor = pedidos;
        
        // Transformamos los pedidos al formato de SaleReport
        const salesData: SaleReport[] = pedidosDelVendedor.map(pedido => ({
          id: pedido.id,
          createdAt: new Date(pedido.createdAt),
          valor: pedido.valor
        }));
        
        // Actualizamos los datos de ventas
        this.salesData.set(salesData);
        this.calculateTotal();
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error al cargar los datos de ventas:', err);
        this.error.set('No se pudieron cargar los datos de ventas. Por favor, intenta de nuevo más tarde.');
        this.loading.set(false);
      }
    });
  }

  calculateTotal() {
    const total = this.salesData().reduce((sum, sale) => sum + sale.valor, 0);
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
