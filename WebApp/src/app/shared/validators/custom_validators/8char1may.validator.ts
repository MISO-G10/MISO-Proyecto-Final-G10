import { ValidatorFn, AbstractControl } from '@angular/forms';

export const passwordPatternValidator: ValidatorFn = (control: AbstractControl) => {
  if (!control.value) {
    return null;
  }
  
  const hasMinLength = control.value.length >= 8;
  const hasUpperCase = /[A-Z]/.test(control.value);
  
  if (!hasMinLength || !hasUpperCase) {
    return { 
      passwordRequirements: {
        minLength: !hasMinLength,
        upperCase: !hasUpperCase
      } 
    };
  }
  
  return null;
};