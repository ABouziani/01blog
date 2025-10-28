import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Report } from '../models/report';

@Injectable({ providedIn: 'root' })
export class ReportAdminService {
  private apiUrl = 'http://localhost:8080/api/admin/reports';

  constructor(private http: HttpClient) {}

  getReports(): Observable<Report[]> {
    return this.http.get<Report[]>(this.apiUrl);
  }

  resolveReport(id: string, action: 'ban_user' | 'delete' | 'dismiss'): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/resolve`, { action });
  }
}