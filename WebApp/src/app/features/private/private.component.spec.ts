import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PrivateComponent } from './private.component';
import { ActivatedRoute,provideRouter } from '@angular/router';

describe('PrivateComponent', () => {
  let component: PrivateComponent;
  let fixture: ComponentFixture<PrivateComponent>;

  // Mock para ActivatedRoute
  const mockActivatedRoute = {
    snapshot: {
      paramMap: new Map(),
      queryParamMap: new Map(),
      data: {}
    }
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        PrivateComponent, // Tu componente standalone
        
      ],
      providers: [
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(PrivateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});