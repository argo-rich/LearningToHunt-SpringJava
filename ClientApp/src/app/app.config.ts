// default imports
import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';

// added imports
import { provideHttpClient } from "@angular/common/http";
import { provideNgIdle } from '@ng-idle/core';
import { provideNgIdleKeepalive } from '@ng-idle/keepalive';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }), 
    provideRouter(routes),
    provideHttpClient(),
    provideNgIdle(),
    provideNgIdleKeepalive()
  ]
};
