import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../../environment/environment';
import { User } from '../../../auth/login/models/user';

@Injectable({
  providedIn: 'root'
})
export class CreateService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl + ':' + environment.endpointUsers;

  constructor() {
  }

  getVendedores() {
    return this.http.get<User[]>(this.apiUrl, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('auth')}`
      }
    });
  }
}
