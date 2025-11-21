import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { EMPTY, Observable, of, throwError } from 'rxjs';
import { Post } from '../post/post-model';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private apiUrl = 'http://localhost:8080/api/posts/getpost/';
  private deleteApiUrl = 'http://localhost:8080/api/posts/delete/';

  waiting: boolean = false;

  constructor(private http: HttpClient) { }

  getPost(postId: String | null): Observable<Post> {
    if (postId == null) {
      return EMPTY;
    }
    return this.http.get<Post>(this.apiUrl + postId);
  }

  deletePost(postId: string | null): Observable<{ message: string }> {
    if (postId == null) {
      return throwError(() => new Error('Invalid post ID'));
    }
    return this.http.delete<{ message: string }>(`${this.deleteApiUrl}${postId}`);
  }

  addReaction(postId: number, type: 'LIKE' | 'DISLIKE'): Observable<any> {
    // Optional: Prevent action if already loading
    if (this.waiting) return of(null);

    this.waiting = true;

    const token = localStorage.getItem('access_token'); // or however you store your JWT
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    const body = {
      postId: postId,
      type: type // "LIKE" or "DISLIKE"
    };

    return this.http.post('http://localhost:8080/api/reactions/add', body, { headers });
  }
}
