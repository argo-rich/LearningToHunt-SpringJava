import { Component, OnInit } from '@angular/core';
import { NgClass, NgIf } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpRequestStatus } from '@app/_models/http-request-status';
import { AccountService } from '@app/_services/account.service';
import { AlertService } from '@app/_services/alert.service';
import { first } from 'rxjs';
import { passwordMatchValidator } from '@app/_validators/password-match.validator';
import { ActivatedRoute, Router } from '@angular/router';

export enum FPSubmitted {
  None,
  Email,
  ResetCode,
  PasswordUpdate
}

@Component({
  selector: 'app-forgot-password',
  imports: [FormsModule, ReactiveFormsModule, NgIf, NgClass],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css'
})
export class ForgotPasswordComponent implements OnInit {
  FPSubmitted = FPSubmitted;
  emailUser!: string;
  form!: FormGroup;
  status = HttpRequestStatus.NotSent;
  fpSubmitted = FPSubmitted.None;
  submitted = false;
  resetCode!: number;
  token!: string;

    constructor(
      private formBuilder: FormBuilder,
      private accountService: AccountService,
      private alertService: AlertService,
      private route: ActivatedRoute,
      private router: Router
    ) { }

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      email: ['', Validators.required]
    });
  }

  // convenience getters for easy access to form fields
  get email() {
    return this.form.get('email');
  }

  get resetCodeInput() {
    return this.form.get('resetCodeInput');
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

  onSubmit() {
      this.submitted = true;

      // reset alerts on submit
      this.alertService.clear();

      // stop here if form is invalid
      if (this.form.invalid) {
        return;
      }

      this.status = HttpRequestStatus.InProgress;

      switch (this.fpSubmitted) {
        case FPSubmitted.None:
          this.requestResetCode();
          break;
        case FPSubmitted.Email:
          this.validateResetCode();
          break;
        case FPSubmitted.ResetCode:
          this.updatePassword();
          break;
      }
  }

  requestResetCode() {
    this.emailUser = this.email!.value;
    this.accountService.forgotPassword(this.email!.value)
      .pipe(first())
      .subscribe({
          next: resetToken => {
            this.resetCode = resetToken.resetCode;
            this.token = resetToken.token;
            this.status = HttpRequestStatus.Successful;
            this.fpSubmitted = FPSubmitted.Email;
            this.alertService.success('Please check your email for password reset instructions', { keepAfterRouteChange: false });
            this.submitted = false;
            this.form = this.formBuilder.group({
              resetCodeInput: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(6)]]
            });
          },
          error: error => {
            this.status = HttpRequestStatus.Failed;
            this.alertService.error("Invalid email address.  Please try again.");
          }
      });
  }

  validateResetCode() {
    if (this.resetCodeInput!.value !== this.resetCode.toString()) {
      this.status = HttpRequestStatus.Failed;
      this.alertService.error("Invalid reset code.  Please try again.");
      return;
    }

    this.fpSubmitted = FPSubmitted.ResetCode;
    this.status = HttpRequestStatus.Successful;
    this.alertService.success('Reset code validated.  Please enter your new password.', { keepAfterRouteChange: false });
    this.submitted = false;
    this.form = this.formBuilder.group({
      password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(100), Validators.pattern('^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[^A-Za-z0-9]).*$')]],
      confirmPassword: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(100), Validators.pattern('^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[^A-Za-z0-9]).*$')]]
    },
    { validators: passwordMatchValidator });
  }

  updatePassword() {
    let resetParams = {
      email: this.emailUser,
      resetToken: this.token,
      newPassword: this.password!.value
    };

    this.accountService.updateForgottenPassword(resetParams)
      .pipe(first())
      .subscribe({
          next: () => {
            this.status = HttpRequestStatus.Successful;
            this.alertService.success('Password updated successfully.  Please log in.', { keepAfterRouteChange: true });
            this.fpSubmitted = FPSubmitted.PasswordUpdate;
            this.router.navigate(['../login'], { relativeTo: this.route });
          },
          error: error => {
            this.status = HttpRequestStatus.Failed;
            this.alertService.error("Password update failed.  Please try again.");
          }
      }
    );
  }
}
