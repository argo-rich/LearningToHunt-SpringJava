import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AlertService } from '@app/_services/alert.service';

@Component({
  selector: 'app-confirmation',
  imports: [],
  templateUrl: './confirmation.component.html',
  styleUrl: './confirmation.component.css'
})
export class ConfirmationComponent implements OnInit {
  serverSideConfirmSuccess!: boolean;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private alertService: AlertService
  ) {}

  ngOnInit(): void {
    this.serverSideConfirmSuccess = this.route.snapshot.paramMap.get('confirmationStatus')! === 'success';
    if (this.serverSideConfirmSuccess) {
      this.alertService.success('Email successfully confirmed.  Please log in.', { keepAfterRouteChange: true });
      this.router.navigate(['../../login'], { relativeTo: this.route });
    } else {
      this.alertService.warn('An error occurred with email confirmation.  Please click the link in your "Confirm your email" message and try again.');
    }
  }
}
