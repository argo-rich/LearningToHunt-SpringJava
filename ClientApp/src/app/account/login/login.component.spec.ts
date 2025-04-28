import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginComponent } from './login.component';
import { AccountService } from '@app/_services/account.service';
import { ActivatedRoute } from '@angular/router';
import { Observable, of, throwError } from 'rxjs';
import { HttpRequestStatus } from '@app/_models/http-request-status';
import { User } from '@app/_models/user';
import { HttpErrorResponse, HttpHeaders } from '@angular/common/http';

const loginError = new HttpErrorResponse({
  error: {
    "type": "https://tools.ietf.org/html/rfc9110#section-15.5.2",
    "title": "Unauthorized",
    "status": 401,
    "detail": "Failed"
  },
  headers: new HttpHeaders(),
  status: 401,
  statusText: "Unauthorized",
  url: "https://apiUrl.com/api/service"
});

class MockAccountService {
  login(email: string, password: string): Observable<User> {
    return (email !== "bad-email") ? of({}) : throwError(() => loginError);
  }

  logout(): Observable<object> {
    return of({});      
  }

  removeUser() { }
}

const mockActivatedRoute = {
  snapshot: { 
    queryParams: {
      ['returnUrl']: '/'
    }
  }
};

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        { provide: AccountService, useClass: MockAccountService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fail validation with blank email', () => {
    // arrange
    component.form.setValue({
      email: "",
      password: "Abc123!"
    });

    // act
    component.onSubmit();

    // assert
    expect(component.form.invalid).toBe(true);
  });

  it('should fail validation with blank password', () => {
    // arrange
    component.form.setValue({
      email: "test@test.com",
      password: ""
    });

    // act
    component.onSubmit();

    // assert
    expect(component.form.invalid).toBe(true);
  });
  
  it('should login with valid creds', () => {
    // arrange
    component.form.setValue({
      email: "test@test.com",
      password: "Abc123!"
    });

    // act
    component.onSubmit();

    // assert
    expect(component.form.invalid).toBe(false);
    expect(component.status).toBe(HttpRequestStatus.Successful);
  });

  it('should not login with invalid creds', () => {
    // arrange
    component.form.setValue({
      email: "bad-email",
      password: "Abc123!"
    });

    // act
    component.onSubmit();

    // assert
    expect(component.form.invalid).toBe(false);
    expect(component.status).toBe(HttpRequestStatus.Failed);
  });
});
