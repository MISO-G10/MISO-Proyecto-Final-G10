import { CommonModule } from '@angular/common';
import { Component,computed,inject,signal  } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import  {getErrorMessages} from '../../../core/validators/error-messages';
import validaciones from '../../../core/validators/login-validator'
@Component({
    selector: 'app-login',
    imports: [CommonModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule,
        MatButtonModule,
        MatProgressSpinnerModule],
    templateUrl: './login.component.html',
    styleUrl: './login.component.scss'
})
export class LoginComponent {
    //Inyeccion de dependecias (Sin constructor)
    private readonly router = inject(Router);
    private readonly fb = inject(FormBuilder);

    getErrorMessages = getErrorMessages;
    validaciones: { [key: string]: { type: string; message: string }[] } =validaciones;

    hide = true;
    isLoading = signal(false);
    errorMessage = signal<string | null>(null);

    loginForm = this.fb.group({
        email: ['', [Validators.required, Validators.pattern(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/)]],
        password: ['', [Validators.required]]
    });
      

      onLogin() {
        this.router.navigate(['/private']);
      }
    

}
