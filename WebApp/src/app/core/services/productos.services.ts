import { HttpClient } from "@angular/common/http";
import { Injectable,inject } from "@angular/core";
import { environment } from '../../../environment/environment';
import { Producto } from "../../features/private/productos/models/producto";
interface ProductoResponse {
  createdAt: string;
    sku:string;
}
@Injectable({ providedIn: 'root' })
export class ProductoService{
    private readonly http = inject(HttpClient);
    private readonly apiProductosUrl = environment.inventarioUrl+'/createproduct';

  crearProducto(producto: Producto) {
    // Crear una copia del objeto y transformar el nombre del campo
    const { fabricanteId, ...rest } = producto;
    
    // Formatear las fechas como strings ISO
    const fechaVencimiento = producto.perecedero ? new Date(producto.fechaVencimiento).toISOString() : null;
    const tiempoEntrega = new Date(producto.tiempoEntrega).toISOString();
    
    const productoData = {
      ...rest,
      fechaVencimiento: fechaVencimiento,
      tiempoEntrega: tiempoEntrega,
      fabricante_id: fabricanteId
    };
    
    console.log('Datos enviados al backend:', productoData);
    return this.http.post<ProductoResponse>(`${this.apiProductosUrl}`, productoData);
  }
}