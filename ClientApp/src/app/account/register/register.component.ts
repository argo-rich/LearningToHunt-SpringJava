import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { first } from 'rxjs/operators';
import {NgIf, NgClass} from '@angular/common';
import { AccountService } from '../../_services/account.service';
import { AlertService } from '../../_services/alert.service';
import { HttpRequestStatus } from '@app/_models/http-request-status';
import { passwordMatchValidator } from '../../_validators/password-match.validator';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-register',
  imports: [FormsModule, ReactiveFormsModule, NgIf, NgClass],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit {
  form!: FormGroup;
  submitted = false;
  status = HttpRequestStatus.NotSent;

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private accountService: AccountService,
    private alertService: AlertService
  ) { }

  ngOnInit() {
    this.form = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(100), Validators.pattern('^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[^A-Za-z0-9]).*$')]],
      confirmPassword: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(100), Validators.pattern('^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[^A-Za-z0-9]).*$')]],
      firstName: ['', [Validators.required, Validators.maxLength(100)]],
      lastName: ['', [Validators.required, Validators.maxLength(100)]]
    },
    { validators: passwordMatchValidator });
  }

  // convenience getters for easy access to form fields
  get email() {    
    return this.form.get('email');
  }
  get password() {    
    return this.form.get('password');
  }
  get confirmPassword() {    
    return this.form.get('password');
  }
  get firstName() {    
    return this.form.get('firstName');
  }
  get lastName() {    
    return this.form.get('lastName');
  }

  // convenience method to determine if the component is working
  get loading() {
    return this.status === HttpRequestStatus.InProgress;
  }

  onSubmit() {
    this.submitted = true;

    // reset alerts on submit
    this.alertService.clear();

    // stop here if form is invalid
    if (this.form.invalid) {
      return;
    }

    this.status = HttpRequestStatus.InProgress;
    this.accountService.register(this.form.value)
        .pipe(first())
        .subscribe({
            next: () => {
              this.status = HttpRequestStatus.Successful;
              this.alertService.success('Registration successful!  A confirmation email is headed to your inbox.', { keepAfterRouteChange: true });
              this.router.navigate(['../login'], { relativeTo: this.route });
            },
            error: error => {
              this.status = HttpRequestStatus.Failed;
              this.alertService.error(this.inferErrorMessage(error));
            }
        });
  }
    
  inferErrorMessage(errResponse: HttpErrorResponse): string {
    if (errResponse.status === 500){
      return "We're sorry!  A server side error occurred, probably related to sending your email confirmation.  We are looking into it.";
    }      
    
    let message = "";
    let count = 0;

    for (let key in errResponse.error.errors) {
      message = errResponse.error.errors[key];
      if (++count > 1) {
        message += "<br>";
      }
    }
    
    return message;
  }
}
