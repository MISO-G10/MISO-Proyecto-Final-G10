import { Component } from '@angular/core';
import {MatIconModule} from '@angular/material/icon';
import {MatDividerModule} from '@angular/material/divider';
import {MatButtonModule} from '@angular/material/button';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-home',
    imports: [MatButtonModule, MatDividerModule, MatIconModule, CommonModule],
    templateUrl: './home.component.html',
    styleUrl: './home.component.scss'
})
export class HomeComponent {
    readonly translations = {
        welcome_title: $localize`:@@home.welcome.title:Bienvenido a la aplicación de gestión de ventas CCP Gestión`,
        welcome_message: $localize`:@@home.welcome.message:Aquí podrás gestionar tus ventas, clientes y productos de manera eficiente y sencilla. Si tienes alguna duda o necesitas ayuda, no dudes en contactar con nuestro equipo de soporte.`,
        contact_support: $localize`:@@home.welcome.contact_support:Contactar con soporte`
    };
}
