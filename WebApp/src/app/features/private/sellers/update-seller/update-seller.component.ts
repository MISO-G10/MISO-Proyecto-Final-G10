import { CommonModule,Location } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from '../../../auth/login/models/user';
import { VisitasService } from '../../../../core/services/visitas.service';

@Component({
  selector: 'app-update-seller',
  imports: [CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatListModule],
  templateUrl: './update-seller.component.html',
  styleUrl: './update-seller.component.scss'
})
export class UpdateSellerComponent {
  userData: User | undefined;
  sellerForm: FormGroup;
  assignedTenderos: any[] = [];
  private readonly fb = inject(FormBuilder);
  private readonly sellerService=inject(VisitasService)
  constructor(private readonly location: Location) {
    this.sellerForm = this.fb.group({
      nombre: [{ value: '', disabled: false }, [Validators.required]],
      apellido: [{ value: '', disabled: false }, [Validators.required]],
      username: [{ value: '', disabled: true }], // Email (no editable)
      rol: [{ value: '', disabled: true }],     // No editable
      telefono: [{ value: '', disabled: false }],
      direccion: [{ value: '', disabled: false }]
    });
    
  }
  
   ngOnInit(): void {
    const state = this.location.getState() as { user?: User };
    if (state?.user) {
      this.userData = state.user;      
      this.sellerForm.patchValue({
        nombre: this.userData.nombre,
        apellido: this.userData.apellido,
        username: this.userData.username,
        rol: this.userData.rol,
        telefono: this.userData.telefono || '',
        direccion: this.userData.direccion || ''
      });
      if (this.userData.id) {
        this.sellerService.getAsignaciones(this.userData.id).subscribe({
          next: (res) => {
            this.assignedTenderos = res;
            console.log('Asignaciones recibidas:', this.assignedTenderos);
          },
          error: (err) => {
            console.error('Error al cargar asignaciones:', err);
          }
        });
      } 
      
      
    } 
  }
  
  
  
  onUpdateSeller() {
    if (this.sellerForm.valid) {
      const updatedData = this.sellerForm.getRawValue(); // Incluye campos deshabilitados
      console.log('Datos enviados para actualización:', updatedData);
      // Lógica para actualizar en el backend
    }
  }
  onAssignTendero() {
    // Lógica para abrir un modal o interfaz para asignar un tendero
    console.log('Asignar tendero');
  }
}
