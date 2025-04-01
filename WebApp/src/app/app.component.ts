import { Component, inject } from '@angular/core';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterOutlet } from '@angular/router';
import { LoadingService } from './core/services/loading.services';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { AsyncPipe, CommonModule } from '@angular/common';

@Component({
    selector: 'app-root',
    imports: [RouterOutlet,MatSnackBarModule,MatProgressBarModule, AsyncPipe,CommonModule],
    templateUrl: './app.component.html',
    styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'CCP';
  loadingService = inject(LoadingService);
}
