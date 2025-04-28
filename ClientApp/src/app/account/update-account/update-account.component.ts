import { NgClass, NgIf } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpRequestStatus } from '@app/_models/http-request-status';
import { User } from '@app/_models/user';
import { AccountService } from '@app/_services/account.service';
import { AlertService } from '@app/_services/alert.service';
import { passwordMatchValidator } from '@app/_validators/password-match.validator';
import { first } from 'rxjs';

@Component({
  selector: 'app-update-account',
  imports: [FormsModule, ReactiveFormsModule, NgIf, NgClass],
  templateUrl: './update-account.component.html',
  styleUrl: './update-account.component.css'
})
export class UpdateAccountComponent implements OnInit {
  user?: User | null;
  form!: FormGroup;
  editPassword = false;
  submitted = false;
  status = HttpRequestStatus.NotSent;

  constructor(
    private formBuilder: FormBuilder,
    private accountService: AccountService,
    private alertService: AlertService
  ) { }

  ngOnInit(): void {
    this.accountService.user.subscribe(u => this.buildForm(u));
  }

  buildForm(user: User | null) {
    this.user = user;
    this.form = this.formBuilder.group({      
      email: [user?.email, [Validators.required, Validators.email]],
      firstName: [user?.firstName, [Validators.required, Validators.maxLength(100)]],
      lastName: [user?.lastName, [Validators.required, Validators.maxLength(100)]],

      // Passwords only required if "Edit Password" is clicked
      currentPassword: ['', [Validators.minLength(6), Validators.maxLength(100)]],
      password: ['', [Validators.minLength(6), Validators.maxLength(100), Validators.pattern('^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[^A-Za-z0-9]).*$')]],
      confirmPassword: ['', [Validators.minLength(6), Validators.maxLength(100), Validators.pattern('^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[^A-Za-z0-9]).*$')]]
    });
  }

  // convenience getters for easy access to form fields
  get email() {
    return this.form.get('email');
  }  
  get firstName() {    
    return this.form.get('firstName');
  }
  get lastName() {    
    return this.form.get('lastName');
  }
  get currentPassword() {    
    return this.form.get('currentPassword');
  }
  get password() {    
    return this.form.get('password');
  }
  get confirmPassword() {    
    return this.form.get('confirmPassword');
  }

  // convenience method to determine if the component is working
  get loading() {
    return this.status === HttpRequestStatus.InProgress;
  }

  setEditPassword(editPassword: boolean) {
    this.editPassword = editPassword;

    if (editPassword) {
      this.currentPassword?.addValidators([Validators.required]);
      this.password?.addValidators([Validators.required]);
      this.confirmPassword?.addValidators([Validators.required]);
      this.form.addValidators([passwordMatchValidator]);
    } else {
      this.currentPassword?.removeValidators([Validators.required]);
      this.currentPassword?.reset();

      this.password?.removeValidators([Validators.required]);
      this.password?.reset();

      this.confirmPassword?.removeValidators([Validators.required]);
      this.confirmPassword?.reset();
      this.form.removeValidators([passwordMatchValidator]);
    }
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
      this.accountService.update(this.form.value)
          .pipe(first())
          .subscribe({
              next: () => {
                this.status = HttpRequestStatus.Successful;
                this.alertService.success('Account update successful', { keepAfterRouteChange: true });
              },
              error: error => {
                this.status = HttpRequestStatus.Failed;
                this.alertService.error(this.inferErrorMessage(error));
              }
          });
    }
      
    inferErrorMessage(errResponse: HttpErrorResponse): string {
      if (errResponse.status === 500) {
        return "A server error occurred.  Please try again later.";
      }

      return errResponse.error;
    }

}
