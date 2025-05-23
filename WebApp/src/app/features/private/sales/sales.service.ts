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
  private readonly apiUrl = environment.apiUrl + ':' + environment.endpointVentas;


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
    return this.http.post<SalesPlan>(`${this.apiUrl}`, sale, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('auth')}`
      }
    })
  }
}
