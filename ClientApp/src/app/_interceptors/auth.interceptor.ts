import { HttpInterceptorFn } from '@angular/common/http';
import { User } from '@app/_models/user';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  let idToken = '';
  let user: User = JSON.parse(localStorage.getItem('user')!);

  if (user && user.token) {
    idToken = user.token;
  }

  if (idToken) {
    const cloned = req.clone({
      headers: req.headers.set("Authorization", "Bearer " + idToken)
    });

    return next(cloned);
  } else {
      return next(req);
  }
};
