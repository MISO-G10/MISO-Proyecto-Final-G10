import { Component, inject, ViewChild } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatToolbarModule} from '@angular/material/toolbar';
import { MatSidenav, MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-private',
    imports: [RouterModule,
        CommonModule,
         MatToolbarModule,
          MatButtonModule, 
          MatIconModule,
          MatSidenavModule,
          MatListModule,
        ],
    templateUrl: './private.component.html',
    styleUrl: './private.component.scss'
})
export class PrivateComponent {
    @ViewChild('sidenav') sidenav!: MatSidenav;
    private readonly router = inject(Router);
    isClickExpanded = false;
    isHoverExpanded = false;
    expandedWidth = 200;
    collapsedWidth = 60;
  
    toggleByClick() {
      this.isClickExpanded = !this.isClickExpanded;
      this.isHoverExpanded = false; // Reset hover state when clicking
    }
  
    expandByHover() {
      if (!this.isClickExpanded) {
        this.isHoverExpanded = true;
      }
    }
  
    collapseByHover() {
      if (!this.isClickExpanded) {
        this.isHoverExpanded = false;
      }
    }
  
    getSidenavWidth(): number {
      if (this.isClickExpanded || this.isHoverExpanded) return this.expandedWidth;
      return this.collapsedWidth;
    }
    logOut(){
        this.router.navigate(['/login']);
    }
}
