import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { PostDto } from '../models/postDto';
import { catchError, finalize, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})


export class PostsService {
  private apiUrl = 'http://localhost:8080/api/posts/getallposts';

  constructor(private http: HttpClient) {}

  getPosts(): Observable<PostDto[]> {
    return this.http.get<PostDto[]>(this.apiUrl);
  }

  // Track loading state per post and reaction type
  loadingReaction: { [postId: number]: 'LIKE' | 'DISLIKE' | null } = {};

  addReaction(postId: number, type: 'LIKE' | 'DISLIKE'): Observable<any>{
    // Optional: Prevent action if already loading
    if (this.loadingReaction[postId]) return of(null);

    this.loadingReaction[postId] = type;

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
      // .pipe(
      //   tap(response => {
      //     // // ✅ Optimistically update the UI
      //     // this.updatePostReaction(postId, type);
      //     console.log(response);
      //   }),
      //   catchError(error => {
      //     console.error('Reaction error:', error);
      //     // Optional: show error message
      //     alert('Failed to react. Please try again.');
      //     return of(null);
      //   }),
      //   finalize(() => {
      //     // Re-enable button
      //     this.loadingReaction[postId] = null;
      //   })
      // )
      // .subscribe();
  }
}
