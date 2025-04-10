import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { SalesPlan } from './models/sales';
import { SnackbarService } from '../../../shared/ui/snackbar.service';
import { environment } from '../../../../environment/environment';

@Injectable({
  providedIn: 'root'
})
export class SalesService {
  private readonly http = inject(HttpClient);
  private readonly snackbarService = inject(SnackbarService);
  private readonly apiUrl = environment.salesUrl;

  constructor() {
  }

  createSale(sale: {
    nombre: string;
    descripcion: string;
    valor_objetivo: number;
    fecha_inicio: string;
    fecha_fin: string;
    seller_ids: string[];
  }) {
    this.http.post<SalesPlan>(`${this.apiUrl}/planes`, sale, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('auth')}`
      }
    }).subscribe({
      next: (response) => {
        this.snackbarService.success('Plan de ventas creado', {
          duration: 5000,
          position: { horizontal: 'center', vertical: 'bottom' }
        });
      },
      error: (error) => {
        if (error.error) {
          const err = error.error;

          for (const key in err) {
            const errCtx = err[key];

            this.snackbarService.error(errCtx.ctx.error, {
              duration: 5000,
              position: { horizontal: 'center', vertical: 'bottom' }
            });
          }
        }
      }
    });
  }
}
