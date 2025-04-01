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
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);
  private readonly apiUrl = 'http://localhost:3000/usuarios';

  login(username: string, password: string) {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth`, { username, password })
      .subscribe({
        next: (response) => {
        console.log(response)
          if (response.token) {
            localStorage.setItem('auth', JSON.stringify(response.token));
            this.snackBar.open('Bienvenido', 'Cerrar', { duration: 3000 });
            this.router.navigate(['private/home']);
          }
        }
        
      });
  }
  isAuthenticated(): boolean {
    return !!localStorage.getItem('auth');
  }
  logout(): void {
    localStorage.clear();
    this.router.navigate(['/login']);
    this.snackBar.open('Sesión cerrada', 'Cerrar', { duration: 3000 });
  }

  getToken(): string | null {
    return localStorage.getItem('auth');
  }
}