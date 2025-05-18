import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UpdateSellerComponent } from './update-seller.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

describe('UpdateSellerComponent', () => {
  let component: UpdateSellerComponent;
  let fixture: ComponentFixture<UpdateSellerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpdateSellerComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: of({ id: '123' })
          }
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpdateSellerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
