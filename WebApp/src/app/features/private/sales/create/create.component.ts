import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { FormArray, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { SalesService } from '../sales.service';
import { getErrorMessages } from '../../../../shared/validators/error-messages';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { SalesPlanSeller } from '../models/sales';
import { map, Observable, startWith } from 'rxjs';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatChipsModule } from '@angular/material/chips';
import { User } from '../../../auth/login/models/user';
import { CreateService } from './create.service';
import { SnackbarService } from '../../../../shared/ui/snackbar.service';

@Component({
  selector: 'app-create',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatTableModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatTooltipModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatAutocompleteModule,
    MatChipsModule,
    FormsModule
  ],
  templateUrl: './create.component.html',
  styleUrl: './create.component.scss'
})

export class CreateComponent {
  createForm!: FormGroup;
  vendors: SalesPlanSeller[] = [
    { id: 1, seller_id: 1, nombre: 'Vendedor 1', apellido: 'Apellido 1' },
    { id: 2, seller_id: 2, nombre: 'Vendedor 2', apellido: 'Apellido 2' }
  ];

  vendedores = signal<User[]>([]);

  filteredVendors: Observable<User[]> | undefined;

  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  private readonly salesService = inject(SalesService);
  private readonly createService = inject(CreateService);
  private readonly snackbarService = inject(SnackbarService);

  // Translations
  readonly translations = {
    title: $localize`:@@private.sales.create.title:Crear Plan`,
    field_name: $localize`:@@private.sales.create.field.name:Nombre plan`,
    field_search_seller: $localize`:@@private.sales.create.field.search_seller:Buscar vendedor`,
    field_search_seller_placeholder: $localize`:@@private.sales.create.field.search_seller.placeholder:Buscar vendedor`,
    field_start_date: $localize`:@@private.sales.create.field.start_date:Fecha inicio`,
    field_end_date: $localize`:@@private.sales.create.field.end_date:Fecha final`,
    field_description: $localize`:@@private.sales.create.field.description:Descripcion`,
    field_target: $localize`:@@private.sales.create.field.target:Objetivo`,
    button_submit: $localize`:@@private.sales.create.button.submit:Registrar plan`,
    button_cancel: $localize`:@@private.sales.create.button.cancel:Cancelar`
  };

  getErrorMessages = getErrorMessages;

  // Validation messages
  validaciones = {
    'nombre': [
      {
        type: 'required',
        message: $localize`:@@private.sales.create.validation.name.required:Nombre`
      }
    ],
    'descripcion': [
      {
        type: 'required',
        message: $localize`:@@private.sales.create.validation.description.required:Descripción`
      }
    ],
    'valor_objetivo': [
      {
        type: 'required',
        message: $localize`:@@private.sales.create.validation.target.required:El valor objetivo es requerido`
      },
      {
        type: 'pattern',
        message: $localize`:@@private.sales.create.validation.target.pattern:El valor objetivo debe ser un número`
      },
      {
        type: 'min',
        message: $localize`:@@private.sales.create.validation.target.min:El valor objetivo debe ser mayor a 0`
      }
    ],
    'fecha_inicio': [
      {
        type: 'required',
        message: $localize`:@@private.sales.create.validation.start_date.required:La fecha de inicio es requerida`
      },
      {
        type: 'pattern',
        message: $localize`:@@private.sales.create.validation.start_date.pattern:La fecha de inicio debe ser una fecha válida`
      }
    ],
    'fecha_fin': [
      {
        type: 'required',
        message: $localize`:@@private.sales.create.validation.end_date.required:La fecha de fin es requerida`
      },
      {
        type: 'pattern',
        message: $localize`:@@private.sales.create.validation.end_date.pattern:La fecha de fin debe ser una fecha válida`
      }
    ],
    'seller_ids': [
      {
        type: 'required',
        message: $localize`:@@private.sales.create.validation.sellers.required:Los vendedores son requeridos`
      },
      {
        type: 'minlength',
        message: $localize`:@@private.sales.create.validation.sellers.minlength:Se requiere al menos un vendedor`
      }
    ]
  };

