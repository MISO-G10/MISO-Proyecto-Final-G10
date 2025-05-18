import { HttpClient } from "@angular/common/http";
import { Injectable,inject } from "@angular/core";
import { environment } from '../../../environment/environment';
import { User } from "../../features/auth/login/models/user";

export interface Asignacion {
  id: string;
  idVendedor: string;
  idTendero: string;
  estado: string;
  fechaCreacion: string;
  fechaActualizacion: string;
}
@Injectable({ providedIn: 'root' })
export class VisitasService{
    private readonly http = inject(HttpClient);
    private readonly apiUrl = environment.apiUrl+':'+environment.endpointVisitas;

    getAsignaciones(id : string) {
    return this.http.get<Asignacion[]>(`${this.apiUrl}/asignaciones/tenderos/${id}`);
    }

    createAsignacion(data: any) {
        return this.http.post(`${this.apiUrl}/asignaciones`, data);
    }

    updateAsignacion(id: string, data: any) {
        return this.http.put(`${this.apiUrl}/asignaciones/${id}`, data);
    }

    // --- TENDEROS ASIGNADOS AL VENDEDOR ---

    getMisTenderos() {
        return this.http.get(`${this.apiUrl}/asignaciones/mis-tenderos`);
    }
    
}