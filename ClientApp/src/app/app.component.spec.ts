import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';

//added
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { HttpErrorResponse, HttpHeaders, provideHttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { AccountService } from './_services/account.service';
import { User } from './_models/user';
import { Idle, LocalStorage, LocalStorageExpiry, InterruptSource, AutoResume, provideNgIdle } from '@ng-idle/core';
import { Keepalive, provideNgIdleKeepalive } from '@ng-idle/keepalive';
import { EventEmitter } from '@angular/core';

const logoutError = new HttpErrorResponse({
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
  private userSubject: BehaviorSubject<User | null>;
  public user: Observable<User | null>;
  
  constructor() {
      this.userSubject = new BehaviorSubject(JSON.parse(localStorage.getItem('user')!));
      this.user = this.userSubject.asObservable();
  }

  logout(): Observable<object> {
    return (true) ? of({}) : throwError(() => logoutError);
  }

  removeUser() {
    // remove user from local storage and set current user to null
    localStorage.removeItem('user');
    this.userSubject.next(null);
  }

  login(email: string, password: string): Observable<User | null> {
    let user = new User();
    localStorage.setItem('user', JSON.stringify(user));
    this.userSubject.next(user);
    return this.user;
  }
}

const mockAccountService = new MockAccountService();

const localStorageMock = {
  getItem: (key: string): string | null => {
    return key in localStorageData ? localStorageData[key] : null;
  },
  setItem: (key: string, value: string) => {
    localStorageData[key] = value;
  },
  removeItem: (key: string) => {
    delete localStorageData[key];
  },
  clear: () => {
    localStorageData = {};
  },
};
let localStorageData: { [key: string]: any } = {};

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;

  beforeEach(async () => {

    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [
          { provide: AccountService, useValue: mockAccountService },
          { provide: LocalStorage, useValue: localStorageMock},
          { provide: LocalStorageExpiry, useClass: LocalStorageExpiry},
          provideRouter(routes),
          provideNgIdle(),
          provideNgIdleKeepalive(),
          provideHttpClient()
        ]
    }).compileComponents();
    
    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it(`should have the 'ClientApp' title`, () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app.title).toEqual('ClientApp');
  });

  it('should render nav', () => {
    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('header')).toBeTruthy();
  });

  it('should have null user on logout', async () => {
    // arrange

    // act
    component.logout();

    // assert
    expect(component).toBeTruthy();
    expect(component.user).toBe(null);
  });

  it('should have new User()', async () => {
    // arrange

    // act
    mockAccountService.login('test', 'test').subscribe();

    // assert
    expect(component).toBeTruthy();
    expect(component.user).toEqual(new User());
    expect(component.idleState).toEqual('Not started.');
  });
});