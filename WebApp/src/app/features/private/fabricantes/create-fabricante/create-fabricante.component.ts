import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { Router } from '@angular/router';
import { SnackbarService } from '../../../../shared/ui/snackbar.service';
import { getErrorMessages } from '../../../../shared/validators/error-messages';
import { Fabricante } from '../models/fabricante';

@Component({
  selector: 'app-create-fabricante',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule
  ],
  templateUrl: './create-fabricante.component.html',
  styleUrl: './create-fabricante.component.scss'
})
export class CreateFabricanteComponent {
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly snackbarService = inject(SnackbarService);
  getErrorMessages = getErrorMessages;

  // Validaciones para los mensajes de error
  validaciones = {
    name: {
      required: 'El nombre es requerido',
      maxlength: 'El nombre no puede exceder 50 caracteres'
    },
    legalRepresentative: {
      required: 'El representante legal es requerido',
      maxlength: 'El nombre no puede exceder 50 caracteres'
    },
    phone: {
      maxlength: 'El número de teléfono no puede exceder 15 caracteres'
    }
  };

  fabricanteForm = this.fb.group({
    name: ['', [Validators.required, Validators.maxLength(50)]],
    legalRepresentative: ['', [Validators.required, Validators.maxLength(50)]],
    phone: ['', Validators.maxLength(15)]
  });

  // Función para limpiar campos del formulario
  clearField(fieldName: string): void {
    this.fabricanteForm.get(fieldName)?.setValue('');
  }

  onCreateFabricante() {
    if (this.fabricanteForm.invalid) {
      return;
    }

    const fabricante: Fabricante = {
      id: '', // ID será generado por el backend
      name: this.fabricanteForm.value.name!,
      phone: this.fabricanteForm.value.phone || '',
      legalRepresentative: this.fabricanteForm.value.legalRepresentative!
    };

    // Aquí se implementaría la llamada al servicio para crear el fabricante
    // Por ahora simularemos una respuesta exitosa
    setTimeout(() => {
      this.snackbarService.success('Fabricante creado exitosamente', {
        duration: 3000,
        position: { horizontal: 'end', vertical: 'top' }
      });
      this.router.navigate(['/private/fabricantes']);
    }, 1000);
  }

  onCancelCreate() {
    this.router.navigate(['/private/fabricantes']);
  }
}
