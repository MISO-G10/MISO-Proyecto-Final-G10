import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';

interface AuthResponse {
  expireAt: string;
  id: string;
  token: string;
  message:string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);
  private apiUrl = 'http://localhost:3000/usuarios';

  login(username: string, password: string) {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth`, { username, password })
      .subscribe({
        next: (response) => {
        console.log(response)
          if (response.token) {
            localStorage.setItem('auth', JSON.stringify(response.token));
            this.snackBar.open('Bienvenido', 'Cerrar', { duration: 3000 });
            this.router.navigate(['private/home']);
          } else {
            this.snackBar.open(response.message || 'Credenciales incorrectas', 'Cerrar');
          }
        }
        // El error ya lo maneja el interceptor
      });
  }
}