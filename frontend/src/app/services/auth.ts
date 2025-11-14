// src/app/services/auth.service.ts
import { Injectable, signal } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { Router } from '@angular/router';

interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  admin: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly TOKEN_KEY = 'access_token';
  private readonly REFRESH_KEY = 'refresh_token';

  public loggedIn = new BehaviorSubject<boolean>(this.hasValidToken());
  private isadmin = signal<boolean>(false);
  public loggedId = signal<number>(0);

  constructor(private http: HttpClient, public router: Router) {
    // On app start: check if we have a token AND if it's valid on backend
    this.checkAuthStatus();
  }

  private checkAuthStatus(): void {
    const token = localStorage.getItem(this.TOKEN_KEY);
    if (!token) {
      this.loggedIn.next(false);
      return;
    }
    // Send token to backend to verify
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    this.http.get<{ id: number, owner: boolean, role: string }>('http://localhost:8080/api/auth/islogged', { headers })
      .pipe(
        map(response => {
          console.log("oninit : ", response)
          this.isadmin.set(response.role == "ADMIN");
          this.loggedId.set(response.id);
          return true;
        }), // 200 → valid
        catchError((err) => {
          console.log(err);
          this.clearTokens(); // 401 or error → invalid
          return of(false);
        })
      )
      .subscribe(isValid => {
        this.loggedIn.next(isValid);
      });
  }

  login(identifier: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>('http://localhost:8080/api/auth/login', { identifier, password }).pipe(
      tap(response => {
        console.log(response.accessToken);
        localStorage.setItem(this.TOKEN_KEY, response.accessToken);
        localStorage.setItem(this.REFRESH_KEY, response.refreshToken);
        this.loggedIn.next(true);
        this.isadmin.set(response.admin);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_KEY);
    this.loggedIn.next(false);
    this.isadmin.set(false);
    this.router.navigate(['/'])
  }

  isLoggedIn(): boolean {
    return this.loggedIn.value;
  }
  setLoggedIn(b:boolean): void {
    this.loggedIn.next(false);
  }
  isAdmin(): boolean {
    return this.isadmin();
  }

  private hasValidToken(): boolean {
    return !!localStorage.getItem(this.TOKEN_KEY);
  }

  private clearTokens(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_KEY);
  }

  // In auth.service.ts, add this method:
  refreshToken(refreshToken: string): Observable<string> {
    return this.http.post<{ accessToken: string }>('/api/auth/refresh-token', { refreshToken }).pipe(
      tap(response => {
        localStorage.setItem(this.TOKEN_KEY, response.accessToken);
        this.loggedIn.next(true);
      }),
      map(response => response.accessToken)
    );
  }
}