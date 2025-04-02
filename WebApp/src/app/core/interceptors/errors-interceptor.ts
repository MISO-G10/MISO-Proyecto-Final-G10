import { HttpRequest, HttpHandlerFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { catchError, throwError } from 'rxjs';

export function errorInterceptor(request: HttpRequest<unknown>, next: HttpHandlerFn) {
  const snackBar = inject(MatSnackBar);
  
  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {

      if (error.status !== 401) {
        let errorMessage = 'Error desconocido';
        
        if (error.error?.message) {
          errorMessage = error.error.message;
        } else if (error.message) {
          errorMessage = error.message;
        }

        snackBar.open(errorMessage, 'Cerrar', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
      }
      return throwError(() => error);

    })
  );
}