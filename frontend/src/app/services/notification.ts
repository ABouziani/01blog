import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { NotificationItem } from '../models/notification';
import { AuthService } from './auth';

@Injectable({
  providedIn: 'root'
})

export class NotificationService {
  private base = 'http://localhost:8080/api/notifications';

  constructor(private http: HttpClient, private authService: AuthService) {}

  private authHeaders(): { headers?: HttpHeaders } {
    const token = localStorage.getItem('access_token');
    if (token) {
      return { headers: new HttpHeaders({ Authorization: `Bearer ${token}` }) };
    }
    return {};
  }

  getNotifications(): Observable<NotificationItem[]> {
    return this.http.get<NotificationItem[]>(`${this.base}`, this.authHeaders())
      .pipe(
        catchError(err => {
          console.error('Failed fetching notifications', err);
          return of([] as NotificationItem[]);
        })
      );
  }

  // Mark provided notification ids as read on server
  markAsRead(ids: number[]): Observable<{ success: boolean } | any> {
    if (!ids || ids.length === 0) {
      return of({ success: true });
    }
    return this.http.post(`${this.base}/${ids[0]}/mark-read`, this.authHeaders())
      .pipe(
        map(() => ({ success: true })),
        catchError(err => {
          console.error('Failed to mark notifications as read', err);
          return of({ success: false, error: err });
        })
      );
  }

  // Mark provided notification ids as read on server
  markAllAsRead(): Observable<{ success: boolean } | any> {
    return this.http.post(`${this.base}/mark-all-read`, this.authHeaders())
      .pipe(
        map(() => ({ success: true })),
        catchError(err => {
          console.error('Failed to mark notifications as read', err);
          return of({ success: false, error: err });
        })
      );
  }
}
