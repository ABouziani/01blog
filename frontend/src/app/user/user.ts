import { Component, Input } from '@angular/core';
import { UserDto } from '../models/userDto';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { PostsComponent } from '../posts/posts';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../services/auth';

@Component({
  selector: 'app-user',
  imports: [CommonModule, PostsComponent],
  templateUrl: './user.html',
  styleUrl: './user.css'
})
export class UserComponent {
  user: UserDto | null = null;
  error: string | null = null;

  constructor(private http: HttpClient, private route: ActivatedRoute, private auth:AuthService,public router:Router) { }

  ngOnInit(): void {
    let userId = this.route.snapshot.paramMap.get('id');
    if (userId == null) userId = "0";
    this.fetchUser(userId);
  }

  fetchUser(id: String | null): void {
    this.http.get<UserDto>(`http://localhost:8080/api/users/${id}`)
      .subscribe({
        next: (data) => {
          console.log(data);
          data.posts.forEach(post => {
            post.creatorId = data.id.toString();
            post.creatorUsername = data.username;
          });
          this.user = data;
          if (data.id == this.auth.loggedId()) {
            this.user.owner = true;
          }
        },
        error: (err) => {
          console.error('Error fetching user:', err);
          this.error = 'Failed to load user data.';
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
          console.log(data);
          alert(data.message)
          if (this.user) this.user.alreadyFollowed = true;
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
          console.log(data);
          alert(data.message)
          if (this.user) this.user.alreadyFollowed = false;
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

  reportUser() {

    const token = localStorage.getItem('access_token'); // or however you store your JWT
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    const body = {
      reportedId: this.user?.id,
      reason: "test report"
    };

    return this.http.post('http://localhost:8080/api/reports/add', body, { headers })
    .subscribe({
        next: (data) => {
          console.log(data)
          alert("User reported to admins successfully!")
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
}
