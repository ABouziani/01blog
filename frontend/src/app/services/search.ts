// src/app/services/search.service.ts

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { switchMap, map, catchError, tap } from 'rxjs/operators';
import { PostDto } from '../models/postDto';
import { UserDto } from '../models/userDto';

export type SearchType = 'users' | 'posts';
export interface SearchParams {
  type: SearchType;
  query: string;
}

@Injectable({
  providedIn: 'root'
})
export class SearchService {
  // 🔁 Shared state: current search params
  apiUrl = "http://localhost:8080/api/search";
  private searchParams$$ = new BehaviorSubject<SearchParams | null>(null);
  searchParams$ = this.searchParams$$.asObservable();

  // 📦 Shared state: current results (with loading/error)
  private results$$ = new BehaviorSubject<{
    loading: boolean;
    error: string | null;
    users: UserDto[];
    posts: PostDto[];
  }>({
    loading: false,
    error: null,
    users: [],
    posts: []
  });

  results$ = this.results$$.asObservable();

  constructor(private http: HttpClient) {
    // 🔄 React to param changes → auto-fetch
    this.searchParams$
      .pipe(
        switchMap(params => {
          if (!params?.query.trim()) {
            // Clear results on empty query
            return of({
              loading: false,
              error: null,
              users: [],
              posts: []
            });
          }

          this.results$$.next({ ...this.results$$.value, loading: true });

          if (params.type === 'users') {
            return this.searchUsers(params.query).pipe(
              map(users => ({
                loading: false,
                error: null,
                users,
                posts: []
              }))
            );
          } else {
            return this.searchPosts(params.query).pipe(
              map(posts => ({
                loading: false,
                error: null,
                users: [],
                posts
              }))
            );
          }
        }),
        catchError(error => {
          console.error('Search failed', error);
          return of({
            loading: false,
            error: 'Failed to load results',
            users: [],
            posts: []
          });
        })
      )
      .subscribe(state => this.results$$.next(state));
  }

  search(params: SearchParams): void {
    this.searchParams$$.next(params);
  }

  clear(): void {
    this.searchParams$$.next(null);
  }

  private searchUsers(query: string): Observable<UserDto[]> {
    const params = new HttpParams().set('query', query.trim());
    return this.http.get<UserDto[]>(this.apiUrl + '/users', { params });
  }

  private searchPosts(query: string): Observable<PostDto[]> {
    const params = new HttpParams().set('query', query.trim());
    return this.http.get<PostDto[]>(this.apiUrl + '/posts', { params });
  }

  searchRaw(type: 'users' | 'posts', query: string): Observable<{ users: UserDto[]; posts: PostDto[] }> {
  if (type === 'users') {
    return this.searchUsers(query).pipe(
      map(users => ({ users, posts: [] }))
    );
  } else {
    return this.searchPosts(query).pipe(
      map(posts => ({ users: [], posts }))
    );
  }
}
}