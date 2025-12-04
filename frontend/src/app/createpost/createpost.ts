// create-post/create-post.component.ts

import { Component, EventEmitter, inject, Input, input, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http'; // Remove HttpClientModule import from here
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Post } from '../post/post-model';
import { AlertService } from '../services/alert-service';

@Component({
  selector: 'app-create-post',
  templateUrl: './createpost.html',
  imports: [ReactiveFormsModule, CommonModule], // FormsModule not needed for reactive forms
  styleUrls: ['./createpost.css']
})
export class CreatePostComponent {
  postForm: FormGroup;
  isSubmitting = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  noPhoto = false;

  selectedFile: File | null = null;
  previewUrl: string | null = null; // For image/video preview

  @Input() posttoedit: Post | null = null;


  @Input() editing: {
    id: number;
    title: String;
    description: String;
    media: String;
  } | null = null;


  @Output() cancelEvent = new EventEmitter<string>();
  @Output() updateEvent = new EventEmitter<Post>();

  cancelEdit() {
    this.editing = null;
    this.cancelEvent.emit('cancel');
  }

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    public router: Router,
    private myAlert: AlertService
  ) {
    this.postForm = this.fb.group({
      title: [this.editing?.title || '', [Validators.required, Validators.minLength(6), Validators.maxLength(100)]],
      description: ['', [Validators.required, Validators.minLength(20), Validators.maxLength(1000)]],
      // media: [null, Validators.required]
    });
  }

  ngOnChanges() {
    if (this.editing) {
      this.postForm.patchValue({
        title: this.editing.title,
        description: this.editing.description,
        media: this.editing.media,
      });
    }
  }

  onFileSelected(event: Event): void {
    this.previewUrl = null;
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

    this.http.post('http://localhost:8080/api/posts/create', formData, { headers })
      .subscribe({
        next: (response) => {
          this.successMessage = 'Post created successfully!';
          this.noPhoto = false;
          setTimeout(() => {
            this.router.navigate(['/']);
          }, 2000);
        },
        error: (error) => {
          this.isSubmitting = false;
          if (error.status == 400) {
            this.errorMessage = error.error;
            return;
          }
          this.errorMessage = this.getErrorMessage(error);
          console.error('Error creating post:', error);
        }
      });
  }

  updatePost(): void {
    this.isSubmitting = true;
    this.errorMessage = null;
    this.successMessage = null;

    const formData = new FormData();
    formData.append('title', this.postForm.get('title')?.value);
    formData.append('description', this.postForm.get('description')?.value);
    formData.append('noPhoto', this.noPhoto ? "true" : "false");
    if (this.selectedFile) {
      formData.append('media', this.selectedFile); // 'media' should match your backend field name
    }

    const token = localStorage.getItem('access_token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
      // ⚠️ DO NOT set Content-Type! Let the browser set it with boundary
    });

    this.http.put<Post>(`http://localhost:8080/api/posts/edit/${this.editing?.id}`, formData, { headers })
      .subscribe({
        next: (data) => {
          this.myAlert.showAlert("Success", "Post updated successfully!");
          this.isSubmitting = false;
          this.updateEvent.emit(data);
        },
        error: (error) => {
          this.isSubmitting = false;
          if (error.status == 400) {
          this.myAlert.showAlert("Error", error.error);
          } else {
            this.errorMessage = this.getErrorMessage(error);
          }
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

  // Cleanup preview URL on destroy (optional but good practice)
  ngOnDestroy(): void {
    if (this.previewUrl) {
      URL.revokeObjectURL(this.previewUrl);
    }
  }

  // Helper methods
  get title() { return this.postForm.get('title'); }
  get description() { return this.postForm.get('description'); }
  // Add these getters
  get isImage(): boolean {
    return this.selectedFile !== null && this.selectedFile.type.startsWith('image/');
  }

  get isVideo(): boolean {
    return this.selectedFile !== null && this.selectedFile.type.startsWith('video/');
  }

  removePhoto() {
    this.noPhoto = true;
    if (this.editing) this.editing.media = '';
  }

  isMediaPhoto(url: string): boolean {
    return /\.(jpe?g|png|gif|webp)$/i.test(url);
  }

  isMediaVideo(url: string): boolean {
    return /\.(mp4|webm|ogg|mov|avi|mkv)$/i.test(url);
  }
}