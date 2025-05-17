import { HttpClient } from "@angular/common/http";
import { Injectable,inject } from "@angular/core";
import { environment } from '../../../environment/environment';
import { Ruta } from "../../features/private/rutas/models/ruta";
interface RutaResponse {
    createdAt: string;
    id:string;
}
@Injectable({ providedIn: 'root' })
export class RutaService{
    private readonly http = inject(HttpClient);
    private readonly apiUrl = environment.apiUrl+':'+environment.endpointInventario;
    private readonly listRutasUrl = this.apiUrl + '/rutas';

    listRutas(){
        return this.http.get<Ruta[]>(`${this.listRutasUrl}`)
    }
}