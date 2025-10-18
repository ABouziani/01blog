import { ChangeDetectorRef, Component } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Post } from './post-model';
import { PostService } from '../services/post-service';
import { CommonModule } from '@angular/common';
import { catchError, finalize, of, tap } from 'rxjs';
import { AddCommentComponent } from '../addcomment/addcomment';
import { CommentDto } from '../models/comment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-post',
  imports: [ReactiveFormsModule, CommonModule, AddCommentComponent, RouterLink],
  templateUrl: './post.html',
  styleUrl: './post.css'
})

export class PostComponent {
  postId: string | null = "";
  post: Post | null = null;
  loading = true;
  isEditing = false;

  //forediting
  postForm: FormGroup;
  isSubmitting = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  selectedFile: File | null = null;
  previewUrl: string | null = null; // For image/video preview

  constructor(private fb: FormBuilder, private http: HttpClient, public postService: PostService, private route: ActivatedRoute, public router: Router, private cd: ChangeDetectorRef) {
    this.postForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      description: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(1000)]],
    });
  }

  ngOnInit(): void {
    this.postId = this.route.snapshot.paramMap.get('id');
    this.postService.getPost(this.postId).subscribe({
      next: (data) => {
        console.log(data);
        this.post = data;
        if (this.post) {
          this.post.likes = data.reactions.filter(r => r.type == "like").length.toString();
          this.post.dislikes = data.reactions.filter(r => r.type == "dislike").length.toString();
          this.postForm.patchValue({
            title: this.post.title,
            description: this.post.description
          });
        }
        this.post.comments.reverse();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching posts:', err);
        this.loading = false;
      }
    });
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
        console.error('Reaction error:', error);
        // Optional: show error message
        alert('Failed to react. Please try again.');
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
    this.post?.comments.unshift(data);
  }

  isImage(url: string): boolean {
    return /\.(jpe?g|png|gif|webp)$/i.test(url);
  }

  isVideo(url: string): boolean {
    return /\.(mp4|webm|ogg|mov|avi|mkv)$/i.test(url);
  }

  delete(postId: number) {
    if (confirm("Are you sure you want to delete this post?")) {
      this.postService.deletePost(this.postId).subscribe({
        next: (data) => {
          console.log(data);
          alert(data.message)
          this.router.navigate(['/']);
        },
        error: (err) => {
          alert('Error deleting this posts:');
          console.log(err);
        }
      });
    }
  }

  cancelEditing() {
    this.isEditing = false;
    this.postForm.patchValue({
      title: this.post?.title,
      description: this.post?.description
    });
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
      const maxSize = 10 * 1024 * 1024; // 10MB
      if (file.size > maxSize) {
        this.errorMessage = 'File size must be less than 10MB.';
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
          alert("Post updated successfully!")
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

}
