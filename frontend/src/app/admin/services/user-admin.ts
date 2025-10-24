import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user';

@Injectable({ providedIn: 'root' })
export class UserAdminService {
  private apiUrl = 'http://localhost:8080/api/admin/users';

  constructor(private http: HttpClient) {}

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }

  banUser(id: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/ban`, {});
  }

  deleteUser(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}