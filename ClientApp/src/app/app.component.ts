import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';
import { DatePipe, NgIf } from '@angular/common';
import { AlertComponent } from './_components/alert.component';
import { User } from './_models/user';
import { AccountService } from './_services/account.service';
import { Idle, DEFAULT_INTERRUPTSOURCES, EventTargetInterruptSource } from '@ng-idle/core';
import { Keepalive } from '@ng-idle/keepalive';
import { environment } from 'environments/environment';
import { ModalDirective, ModalModule } from 'ngx-bootstrap/modal';
import { AlertService } from './_services/alert.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, DatePipe, AlertComponent, NgIf, ModalModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
  standalone: true
})
export class AppComponent implements AfterViewInit {
  title = 'ClientApp';
  currDate = Date.now();
  user?: User | null;

  // session timeout vars
  idleState = 'Not started.';
  lastPing!: Date;

 @ViewChild('childModal', { static: false }) childModal!: ModalDirective;

 @ViewChild('stayButton', { static: false }) stayButton!: ElementRef<HTMLButtonElement>;

  constructor(
    private accountService: AccountService, 
    private alertService: AlertService,
    private idle: Idle, 
    private keepalive: Keepalive
  ) {    
    // sets the idle timeout (i.e.: the amount of idle time before we want to prompt the user)
    idle.setIdle(environment.idleTimeout);
    // sets a timeout period on top of the idle timeout that the user will be considered timed out
    idle.setTimeout(environment.totalSessionTimeout - environment.idleTimeout);
    // sets the default interrupts, in this case, things like clicks, scrolls, touches to the document
    idle.setInterrupts(DEFAULT_INTERRUPTSOURCES);

    idle.onIdleEnd.subscribe(() => {
      if (!this.childModal || !this.childModal.isShown) {
        this.idleState = 'No longer idle.'
        this.reset();
      }
    });
    
    idle.onTimeout.subscribe(() => {
      this.idleState = 'Timed out!';
      this.logout("You have been logged out due to inactivity.");
    });
    
    idle.onIdleStart.subscribe(() => {
      this.idleState = 'Idle has started.'
      this.childModal.show();
    });
    
    idle.onTimeoutWarning.subscribe((countdown) => {
      let minutes = Math.floor(countdown / 60);
      let seconds = Math.abs((minutes * 60 ) - countdown);
      this.idleState = `You will be automatically logged out in ${minutes} minute(s), ${seconds} seconds!`;
      let source = new EventTargetInterruptSource(this.stayButton.nativeElement, 'click');
      idle.setInterrupts([source]);
    });

    this.keepalive.interval(environment.pingInterval);

    this.keepalive.onPing.subscribe(() => {
      this.accountService.ping().subscribe({
          next: (user) => {
            this.lastPing = new Date();
          },
          error: error => {
              // if error.status === 401 the user was already logged out on the server, logging them out here too
              if (error.status === 401) {
                this.logout();
              }
          }          
      });        
    });
    
    this.accountService.user.subscribe(u => {
      this.user = u;
      if (this.user) {
        idle.watch();
        keepalive.start();
      } else {
        idle.stop();
        keepalive.stop();
      }
    });
  }

  ngAfterViewInit(): void {
    this.childModal.config = {
      backdrop: 'static',
      keyboard: false
    };
  }

  /**
   * Resets the idle timer
   */
  reset() {
    if (!this.childModal || !this.childModal.isShown) {
      this.idle.watch();
      this.idleState = 'Started.';
    }
  }

  stay() {
    this.idle.setInterrupts(DEFAULT_INTERRUPTSOURCES);
    this.childModal.hide();
    this.reset();
  }

  logout(msg?: string) {
    this.childModal.hide();
    this.accountService.logout().subscribe(() =>{
      this.accountService.removeUser();
      this.user = null;
      this.alertService.warn(msg || "You have been logged out.");
    });
  }
}
