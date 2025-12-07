import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Post } from './post-model';
import { PostService } from '../services/post-service';
import { CommonModule } from '@angular/common';
import { catchError, finalize, of, tap } from 'rxjs';
import { AddCommentComponent } from '../addcomment/addcomment';
import { CommentDto } from '../models/comment';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { CreatePostComponent } from '../createpost/createpost';
import { AlertService } from '../services/alert-service';
import { SearchBarComponent } from '../search/search';
import { AuthService } from '../services/auth';
import { ConfirmDialogComponent } from '../admin/confirm-dialog/confirm-dialog';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-post',
  imports: [ReactiveFormsModule, CommonModule, FormsModule, AddCommentComponent, CreatePostComponent, SearchBarComponent],
  templateUrl: './post.html',
  styleUrl: './post.css'
})

export class PostComponent {
  private dialog = inject(MatDialog);
  private apiUrlPostComment = 'http://localhost:8080/api/comments/getpostcomments/'
  postId: string | null = "";
  post: Post | null = null;
  loading = true;
  isEditing = false;
  loadingMore = false;
  allLoaded = false;
  lastCommentId: number = 0;
  pageSize: number = 10;
  private scrollTimer: any;
  showPopup = false;
  reason = '';
  reporterror = '';

  //forediting
  postForm: FormGroup;
  isSubmitting = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  selectedFile: File | null = null;
  previewUrl: string | null = null; // For image/video preview

