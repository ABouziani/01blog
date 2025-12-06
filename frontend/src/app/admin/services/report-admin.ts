import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PostReport, Report } from '../models/report';

@Injectable({ providedIn: 'root' })
export class ReportAdminService {
  private apiUrl = 'http://localhost:8080/api/admin';

  constructor(private http: HttpClient) {}

  getReports(): Observable<{userReports:Report[],postReports:PostReport[]}> {
    return this.http.get<{userReports:Report[],postReports:PostReport[]}>(`${this.apiUrl}/reports`);
  }

  resolveUserReport(userId: string, reportId: string, action: 'ban_user' | 'delete' | 'dismiss'): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/userreports/${userId}/${reportId}/resolve`, { action });
  }

  resolvePostReport(postId: string, reportId:String, action: 'delete_post' | 'hide_post' | 'dismiss' | 'delete_report'): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/postreports/${postId}/${reportId}/resolve`, { action });
  }
}