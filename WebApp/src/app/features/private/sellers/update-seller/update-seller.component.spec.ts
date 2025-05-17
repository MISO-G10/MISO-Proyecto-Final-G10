import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateSellerComponent } from './update-seller.component';

describe('UpdateSellerComponent', () => {
  let component: UpdateSellerComponent;
  let fixture: ComponentFixture<UpdateSellerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpdateSellerComponent]
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
