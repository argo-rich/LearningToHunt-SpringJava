import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmationComponent } from './confirmation.component';
import { ActivatedRoute } from '@angular/router';

let returnSuccess = true;

const mockActivatedRoute = {
  snapshot: { 
    paramMap: {
      get(id: string): string {        
        return returnSuccess ? 'success' : 'failure';
      }
    }
  }
};

describe('ConfirmationComponent', () => {
  let component: ConfirmationComponent;
  let fixture: ComponentFixture<ConfirmationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfirmationComponent],
      providers: [        
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should handle success', () => {
    // arrange
    returnSuccess = true; // is true by default

    // act
    component.ngOnInit();

    // assert
    expect(component.serverSideConfirmSuccess).toBe(true);
    expect(mockActivatedRoute.snapshot.paramMap.get('confirmationStatus')).toBe('success');
  });
  
  it('should handle failure', () => {
    // arrange
    returnSuccess = false; // is true by default

    // act
    component.ngOnInit();

    // assert
    expect(component.serverSideConfirmSuccess).toBe(false);
    expect(mockActivatedRoute.snapshot.paramMap.get('confirmationStatus')).toBe('failure');
  });
});
