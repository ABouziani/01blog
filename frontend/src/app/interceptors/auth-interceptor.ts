import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('access_token');

  // if (token && req.url.includes('/api/admin/')) {
  req = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });
  // }

  return next(req);
};


// return next(req).pipe(
  //   catchError((error) => {
  //     // Handle 401 (Unauthorized)      
  //     if ((error.status === 403 || error.status === 401) && !req.url.endsWith('/login') ) {
  //       localStorage.removeItem('access_token');
  //       authService.loggedIn.next(false);
  //       router.navigate(['']);
  //     }

  //     return throwError(() => error);
  //   })
  // );