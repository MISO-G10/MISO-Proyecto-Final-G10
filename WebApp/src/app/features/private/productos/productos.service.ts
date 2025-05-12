import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environment/environment';
import { Producto, ProductoConUbicaciones } from './models/producto';

@Injectable({ providedIn: 'root' })
export class ProductosService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl + ':' + environment.endpointInventario;


  getProductos() {
    return this.http.get<Producto[]>(`${this.apiUrl}/productos`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('auth')}`
      }
    });
  }

  getProducto(id: string) {
    return this.http.get<ProductoConUbicaciones>(`${this.apiUrl}/productos/${id}`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('auth')}`
      }
    });
  }
}