  constructor(private fb: FormBuilder, private http: HttpClient,
    public postService: PostService, private route: ActivatedRoute,
    public router: Router, private cd: ChangeDetectorRef,
    private myAlert: AlertService, public authService: AuthService) {
    this.postForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      description: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(1000)]],
    });
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.postId = params.get('id');
      if (this.postId) {
        this.postService.getPost(this.postId).subscribe({
          next: (data) => {
            console.log("Post: ", data);
            data.comments = []
            this.post = data;
            if (this.post) {
              this.post.likes = data.reactions.filter(r => r.type == "like").length.toString();
              this.post.dislikes = data.reactions.filter(r => r.type == "dislike").length.toString();
              this.postForm.patchValue({
                title: this.post.title,
                description: this.post.description
              });
            }
            this.loadComments();
            this.loading = false;
          },
          error: (err) => {
            console.log(err);

            this.errorMessage = err.error;
            this.loading = false;
          }
        });
      }
    });

  }


  cancelEdit(event: string) {
    if (event == 'cancel') this.isEditing = false;
    this.loadComments();
  }

  addReaction(postId: number, type: 'LIKE' | 'DISLIKE') {
    if (this.postService.waiting) return;
    this.postService.addReaction(postId, type).pipe(
      tap(response => {
        // ✅ Optimistically update the UI
        if (this.post) {
          this.post.likes = response.likeCount;
          this.post.dislikes = response.dislikeCount;
        }
      }),
      catchError(error => {
        console.error('Reaction error:', error.error);
        // Optional: show error message
        if (error.status == 400) {
          this.myAlert.showAlert("Error", error.error);
        }
        // this.myAlert.showAlert("Error", "Failed to react. Please try again.");
        return of(null);
      }),
      finalize(() => {
        // Re-enable button
        this.postService.waiting = false;
      })
    )
      .subscribe();
  }

  onCommentAdded(data: CommentDto) {
    data.owner = true;
    this.post?.comments.unshift(data);
  }

  isImage(url: string): boolean {
    return /\.(jpe?g|png|gif|webp)$/i.test(url);
  }

  isVideo(url: string): boolean {
    return /\.(mp4|webm|ogg|mov|avi|mkv)$/i.test(url);
  }

  delete(postId: number) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '300px',
      data: {
        title: 'Delete Comment',
        message: 'Are you sure you want to delete this comment?'
      }
    });
    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.postService.deletePost(this.postId).subscribe({
          next: (data) => {
            console.log(data);
            this.myAlert.showAlert("Success", "Post deleted successfully!");
            this.router.navigate(['/']);
          },
          error: (err) => {
            this.myAlert.showAlert("Error", "Error deleting this posts!");
            console.log(err);
          }
        });
      }
    });
  }

  cancelEditing() {
    this.isEditing = false;
    this.postForm.patchValue({
      title: this.post?.title,
      description: this.post?.description
    });
    this.loadComments();
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];

      // Optional: Validate file type
      const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'video/mp4', 'video/webm', 'video/ogg'];
      if (!allowedTypes.includes(file.type)) {
        this.errorMessage = 'Only images (JPEG, PNG, GIF) and videos (MP4, WebM, Ogg) are allowed.';
        this.selectedFile = null;
        this.previewUrl = null;
        return;
      }

      // Optional: Validate file size (e.g., max 10MB)
      const maxSize = 50 * 1024 * 1024; // 10MB
      if (file.size > maxSize) {
        this.errorMessage = 'File size must be less than 50MB.';
        this.selectedFile = null;
        this.previewUrl = null;
        return;
      }

      this.selectedFile = file;
      this.errorMessage = null;

      // Generate preview
      const reader = new FileReader();
      reader.onload = (e) => {
        this.previewUrl = e.target?.result as string;
      };
      reader.readAsDataURL(file);
    }
  }


  // startEditing() {
  //   this.isEditing = true;
  //   this.cd.detectChanges(); // 👈 force Angular to render textarea
  //   this.postForm.patchValue({
  //     title: this.post?.title,
  //     description: this.post?.description
  //   });
  // }

  onSubmit(): void {
    if (this.postForm.invalid) {
      this.markFormGroupTouched();
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = null;
    this.successMessage = null;

    const formData = new FormData();
    formData.append('title', this.postForm.get('title')?.value);
    formData.append('description', this.postForm.get('description')?.value);
    if (this.selectedFile) {
      formData.append('media', this.selectedFile); // 'media' should match your backend field name
    }

    const token = localStorage.getItem('access_token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
      // ⚠️ DO NOT set Content-Type! Let the browser set it with boundary
    });

    this.http.put<Post>(`http://localhost:8080/api/posts/edit/${this.post?.id}`, formData, { headers })
      .subscribe({
        next: (data) => {
          this.isEditing = false;
          this.myAlert.showAlert("Success", "Post updated successfully!");
          // this.successMessage = 'Post updated successfully!';
          this.post = data;
          if (this.post) {
            this.post.likes = data.reactions.filter(r => r.type == "like").length.toString();
            this.post.dislikes = data.reactions.filter(r => r.type == "dislike").length.toString();
          }
          this.post.comments.reverse();
          this.isSubmitting = false;
          this.loading = false;
        },
        error: (error) => {
          this.isSubmitting = false;
          this.errorMessage = this.getErrorMessage(error);
          console.error('Error editing post:', error);
        }
      });
  }

  private markFormGroupTouched(): void {
    Object.keys(this.postForm.controls).forEach(key => {
      const control = this.postForm.get(key);
      control?.markAsTouched();
    });
  }

  private getErrorMessage(error: any): string {
    if (error.status === 400) {
      return 'Invalid post data. Please check your input.';
    } else if (error.status === 401) {
      return 'Unauthorized. Please log in.';
    } else if (error.status === 500) {
      return 'Server error. Please try again later.';
    }
    return 'An error occurred while creating the post.';
  }

  onUpdate(data: Post) {
    this.lastCommentId = 0;
    this.allLoaded = false;
    this.loadingMore = false;
    data.comments = [];
    this.post = data;
    if (this.post) {
      this.post.likes = data.reactions.filter(r => r.type == "like").length.toString();
      this.post.dislikes = data.reactions.filter(r => r.type == "dislike").length.toString();
    }
    // this.post.comments.reverse();
    this.isSubmitting = false;
    this.loading = false;
    this.isEditing = false;
    this.loadComments();

  }


  deleteComment(commentId: number) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '300px',
      data: {
        title: 'Delete Comment',
        message: 'Are you sure you want to delete this comment?'
      }
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        const token = localStorage.getItem('access_token');
        if (!token) {
          this.myAlert.showAlert("Error", "You must be logged in to delete a comment!");
          return;
        }

        const headers = new HttpHeaders({
          'Authorization': `Bearer ${token}`
        });

        this.http.delete<{ message: string }>(`http://localhost:8080/api/comments/delete/${commentId}`, { headers })
          .subscribe({
            next: (response) => {
              // ✅ Remove comment from UI
              if (this.post && this.post.comments) {
                this.post.comments = this.post.comments.filter(c => c.id !== commentId);
              }
              this.myAlert.showAlert("Success", "Comment deleted successfully!");
            },
            error: (error) => {
              console.error('Error deleting comment:', error);
              if (error.status === 403) {
                this.myAlert.showAlert("Error", "You are not authorized to delete this comment!");
              } else if (error.status === 404) {
                this.myAlert.showAlert("Error", "Comment not found!");
              } else if (error.status === 400) {
                this.myAlert.showAlert("Error", error.error);
              } else {
                this.myAlert.showAlert("Error", "An error occurred while deleting the comment!");
              }
            }
          });
      }
    });

  }


  loadComments() {
    console.log("last ID", this.lastCommentId)
    if (this.loadingMore || this.allLoaded) return;
    this.loadingMore = true;

    setTimeout(() => {
      let params = new HttpParams().set('pageSize', this.pageSize.toString());
      params = params.set('lastId', this.lastCommentId.toString());

      this.http.get<CommentDto[]>(this.apiUrlPostComment + this.postId, { params }).subscribe({
        next: (data) => {
          if (this.post) {
            // data.reverse();
            this.post.comments = [...this.post.comments, ...data];
            const oldestComment = this.post.comments.reduce((min, p) => p.id < min.id ? p : min, this.post.comments[0]);
            if (oldestComment) this.lastCommentId = oldestComment.id;
          }
          this.loading = false;
          if (data.length < this.pageSize) {
            this.allLoaded = true;
            this.loadingMore = false;
            return;
          }
        },
        error: (err) => {
          console.error('Error fetching comments:', err);
          this.loading = false;
        },
        complete: () => {
          this.loadingMore = false;
        }
      });
    }, 1000);
  }

  onScroll(event: Event) {
    clearTimeout(this.scrollTimer);

    this.scrollTimer = setTimeout(() => {
      const element = event.target as HTMLElement;

      const scrollTop = element.scrollTop;
      const visibleHeight = element.clientHeight;
      const fullHeight = element.scrollHeight;

      if (scrollTop + visibleHeight >= fullHeight - 50) {
        if (!this.loadingMore && !this.allLoaded) {
          this.loadComments();
        }
      }
    }, 500);
  }


  // ngOnDestroy(): void {
  //   window.removeEventListener('scroll', this.onScroll);
  // }

  openPopup() {
    this.showPopup = true;
  }

  closePopup() {
    this.showPopup = false;
    this.reason = '';
  }

  submitReport() {
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
      }, 2500);
      return;
    }

    const token = localStorage.getItem('access_token'); // or however you store your JWT
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    const body = {
      reportedPostId: this.postId,
      reason: this.reason
    };

    return this.http.post('http://localhost:8080/api/reports/addpostreport', body)
      .subscribe({
        next: (data) => {
          console.log(data)
          this.closePopup();
          this.myAlert.showAlert("Success", "Post reported to admins successfully!");
          // if (this.user) this.user.alreadyReported = true;
        },
        error: (err) => {
          console.error('Error reporting this post:', err);
          this.reporterror = err.error;
          setTimeout(() => {
            this.reporterror = '';
          }, 2500);
          setTimeout(() => {
            // this.error = '';
          }, 1500);
        }
      });

  }
}
