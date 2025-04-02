// services/loading.service.ts
import { Injectable, inject } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { MatProgressBar } from '@angular/material/progress-bar';

@Injectable({
  providedIn: 'root'
})
export class LoadingService {
  private readonly isLoading = new BehaviorSubject<boolean>(false);
  private readonly progressBar = inject(MatProgressBar, { optional: true });

  constructor() {
    this.isLoading.subscribe(loading => {
      if (this.progressBar) {
        this.progressBar.mode = loading ? 'indeterminate' : 'determinate';
      }
    });
  }

  show(): void {
    this.isLoading.next(true);
  }

  hide(): void {
    this.isLoading.next(false);
  }

  get loading$() {
    return this.isLoading.asObservable();
  }
}