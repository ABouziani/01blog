import { Component, OnInit, OnDestroy, ChangeDetectorRef, NgZone } from '@angular/core';
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
    private searchService: SearchService, private cdr: ChangeDetectorRef,
    private zone: NgZone
  ) {}

  ngOnInit(): void {
    this.subscription = this.route.queryParamMap
      .pipe(
        switchMap(params => {
          const q = params.get('q')?.trim() || '';
          const type = (params.get('type') || 'users') as 'users' | 'posts';

          this.currentQuery = q;
          this.currentType = type;

          if (!q) {
            return [{ loading: false, error: null, users: [], posts: [] }];
          }

          this.state = { ...this.state, loading: true, error: null };

          return this.searchService.searchRaw(type, q).pipe(
            
          );
        })
      )
      .subscribe({
        next: (result: any) => {

          this.generateThumbnails(result.posts);
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

  isVideo(url: string): boolean {
    return /\.(mp4|webm|ogg|mov|avi|mkv)$/i.test(url);
  }

  private getVideoThumbnail(videoUrl: string, seekTime: number = 1): Promise<string> {
    return new Promise((resolve, reject) => {
      const video = document.createElement('video');
      video.crossOrigin = 'anonymous';
      video.src = videoUrl;
      video.preload = 'metadata';

      video.addEventListener('loadedmetadata', () => {
        if (seekTime > video.duration) seekTime = 0;
        video.currentTime = seekTime;
      });

      video.addEventListener('seeked', () => {
        const canvas = document.createElement('canvas');
        canvas.width = video.videoWidth;
        canvas.height = video.videoHeight;
        const ctx = canvas.getContext('2d');
        if (!ctx) return reject('Canvas context error');
        ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
        resolve(canvas.toDataURL('image/jpeg'));
      });

      video.addEventListener('error', (err) => reject(err));
    });
  }

  private generateThumbnails(posts: any[]): void {
    posts.forEach(post => {
      if (post.media && this.isVideo(post.media.toString())) {
        this.getVideoThumbnail('http://localhost:8080' + post.media)
          .then(thumb => {
            this.zone.run(() => {
              post.thumbnail = thumb;
              this.cdr.detectChanges();
            });
          })
          .catch(err => console.error('Thumbnail error:', err));
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/']);
  }
}