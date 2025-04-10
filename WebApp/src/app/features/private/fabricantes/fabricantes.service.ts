import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Fabricante } from './models/fabricante';
import { SnackbarService } from '../../../shared/ui/snackbar.service';
import { environment } from '../../../../environment/environment';

@Injectable({
  providedIn: 'root'
})
export class FabricantesService {
  private readonly http = inject(HttpClient);
  private readonly snackbarService = inject(SnackbarService);
  private readonly apiUrl = environment.fabricanteUrl;

  constructor() {
  }

  createFabricante(fabricante: {
    nombre: string;
    numeroTel: string;
    representante: string;
  }) {
    this.http.post<Fabricante>(`${this.apiUrl}/fabricantes`, fabricante, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('auth')}`
      }
    }).subscribe({
      next: (response) => {
        this.snackbarService.success('Fabricante creado exitosamente', {
          duration: 5000,
          position: { horizontal: 'center', vertical: 'bottom' }
        });
      },
      error: (error) => {
        const err = error.error;

        for (const key in err) {
          const errCtx = err[key];

          this.snackbarService.error(errCtx.ctx.error, {
            duration: 5000,
            position: { horizontal: 'center', vertical: 'bottom' }
          });
        }
      }
    });
  }
}
