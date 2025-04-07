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
    private readonly apiProductosUrl = environment.apiProductosUrl+'/createproduct';

    crearProducto(producto: Producto) {
        return this.http.post<ProductoResponse>(`${this.apiProductosUrl}`, producto)
    }
}