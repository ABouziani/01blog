import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, EMPTY, throwError, switchMap } from 'rxjs';
import { AuthService } from '../services/auth';
import { Router } from '@angular/router';
import { AlertService } from '../services/alert-service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const myAlert = inject(AlertService);

  const token = localStorage.getItem('access_token');
  const refresh = localStorage.getItem('refresh_token');

  // Attach token when it exists
  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  return next(req).pipe(
    catchError((error) => {
      // 401 Unauthorized (token expired)
      if (error.status === 401) {
        authService.logout();
        return EMPTY;
      }

      // 403 Forbidden (user not allowed)
      // if ((error.status === 403 || error.status === 401) && !req.url.endsWith('/login') ) {
      if (error.status === 403 && !req.url.endsWith('/login')) {
        console.warn('Access denied:', req.url);
        // Option 1: redirect to a forbidden page
        router.navigate(['/forbidden']);
        return EMPTY;
      }

      if (error.status === 0) {
        console.log("Server unreachable");
        // myAlert.showAlert("Error", "Server unreachable!");
        router.navigate(['/unreachable']);
        return EMPTY;
      }

      // Other errors
      return throwError(() => error);
    })
  );
};
