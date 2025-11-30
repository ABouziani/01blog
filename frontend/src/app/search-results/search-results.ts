import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription, switchMap } from 'rxjs';
import { SearchService } from '../services/search';
import { UserDto } from '../models/userDto';
import { PostDto } from '../models/postDto';
import { SearchBarComponent } from '../search/search';

interface SearchResultState {
  loading: boolean;
  error: string | null;
  users: UserDto[];
  posts: PostDto[];
}

@Component({
  selector: 'app-search-results',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatIconModule,
    MatButtonModule,
    SearchBarComponent
  ],
  templateUrl: `./search-results.html`,
  styleUrls: [`./search-results.css`, '../posts/posts.css','../user/user.css']
})
export class SearchResultsComponent implements OnInit {
  state: SearchResultState = {
    loading: false,
    error: null,
    users: [],
    posts: []
  };

  currentQuery = '';
  currentType: 'users' | 'posts' = 'users';

  private subscription!: Subscription;

  constructor(
    private route: ActivatedRoute,
    public router: Router,
    private searchService: SearchService
  ) {}

  ngOnInit(): void {
    // ✅ React to paramMap changes (e.g., back/forward, new search)
    this.subscription = this.route.queryParamMap
      .pipe(
        switchMap(params => {
          const q = params.get('q')?.trim() || '';
          const type = (params.get('type') || 'users') as 'users' | 'posts';

          this.currentQuery = q;
          this.currentType = type;

          if (!q) {
            // No query → clear results
            return [{ loading: false, error: null, users: [], posts: [] }];
          }

          this.state = { ...this.state, loading: true, error: null };

          return this.searchService.searchRaw(type, q).pipe(
            // searchRaw returns { users, posts } depending on type
            // We'll define it below
          );
        })
      )
      .subscribe({
        next: (result: any) => {
          console.log("-+-+-+-+-+-",result);
          
          if (Array.isArray(result)) {
            // Legacy fallback (shouldn’t happen)
            result = { users: [], posts: result };
          }
          this.state = {
            loading: false,
            error: null,
            users: result.users || [],
            posts: result.posts || []
          };
        },
        error: (err) => {
          console.error('Search failed', err);
          this.state = {
            loading: false,
            error: 'Failed to load results. Please try again.',
            users: [],
            posts: []
          };
        }
      });
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }


  goBack(): void {
    this.router.navigate(['/']);
  }
}