import { HttpClient } from "@angular/common/http";
import { Injectable,inject } from "@angular/core";
import { environment } from '../../../environment/environment';
import { Bodega } from "../../features/private/bodegas/models/bodega";

@Injectable({ providedIn: 'root' })
export class BodegaService{
    private readonly http = inject(HttpClient);
    private readonly apiUrl = environment.apiUrl+':'+environment.endpointInventario;
    private readonly listBodegasUrl = this.apiUrl + '/bodegas';

    listBodegas(){
        return this.http.get<Bodega[]>(`${this.listBodegasUrl}`)
    }
}