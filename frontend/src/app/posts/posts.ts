import { Component, OnInit, ViewEncapsulation, Input, ChangeDetectorRef, NgZone } from '@angular/core';
import { PostsService } from '../services/posts-service';
import { PostDto } from '../models/postDto';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { catchError, finalize, of, tap } from 'rxjs';

@Component({
  selector: 'app-posts',
  templateUrl: './posts.html',
  imports: [CommonModule, RouterLink],
  styleUrl: './posts.css',
  encapsulation: ViewEncapsulation.None
})
export class PostsComponent implements OnInit {
  @Input() posts: PostDto[] | null = null;
  loading = true;
  readonly PREVIEW_LENGTH = 150;

  private internalPosts: PostDto[] = [];

  constructor(
    public postsService: PostsService,
    private cdr: ChangeDetectorRef,
    private zone: NgZone
  ) { }

  ngOnInit(): void {
    if (this.posts !== null) {
      this.generateThumbnails(this.posts);
      this.loading = false;
      return;
    }

    this.postsService.getPosts().subscribe({
      next: (data) => {
        this.internalPosts = data;
        this.generateThumbnails(this.internalPosts);
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching posts:', err);
        this.loading = false;
      }
    });
  }

  get displayedPosts(): PostDto[] {
    return this.posts ?? this.internalPosts;
  }

  addReaction(postId: number, type: 'LIKE' | 'DISLIKE') {
    if (this.postsService.loadingReaction[postId]) return;
    this.postsService.addReaction(postId, type).pipe(
      tap(response => {
        const postUp: PostDto | undefined = this.displayedPosts.find(p => p.id == postId);
        if (!postUp) return;
        postUp.likes = response.likeCount;
        postUp.dislikes = response.dislikeCount;
      }),
      catchError(error => {
        console.error('Reaction error:', error);
        alert('Failed to react. Please try again.');
        return of(null);
      }),
      finalize(() => {
        this.postsService.loadingReaction[postId] = null;
      })
    )
      .subscribe();
  }

  getPreview(description: string): string {
    if (!description) return '';
    if (description.length <= this.PREVIEW_LENGTH) return description;

    let preview = description.substring(0, this.PREVIEW_LENGTH);
    const lastSpace = preview.lastIndexOf(' ');
    if (lastSpace > this.PREVIEW_LENGTH - 20) preview = preview.substring(0, lastSpace);

    return preview + '...';
  }

  // 📸 Capture a thumbnail from a video URL
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

  // 🧠 Generate thumbnails for all posts that have a video
  private generateThumbnails(posts: PostDto[]): void {
    posts.forEach(post => {
      if (post.media && this.isVideo(post.media.toString())) {
        this.getVideoThumbnail('http://localhost:8080' + post.media)
          .then(thumb => {
            // Update inside Angular zone so view refreshes
            this.zone.run(() => {
              post.thumbnail = thumb;
              this.cdr.detectChanges(); // force refresh
            });
          })
          .catch(err => console.error('Thumbnail error:', err));
      }
    });
  }

  isImage(url: string): boolean {
    return /\.(jpe?g|png|gif|webp)$/i.test(url);
  }

  isVideo(url: string): boolean {
    return /\.(mp4|webm|ogg|mov|avi|mkv)$/i.test(url);
  }
}
