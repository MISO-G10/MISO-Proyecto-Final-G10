import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import  {getErrorMessages} from '../../../../shared/validators/error-messages';
import {validaciones} from '../../../../shared/validators/error_validators/seller-validator'
import { passwordMatchValidator } from '../../../../shared/validators/custom_validators/password-match.validator';
import {  MatIconModule } from '@angular/material/icon';
import {MatCardModule} from '@angular/material/card';
import { passwordPatternValidator } from '../../../../shared/validators/custom_validators/8char1may.validator';
import { SellerService } from '../../../../core/services/seller.services';
import  { User } from '../../../auth/login/models/user';
import  { UserRole } from '../../../auth/login/models/roles';
import { SnackbarService } from '../../../../shared/ui/snackbar.service';

@Component({
  selector: 'app-create-seller',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule
    
  ],
  templateUrl: './create-seller.component.html',
  styleUrl: './create-seller.component.scss'
})
export class CreateSellerComponent {
  private readonly fb = inject(FormBuilder);
  private readonly sellerService=inject(SellerService)
  private readonly snackbarService = inject(SnackbarService);
  getErrorMessages = getErrorMessages;
  validaciones=validaciones;
  hide1=true;
  hide2=true;
  sellerForm=this.fb.group({
    name: ['',[Validators.required,Validators.maxLength(50)]],
    lastName: ['',[Validators.required,Validators.maxLength(50)]],
    email: ['',[Validators.required,Validators.pattern(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/),Validators.maxLength(50)]],        
    password: ['',[Validators.required,passwordPatternValidator]],
    confirmPassword: ['',[Validators.required]]
  },
  { validators: passwordMatchValidator })

  onCreateSeller(){

    const seller:User = {
      username: this.sellerForm.value.email!,  
      nombre: this.sellerForm.value.name!,
      apellido: this.sellerForm.value.lastName!,
      password: this.sellerForm.value.password!,
      rol: 'VENDEDOR' as UserRole  
    };
     // Definimos el rol como 'seller'
    this.sellerService.createSeller(seller).subscribe({
      next: (response) => {   
        if(response){          
          this.snackbarService.success('Vendedor creado exitosamente', {
            duration: 3000,
            position: { horizontal: 'end', vertical: 'top' }
          });
          this.onCancelCreate()
        } 
        
      },
      error: (error) => {
        this.snackbarService.error('Error al crear vendedor', {
          duration: 5000
        });
      }
    })
  }
  onCancelCreate(){
    this.sellerForm.reset({}, { emitEvent: false });
  
    // Resetear estados y errores
    Object.keys(this.sellerForm.controls).forEach(controlName => {
      const control = this.sellerForm.get(controlName);
      control?.setErrors(null);
      control?.markAsPristine();
      control?.markAsUntouched();
    });
  }

}

