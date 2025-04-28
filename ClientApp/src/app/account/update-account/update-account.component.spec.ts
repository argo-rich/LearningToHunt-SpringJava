import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateAccountComponent } from './update-account.component';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { User } from '@app/_models/user';
import { AccountService } from '@app/_services/account.service';
import { Router } from '@angular/router';

const successfulUpdate = of({});

const updateError = new HttpErrorResponse({
  error: "Password and Confirm Password do not match.", // Current Password is invalid.
  headers: new HttpHeaders(),
  status: 400,
  statusText: "Bad Request",
  url: "https://apiUrl.com/api/service"
});

class MockAccountService {
  private userSubject: BehaviorSubject<User | null>;
  public user: Observable<User | null>;

  constructor() {
      this.userSubject = new BehaviorSubject(JSON.parse(localStorage.getItem('user')!));
      this.user = this.userSubject.asObservable();
  }
    
  update(params: any): Observable<object> {
    return (true) ? successfulUpdate : throwError(() => updateError);
  }
}

describe('UpdateAccountComponent', () => {
  let component: UpdateAccountComponent;
  let fixture: ComponentFixture<UpdateAccountComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpdateAccountComponent],
      providers: [{ provide: AccountService, useClass: MockAccountService }]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpdateAccountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should invalidate bad email', () => {
    // arrange
    component.form.setValue({
      email: "test@", // invalid
      firstName: "Bill",
      lastName: "Smith",
      currentPassword: "",
      password: "",
      confirmPassword: ""
    });

    // act
    component.onSubmit();

    // assert
    expect(component.form.invalid).toBe(true);
  });

  it('should invalidate bad password', () => {
    // arrange
    component.form.setValue({
      email: "test@test.com",
      firstName: "Bill",
      lastName: "Smith",
      currentPassword: "Abc123!",
      password: "Abc123", // invalid
      confirmPassword: "Abc123" // invalid
    });

    // act
    component.onSubmit();

    // assert
    expect(component.form.invalid).toBe(true);
  });

  it('should validate valid attributes', () => {
    // arrange
    component.form.setValue({
      email: "test@test.com",
      firstName: "Bill",
      lastName: "Smith",
      currentPassword: "Abc123!",
      password: "Abc123+",
      confirmPassword: "Abc123+"
    });

    // act
    component.onSubmit();

    // assert
    expect(component.form.invalid).toBe(false);
  });

  it('should allow no passwords when editPassword set to false', () => {
    // arrange
    component.setEditPassword(false);
    component.form.setValue({
      email: "test@test.com",
      firstName: "Bill",
      lastName: "Smith",
      currentPassword: "",
      password: "",
      confirmPassword: ""
    });

    // act
    component.onSubmit();

    // assert
    expect(component.form.invalid).toBe(false);
  });

  it('should not allow empty passwords when editPassword set to true', () => {
    // arrange
    component.setEditPassword(true);
    component.form.setValue({
      email: "test@test.com",
      firstName: "Bill",
      lastName: "Smith",
      currentPassword: "",
      password: "",
      confirmPassword: ""
    });

    // act    
    component.onSubmit();

    // assert
    expect(component.form.invalid).toBe(true);
  });

  it('should invalidate when password invalid (when editPassword set to true)', () => {
    // arrange
    component.setEditPassword(true);
    component.form.setValue({
      email: "test@test.com",
      firstName: "Bill",
      lastName: "Smith",
      currentPassword: "Abc123!",
      password: "Abc123",
      confirmPassword: "Abc123"
    });

    // act    
    component.onSubmit();

    // assert
    expect(component.form.invalid).toBe(true);
  });

  it('should invalidate when password and confirm password do not match (when editPassword set to true)', () => {
    // arrange
    component.setEditPassword(true);
    component.form.setValue({
      email: "test@test.com",
      firstName: "Bill",
      lastName: "Smith",
      currentPassword: "Abc123!",
      password: "Abc123+",
      confirmPassword: "Abc123-"
    });

    // act    
    component.onSubmit();

    // assert
    expect(component.form.invalid).toBe(true);
  });
});
