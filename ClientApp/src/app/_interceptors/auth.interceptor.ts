import { HttpInterceptorFn } from '@angular/common/http';
import { User } from '@app/_models/user';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  let user: User = JSON.parse(localStorage.getItem('user')!);

  if (user && user.token) {
    const cloned = req.clone({
      headers: req.headers.set("Authorization", "Bearer " + user.token)
    });

    return next(cloned);
  } else {
      return next(req);
  }
};
