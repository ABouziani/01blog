import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef, Component, NgZone } from '@angular/core';
import { Router } from '@angular/router';
import { SearchBarComponent } from '../search/search';

interface recentPosts { id: number; title: string; creatorId: number; creatorUsername: string; media: string; thumbnail: string; }
interface activeUsers { id: number; name: string; posts: number; avatar: string; }
interface topPosts { id: number; title: string; creatorId: number; creatorUsername: string; media: string; thumbnail: string; }


@Component({
  selector: 'app-home',
  imports: [CommonModule, SearchBarComponent],
  standalone: true,
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class HomeComponent {
  apiUrl = 'http://localhost:8080/api/home';
  constructor(private http: HttpClient, public router: Router, private cdr: ChangeDetectorRef,
    private zone: NgZone) { }
  recentPosts: recentPosts[] = [];
  activeUsers: activeUsers[] = [];
  topPosts: topPosts[] = [];

  trendingTags = [
    { label: 'Java', primary: false },
    { label: 'Springboot', primary: false },
    { label: 'Angular', primary: false },
    { label: 'Golang', primary: false },
    { label: 'Javascript', primary: false },
    { label: 'AI', primary: false },
    { label: 'Rust', primary: false },
    { label: 'Hackathon', primary: false },
  ];

  ngOnInit(): void {

    this.http.get<{ recentPosts: recentPosts[], activeUsers: activeUsers[], topPosts: topPosts[] }>(this.apiUrl).subscribe({
      next: (resp) => {
        this.recentPosts = resp.recentPosts;
        this.activeUsers = resp.activeUsers;
        this.topPosts = resp.topPosts;
        this.generateThumbnails(this.recentPosts);
        this.generateThumbnails(this.topPosts);
      },
      error: (err) => {
        console.error('Error fetching Home data:', err);
      }
    });
  }

  isImage(url: string): boolean {
    return /\.(jpe?g|png|gif|webp)$/i.test(url);
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

  // 🧠 Generate thumbnails for all posts that have a video
  private generateThumbnails(posts: any[]): void {
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
}