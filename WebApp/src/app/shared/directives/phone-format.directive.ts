import { Directive, ElementRef, HostListener } from '@angular/core';
import { NgControl } from '@angular/forms';

@Directive({
  selector: '[appPhoneFormat]',
  standalone: true
})
export class PhoneFormatDirective {

  constructor(
    private el: ElementRef,
    private control: NgControl
  ) {}

  @HostListener('input', ['$event'])
  onInputChange(event: InputEvent) {
    const input = event.target as HTMLInputElement;
    let value = input.value.replace(/\D/g, ''); // Eliminar todos los caracteres que no sean dígitos

    // Separar los dígitos para el indicativo y el número principal
    let countryCode = '';
    let mainNumber = '';
    
    if (value.length > 0) {
      // Los primeros 2 dígitos son para el indicativo del país
      countryCode = value.substring(0, Math.min(2, value.length));
      
      // El resto de dígitos (hasta 10) son para el número principal
      if (value.length > countryCode.length) {
        mainNumber = value.substring(countryCode.length, Math.min(countryCode.length + 10, value.length));
      }
    }

    // Formatear el número según el patrón +(xx) xxx xxx-xxxx
    let formattedValue = '';
    
    if (countryCode) {
      formattedValue = '+(' + countryCode + ')';
      
      if (mainNumber) {
        // Formatear el número principal
        if (mainNumber.length > 0) {
          formattedValue += ' ' + mainNumber.substring(0, Math.min(3, mainNumber.length));
        }
        
        if (mainNumber.length > 3) {
          formattedValue += ' ' + mainNumber.substring(3, Math.min(6, mainNumber.length));
        }
        
        if (mainNumber.length > 6) {
          formattedValue += '-' + mainNumber.substring(6, 10);
        }
      }
      
      // Actualizar el valor en el formulario
      this.control.control?.setValue(formattedValue, { emitEvent: false });
      
      // Actualizar el valor mostrado
      input.value = formattedValue;
    } else {
      // Si no hay dígitos, limpiar el campo
      this.control.control?.setValue('', { emitEvent: false });
      input.value = '';
    }
  }

  @HostListener('blur')
  onBlur() {
    // Asegurarse de que el formato sea correcto cuando el campo pierde el foco
    const value = this.control.value;
    if (value && typeof value === 'string') {
      const digitsOnly = value.replace(/\D/g, '');
      if (digitsOnly.length > 0 && digitsOnly.length < 12) {
        // Si el número está incompleto, mantener el formato pero mostrar error
        // La validación se manejará en el componente
      }
    }
  }
}
