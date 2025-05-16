import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../../environment/environment';
import { Producto, Categoria, ProductoConUbicaciones, Bodega } from '../../features/private/productos/models/producto';

interface ProductoResponse {
  createdAt: string;
  sku: string;
}

@Injectable({ providedIn: 'root' })
export class ProductoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl + ':' + environment.endpointInventario;
  private readonly apiProductosUrl = environment.apiUrl + ':' + environment.endpointInventario + '/createproduct';

  crearProducto(producto: Producto) {
    // Crear una copia del objeto y transformar el nombre del campo
    const { fabricanteId, ...rest } = producto;

    // Formatear las fechas como strings ISO
    const fechaVencimiento = producto.perecedero ? new Date(producto.fechaVencimiento).toISOString() : null;
    const tiempoEntrega = new Date(producto.tiempoEntrega).toISOString();

    // Transformar el valor de la categoría al formato que espera el backend
    // Necesitamos enviar el nombre del enum, no el valor
    let categoriaTransformada = producto.categoria;

    // Mapeo de valores de categoría a las claves de enum esperadas por el backend
    const categoriasMap: Record<string, Categoria> = {
      'ALIMENTOS Y BEBIDAS': Categoria.ALIMENTOS_BEBIDAS,
      'CUIDADO PERSONAL': Categoria.CUIDADO_PERSONAL,
      'LIMPIEZA Y HOGAR': Categoria.LIMPIEZA_HOGAR,
      'BEBÉS': Categoria.BEBES,
      'MASCOTAS': Categoria.MASCOTAS
    };

    const productoData = {
      ...rest,
      fechaVencimiento: fechaVencimiento,
      tiempoEntrega: tiempoEntrega,
      fabricante_id: fabricanteId,
      // Enviar el nombre del enum para que haga match de la manera en que está en el backend
      categoria: Object.keys(Categoria).find(key => Categoria[key as keyof typeof Categoria] === producto.categoria) || producto.categoria
    };

    console.log('Datos enviados al backend:', productoData);
    return this.http.post<ProductoResponse>(`${this.apiProductosUrl}`, productoData);
  }

  getBodegas() {
    return this.http.get<Bodega[]>(`${this.apiUrl}/bodegas`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('auth')}`
      }
    });
  }

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
