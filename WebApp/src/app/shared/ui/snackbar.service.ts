import { Injectable } from '@angular/core';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';

export type SnackbarType = 'success' | 'error' | 'warning' | 'info';

interface SnackbarOptions {
  message: string;
  action?: string;
  duration?: number;
  position?: {
    horizontal: 'start' | 'center' | 'end';
    vertical: 'top' | 'bottom';
  };
}

@Injectable({
    providedIn: 'root'
})
export class SnackbarService {
  private readonly defaultDuration = 5000;
  private readonly defaultAction = 'Cerrar';

  private readonly config: Record<SnackbarType, MatSnackBarConfig> = {
    success: {
      panelClass: ['success-snackbar'],
      horizontalPosition: 'end',
      verticalPosition: 'top'
    },
    error: {
      panelClass: ['error-snackbar'],
      horizontalPosition: 'end',
      verticalPosition: 'top'
    },
    warning: {
      panelClass: ['warning-snackbar'],
      horizontalPosition: 'end',
      verticalPosition: 'top'
    },
    info: {
      panelClass: ['info-snackbar'],
      horizontalPosition: 'end',
      verticalPosition: 'top'
    }
  };

  constructor(private readonly snackBar: MatSnackBar) {}

  show(message: string, type: SnackbarType, options: Partial<SnackbarOptions> = {}) {
    const { action, duration, position } = {
      action: this.defaultAction,
      duration: this.defaultDuration,
      ...options
    };

    const config = {
      ...this.config[type],
      duration,
      ...(position && {
        horizontalPosition: position.horizontal,
        verticalPosition: position.vertical
      })
    };

    this.snackBar.open(message, action, config);
  }

  success(message: string, options?: Partial<SnackbarOptions>) {
    this.show(message, 'success', options);
  }

  error(message: string, options?: Partial<SnackbarOptions>) {
    this.show(message, 'error', options);
  }

  warning(message: string, options?: Partial<SnackbarOptions>) {
    this.show(message, 'warning', options);
  }

  info(message: string, options?: Partial<SnackbarOptions>) {
    this.show(message, 'info', options);
  }
}