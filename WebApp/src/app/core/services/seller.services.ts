import { HttpClient } from "@angular/common/http";
import { Injectable,inject } from "@angular/core";
import { environment } from '../../../environment/environment';
import { User } from "../../features/auth/login/models/user";
interface SellerResponse {
    createdAt: string;
    id:string;
}
@Injectable({ providedIn: 'root' })
export class SellerService{
    private readonly http = inject(HttpClient);
    private readonly apiUrl = environment.apiUrl+':'+environment.endpointUsers;

    createSeller(seller: User) {
        return this.http.post<SellerResponse>(`${this.apiUrl}`, seller)
    }
    listSeller(){
        return this.http.get<User[]>(`${this.apiUrl}`)
    }
}