import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Post } from '../models/post';

@Injectable({ providedIn: 'root' })
export class PostAdminService {
  private apiUrl = 'http://localhost:8080/api/admin/posts';

  constructor(private http: HttpClient) {}

  getPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(this.apiUrl);
  }

  hidePost(id: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/hide`, {});
  }

  deletePost(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}