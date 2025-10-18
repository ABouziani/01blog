// comment.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CommentDto } from '../models/comment';

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/comments/add';

  addComment(postId: string, content: string): Observable<CommentDto> {
    const body = { postId, content };
    return this.http.post<CommentDto>(this.apiUrl, body);
  }
}