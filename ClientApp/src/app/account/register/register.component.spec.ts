import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RegisterComponent } from './register.component';

import { ActivatedRoute } from '@angular/router';
import { User } from '@app/_models/user';
import { AccountService } from '@app/_services/account.service';
import { Observable, of, from, throwError } from 'rxjs';
import { HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { HttpRequestStatus } from '@app/_models/http-request-status';

const successfulRegistration = of({});

const registrationError = new HttpErrorResponse({
  error: {
    "type": "https://tools.ietf.org/html/rfc9110#section-15.5.1",
    "title": "One or more validation errors occurred.",
    "status": 400,
    "errors": {
        "DuplicateUserName": [
            "Username 'test1@test.com' is already taken."
        ]
    }
  },
  headers: new HttpHeaders(),
  status: 400,
  statusText: "One or more validation errors occurred.",
  url: "https://apiUrl.com/api/service"
});

class MockAccountService {
  register(user: User): Observable<object> {
    return (user.email === "test2@test.com") ? successfulRegistration : throwError(() => registrationError);
  }
}

const mockActivatedRoute = {};

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisterComponent],
      providers: [
        { provide: AccountService, useClass: MockAccountService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // test validation
  it('should validate correctly', () => {
    // arrange
    component.form.setValue({
      email: "rich@",           // invalid email
      password: "Abc123",       // needs a special char
      confirmPassword: "else",  // doesn't match password
      firstName: "",            // required
      lastName: ""              // required
    });

    // act
    component.onSubmit();

    // assert
    expect(component.form.invalid).toBe(true);
  });

  // test successful registration
  it('should register user', () => {
    // arrange
    component.form.setValue({
      email: "test2@test.com", // this should register successfully
      password: "Abc123!",
      confirmPassword: "Abc123!",
      firstName: "Bill",
      lastName: "Smith"
    });

    // act
    component.onSubmit();

    // assert
    expect(component.form.invalid).toBe(false);
    expect(component.status).toBe(HttpRequestStatus.Successful)
  });

  // test failed registration
  it('should not register user', () => {
    // arrange
    component.form.setValue({
      email: "test1@test.com", // this should fail to register
      password: "Abc123!",
      confirmPassword: "Abc123!",
      firstName: "Bill",
      lastName: "Smith"
    });

    // act
    component.onSubmit();

    // assert
    expect(component.form.invalid).toBe(false);
    expect(component.status).toBe(HttpRequestStatus.Failed)
  });

});
