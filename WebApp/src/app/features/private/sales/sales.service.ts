import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { SalesPlan } from './models/sales';
import { SnackbarService } from '../../../shared/ui/snackbar.service';

@Injectable({
  providedIn: 'root'
})
export class SalesService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly snackbarService = inject(SnackbarService);
  private readonly apiUrl = 'http://localhost:3002';

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
        console.log({ response });
        this.snackbarService.success('Plan de ventas creado', {
          duration: 5000,
          position: { horizontal: 'center', vertical: 'bottom' }
        });

        this.router.navigate(['private/sales']);
      },
      error: (error) => {
        console.log({ error });
        this.snackbarService.error('Error al crear el plan de ventas', {
          duration: 5000,
          position: { horizontal: 'center', vertical: 'bottom' }
        });
      }
    });
  }
}
