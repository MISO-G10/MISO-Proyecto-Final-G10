import { HttpClient } from "@angular/common/http";
import { Injectable,inject } from "@angular/core";
import { environment } from '../../../environment/environment';
import { Pedido } from "../../features/private/pedidos/models/pedido";
interface InventarioResponse {
    createdAt: string;
    id:string;
}
@Injectable({ providedIn: 'root' })
export class PedidoService{
    private readonly http = inject(HttpClient);
    private readonly apiUrl = environment.apiUrl+':'+environment.endpointInventario;
    private readonly listPedidosUrl = this.apiUrl + '/pedidos';

    listPedidos(){
        return this.http.get<Pedido[]>(`${this.listPedidosUrl}`)
    }
}