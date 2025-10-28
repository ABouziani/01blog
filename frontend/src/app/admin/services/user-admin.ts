import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { User } from '../models/user';

@Injectable({ providedIn: 'root' })
export class UserAdminService {
  private apiUrl = 'http://localhost:8080/api/admin/users';

  constructor(private http: HttpClient) {}

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }

  banUser(id: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/ban`, {});
  }

  activateUser(id: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/activate`, {});
  }

  deleteUser(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  makeAdmin(id: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/make-admin`, {});
  }

  removeAdmin(id: string): Observable<void> {
     return this.http.put<void>(`${this.apiUrl}/${id}/remove-admin`, {})
    .pipe(
      catchError(error => {
        if (error.error == "one admin left") {
          alert("Platefrome cannot run without admin, move your admin privilage to someone else!");
        }
        return throwError(() => error); // rethrow so subscribers can handle it
      })
    );
  }
}