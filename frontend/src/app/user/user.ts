import { ChangeDetectorRef, Component, Input, NgZone } from '@angular/core';
import { UserDto } from '../models/userDto';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../services/auth';
import { EditprofileComponent } from '../editprofile/editprofile';
import { PostsService } from '../services/posts-service';
import { FormsModule } from '@angular/forms';
import { Location } from '@angular/common';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { AlertDialogComponent } from '../alert-dialog/alert-dialog';
import { MatButtonModule } from '@angular/material/button';
import { AlertService } from '../services/alert-service';
import { SearchBarComponent } from '../search/search';


@Component({
  selector: 'app-user',
  imports: [CommonModule, FormsModule, EditprofileComponent, MatDialogModule,
    MatButtonModule, SearchBarComponent, RouterLink],
  templateUrl: './user.html',
  styleUrls: ['./user.css', '../posts/posts.css']
})
export class UserComponent {
  user: UserDto | null = null;
  error: string | null = null;
  reporterror: string | null = null;
  isEditing = false;
  loadingMore = false;
  allLoaded = false;
  lastPostId: number = 0;
  pageSize: number = 9;
  private scrollTimer: any;
  loading = true;
  showPopup = false;
  reason = '';

  constructor(private postsService: PostsService, private http: HttpClient,
    private route: ActivatedRoute, private auth: AuthService,
    public router: Router, private location: Location, private snack: MatSnackBar,
    private dialog: MatDialog,
    private myAlert: AlertService, private cdr: ChangeDetectorRef,
    private zone: NgZone) { }

  ngOnInit(): void {
    let userId = this.route.snapshot.paramMap.get('id');
    if (userId == null) userId = "0";
    this.fetchUser(userId);
    window.addEventListener('scroll', this.onScroll, { passive: true });
  }

  fetchUser(id: String | null): void {
    this.http.get<UserDto>(`http://localhost:8080/api/users/${id}`)
      .subscribe({
        next: (data) => {
          // data.posts.forEach(post => {
          //   post.creatorId = data.id.toString();
          //   post.creatorUsername = data.username;
          // });
          data.posts = [];
          this.user = data;
          this.loadMore();
          if (data.id == this.auth.loggedId()) {
            this.user.owner = true;
          }
          // this.location.go('/users/' + this.user?.id.toString());
        },
        error: (err) => {
          
          // this.error = err.error.message;
          this.error = err.error;
        }
      });
  }

  follow() {
    const token = localStorage.getItem('access_token'); // or however you store your JWT
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    this.http.post<{ message: string }>(`http://localhost:8080/api/follow/${this.user?.id}`, { headers })
      .subscribe({
        next: (data) => {
          this.myAlert.showAlert("Success", data.message);
          if (this.user) {
            this.user.alreadyFollowed = true;
            this.user.followers.length++
          };
        },
        error: (err) => {
          console.error('Error following user:', err);
          this.error = 'Failed to follow this user!';
          setTimeout(() => {
            this.error = '';
          }, 1500);
        }
      });
  }


  unfollow() {
    const token = localStorage.getItem('access_token'); // or however you store your JWT
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    this.http.delete<{ message: string }>(`http://localhost:8080/api/follow/${this.user?.id}`, { headers })
      .subscribe({
        next: (data) => {
          this.myAlert.showAlert("Success", data.message);
          if (this.user) {
            this.user.alreadyFollowed = false;
            this.user.followers.length--;
          }
        },
        error: (err) => {
          console.error('Error unfollowing user:', err);
          this.error = 'Failed to unfollow this user!';
          setTimeout(() => {
            this.error = '';
          }, 1500);
        }
      });
  }

  openPopup() {
    this.showPopup = true;
  }

  closePopup() {
    this.showPopup = false;
    this.reason = '';
  }

  reportUser() {

    if (!this.reason.trim()) {
      this.reporterror = 'Please provide a reason.';
      setTimeout(() => {
        this.reporterror = '';
      }, 1500);
      return;
    }

    if (this.reason.trim().length < 10 || this.reason.trim().length > 300) {
      this.reporterror = 'Reason must be between 10 and 300 characters!';
      setTimeout(() => {
        this.reporterror = '';
      }, 1500);
      return;
    }

    const body = {
      reportedId: this.user?.id,
      reason: this.reason
    };

    return this.http.post('http://localhost:8080/api/reports/adduserreport', body)
      .subscribe({
        next: (data) => {
          this.closePopup();
          this.myAlert.showAlert("Success", "User reported to admins successfully!");
          if (this.user) this.user.alreadyReported = true;
        },
        error: (err) => {
          console.error('Error reporting user:', err);
          this.error = 'Failed to report this user!';
          setTimeout(() => {
            this.error = '';
          }, 1500);
        }
      });
  }

  onUpdate(data: UserDto) {
    this.lastPostId = 0;
    data.posts = [];
    this.user = data;
    this.loadMore();
    if (data.id == this.auth.loggedId()) {
      this.user.owner = true;
    }
    this.isEditing = false;
  }

  showAlert(type: string, msg: string) {
    this.dialog.open(AlertDialogComponent, {
      data: {
        title: type,
        message: msg
      }
    });
  }

  cancelEdit(event: string) {
    if (event == 'cancel') this.isEditing = false;
  }

  loadMore() {
    if (this.loadingMore || this.allLoaded) return;
    this.loadingMore = true;

    setTimeout(() => {
      this.postsService.getUserPosts(this.user ? this.user.id : 0, this.lastPostId, this.pageSize).subscribe({
        next: (data) => {

          if (data.length == 0) {
            this.allLoaded = true;
            this.loadingMore = false;
            return;
          }
          if (this.user) {
            this.user.posts = [...this.user.posts, ...data];
            const oldestPost = this.user.posts.reduce((min, p) => p.id < min.id ? p : min, this.user.posts[0]);
            this.lastPostId = oldestPost.id;
            this.generateThumbnails(this.user.posts);
          }
          this.loading = false;
        },
        error: (err) => {
          console.error('Error fetching posts:', err);
          this.loading = false;
        },
        complete: () => {
          this.loadingMore = false;
        }
      });
    }, 1000);
  }

  onScroll = () => {
    // Clear any previous timer
    clearTimeout(this.scrollTimer);

    // Set a new timer — this runs only AFTER scrolling stops for 200ms
    this.scrollTimer = setTimeout(() => {
      const scrollTop = window.scrollY || document.documentElement.scrollTop;
      const viewportHeight = window.innerHeight;
      const fullHeight = document.documentElement.scrollHeight;

      if (scrollTop + viewportHeight >= fullHeight - 100) {
        if (!this.loadingMore && !this.allLoaded) {
          this.loadMore();
        }
      }
    }, 500);
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

  ngOnDestroy(): void {
    window.removeEventListener('scroll', this.onScroll);
  }
}
