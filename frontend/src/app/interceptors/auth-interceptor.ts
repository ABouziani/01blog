// src/app/interceptors/auth.interceptor.ts
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandlerFn,
  HttpEvent,
  HttpInterceptorFn,
  HttpErrorResponse,
  HttpClient
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth';

let isRefreshing = false;

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  // Add token to requests (skip auth endpoints)
  const token = localStorage.getItem('access_token');
  console.log("------------" + token);
  if (token && !req.url.includes('/api/auth/')) {
    req = req.clone({
      setHeaders: { 'Authorization': `Bearer ${token}` }
    });
  }

  return next(req).pipe(
    catchError(error => {
      if (error instanceof HttpErrorResponse && error.status === 401) {
        return handle401Error(req, next);
      }
      return throwError(() => error);
    })
  );
};

function handle401Error(
  req: HttpRequest<unknown>,
  next: HttpHandlerFn,
): Observable<HttpEvent<unknown>> {
  if (!isRefreshing) {
    isRefreshing = true;

    const refreshToken = localStorage.getItem('refresh_token');
    if (!refreshToken) {
      localStorage.removeItem('access_token');
      localStorage.removeItem('refresh_token');
      // Optional: redirect to login
      window.location.href = '/signin';
      isRefreshing = false;
      return throwError(() => new Error('No refresh token'));
    }

    // ✅ Use a standalone HttpClient instance (via inject)
    const http = inject(HttpClient);
    return http.post<{ accessToken: string }>('/api/auth/refresh-token', { refreshToken }).pipe(
      switchMap(response => {
        const newToken = response.accessToken;
        localStorage.setItem('access_token', newToken);
        isRefreshing = false;
        const clonedReq = req.clone({
          setHeaders: { Authorization: `Bearer ${newToken}` }
        });
        return next(clonedReq);
      }),
      catchError(err => {
        isRefreshing = false;
        localStorage.removeItem('access_token');
        localStorage.removeItem('refresh_token');
        window.location.href = '/signin';
        return throwError(() => err);
      })
    );
  }
  // If already refreshing, just retry original request
  return next(req);
}