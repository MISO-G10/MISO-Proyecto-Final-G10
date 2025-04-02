import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { User } from '../../features/auth/login/models/user';

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
          if (response.token) {
            localStorage.setItem('auth', response.token);
            this.getUser();
          }
        }
        
      });
  }
  getUser(){
    return this.http.get<User>(`${this.apiUrl}/me`).subscribe({
      next: (response) => {
        if (response) {
          localStorage.setItem('user', JSON.stringify(response));
          this.snackBar.open('Bienvenido', 'Cerrar', { duration: 3000 });
          this.router.navigate(['private/home']);
        }
      },
      error: (error) => {
        this.snackBar.open(error, 'Cerrar', { duration: 3000 });
      }
    })
  }
  //Hay que mejorar esta funcion, ya que yo podria sencillamente cambiar el token en el localstorage y acceder a la aplicacion sin tener que logearme, se debe llamar al servicio /me
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

  getCurrentUser(): User | null {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }
}