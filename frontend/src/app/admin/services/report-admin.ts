import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PostReport, Report } from '../models/report';

@Injectable({ providedIn: 'root' })
export class ReportAdminService {
  private apiUrl = 'http://localhost:8080/api/admin/reports';
  private apipostUrl = 'http://localhost:8080/api/admin/postreports';

  constructor(private http: HttpClient) {}

  getReports(): Observable<{userReports:Report[],postReports:PostReport[]}> {
    return this.http.get<{userReports:Report[],postReports:PostReport[]}>(this.apiUrl);
  }

  resolveReport(id: string, action: 'ban_user' | 'delete' | 'dismiss'): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/resolve`, { action });
  }

  resolvePostReport(id: string, action: 'delete_post' | 'hide_post' | 'dismiss' | 'delete_report'): Observable<void> {
    return this.http.post<void>(`${this.apipostUrl}/${id}/resolve`, { action });
  }
}