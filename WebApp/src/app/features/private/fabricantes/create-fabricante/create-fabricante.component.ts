import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators, AbstractControl } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { Router } from '@angular/router';
import { SnackbarService } from '../../../../shared/ui/snackbar.service';
import { getErrorMessages } from '../../../../shared/validators/error-messages';
import { PhoneFormatDirective } from '../../../../shared/directives/phone-format.directive';
import { FabricantesService } from '../fabricantes.service';

// Interfaces para tipar correctamente las validaciones
interface ValidationMessage {
  [key: string]: string;
}

interface ValidationMessages {
  name: ValidationMessage;
  legalRepresentative: ValidationMessage;
  phone: ValidationMessage;
}

@Component({
  selector: 'app-create-fabricante',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    PhoneFormatDirective
  ],
  templateUrl: './create-fabricante.component.html',
  styleUrls: ['./create-fabricante.component.scss']
})
export class CreateFabricanteComponent {
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly snackbarService = inject(SnackbarService);
  private readonly fabricantesService = inject(FabricantesService);
  getErrorMessages = getErrorMessages;

  // Validaciones para los mensajes de error
  validaciones: ValidationMessages = {
    name: {
      required: 'El nombre es requerido',
      maxlength: 'El nombre no puede exceder 50 caracteres'
    },
    legalRepresentative: {
      required: 'El representante legal es requerido',
      maxlength: 'El nombre no puede exceder 50 caracteres'
    },
    phone: {
      required: 'El número de teléfono es requerido',
      pattern: 'El formato debe ser +(xx) xxx xxx-xxxx'
    }
  };

  // Convertir las validaciones a formato compatible con getErrorMessages
  getValidationErrors(control: AbstractControl | null, fieldName: 'name' | 'legalRepresentative' | 'phone'): { type: string, message: string }[] {
    if (!control || !this.validaciones[fieldName]) return [];

    const fieldValidations = this.validaciones[fieldName];
    const validationArray: { type: string, message: string }[] = [];

    // Convertir objeto de validaciones a array de objetos {type, message}
    for (const errorType in fieldValidations) {
      validationArray.push({
        type: errorType,
        message: fieldValidations[errorType]
      });
    }

    return validationArray;
  }

  fabricanteForm = this.fb.group({
    name: ['', [Validators.required, Validators.maxLength(50)]],
    legalRepresentative: ['', [Validators.required, Validators.maxLength(50)]],
    phone: ['', [
      Validators.required,
      Validators.pattern(/^\+\(\d{2}\) \d{3} \d{3}-\d{4}$/), // Validar el formato +(xx) xxx xxx-xxxx con exactamente 2 dígitos para el indicativo
    ]]
  });

  // Función para limpiar campos del formulario
  clearField(fieldName: string): void {
    this.fabricanteForm.get(fieldName)?.setValue('');
  }

  onCreateFabricante() {
    if (this.fabricanteForm.invalid) {
      return;
    }

    // Extraer solo los dígitos del número de teléfono
    const phoneDigits = this.fabricanteForm.value.phone?.replace(/\D/g, '') || '';

    // Crear el objeto fabricante con el formato esperado por el servicio
    const fabricanteData = {
      nombre: this.fabricanteForm.value.name!,
      numeroTel: phoneDigits,
      representante: this.fabricanteForm.value.legalRepresentative!
    };

    // Llamar al servicio para crear el fabricante
    this.fabricantesService.createFabricante(fabricanteData);

    // Navegar de vuelta a la lista de fabricantes
    this.router.navigate(['/private/fabricantes']);
  }

  onCancelCreate() {
    this.router.navigate(['/private/fabricantes']);
  }
}