  ngOnInit(): void {
    this.initForm();

    this.createService.getVendedores().subscribe((vendedores) => {
      this.vendedores.set(vendedores.filter(user => user.rol === 'VENDEDOR'));
    });
  }

  initForm() {
    this.createForm = this.fb.nonNullable.group({
      nombre: ['', [Validators.required]],
      descripcion: ['', [Validators.required]],
      valor_objetivo: ['', [
        Validators.required,
        Validators.pattern(/^\d+(\.\d{1,2})?$/),
        Validators.min(1)
      ]],
      fecha_inicio: ['', [
        Validators.required
      ]],
      fecha_fin: ['', [
        Validators.required
      ]],
      seller_ids: this.fb.array([], [
        Validators.required,
        Validators.minLength(1)
      ]),
      vendorSearch: ['']
    });

    this.filteredVendors = this.createForm.get('vendorSearch')!.valueChanges.pipe(
      startWith(''),
      map(value => this._filterVendors(value || ''))
    );
  }


  private _filterVendors(value: string): User[] {
    const filterValue = value.toLowerCase();
    return this.vendedores().filter(vendor =>
      vendor.nombre.toLowerCase().includes(filterValue));
  }

  get sellerIds(): FormArray {
    return this.createForm.get('seller_ids') as FormArray;
  }

  addSeller(vendor: Pick<User, 'id'>): void {
    // Check if vendor is already selected
    const existingIndex = this.getSellerIdIndex(vendor.id!);
    if (existingIndex === -1) {
      this.sellerIds.push(this.fb.control(vendor.id));
    }
    // Clear the search input
    this.createForm.get('vendorSearch')?.setValue('');
  }

  removeSeller(index: number): void {
    this.sellerIds.removeAt(index);
  }

  getSellerIdIndex(id: string): number {
    return this.sellerIds.controls.findIndex(control => control.value === id);
  }

  isVendorSelected(id: string): boolean {
    return this.getSellerIdIndex(id) !== -1;
  }

  getVendorName(id: string) {
    const vendor = this.vendedores().find(v => v.id === id);

    return vendor ? `${vendor.nombre} ${vendor.apellido}` : id;
  }

  // Format date to YYYY-MM-DD string when date is changed
  onDateChange(event: any, controlName: string): void {
    if (event.value) {
      // Extract the date directly from the event value

      // Set the value in the form
      this.createForm.get(controlName)?.setValue(event.value);
    }
  }

  // Helper method to format date as YYYY-MM-DD
  formatDate(date: Date): string {
    // Create a new date with timezone offset applied to ensure the selected date is preserved

    // Format the adjusted date
    const year = date.getFullYear();
    const month = ('0' + (date.getMonth() + 1)).slice(-2);
    const day = ('0' + date.getDate()).slice(-2);

    return `${year}-${month}-${day}`;
  }

  onCreate() {
    if (this.createForm.valid) {
      const { nombre, descripcion, valor_objetivo, fecha_inicio, fecha_fin } = this.createForm.value;

      const formattedFechaInicio = this.formatDate(fecha_inicio);
      const formattedFechaFin = this.formatDate(fecha_fin);
      this.salesService.createSale({
        nombre: nombre!,
        descripcion: descripcion!,
        valor_objetivo: Number(valor_objetivo!),
        fecha_inicio: formattedFechaInicio!,
        fecha_fin: formattedFechaFin!,
        seller_ids: this.createForm.value.seller_ids as string[]
      }).subscribe({
        next: (response) => {
          this.router.navigate(['private/sales']);

          this.snackbarService.success('Plan de ventas creado', {
            duration: 5000,
            position: { horizontal: 'center', vertical: 'bottom' }
          });

        },
        error: (error) => {
          if (error.error) {
            const err = error.error;

            for (const key in err) {
              const errCtx = err[key];

              this.snackbarService.error(errCtx.ctx.error, {
                duration: 5000,
                position: { horizontal: 'center', vertical: 'bottom' }
              });
            }
          }
        }
      });

    } else {
      Object.keys(this.createForm.controls).forEach(key => {
        const control = this.createForm.get(key);
        control?.markAsTouched();
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/private/sales']);
  }
}
