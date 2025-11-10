// create-post/create-post.component.ts

import { Component, EventEmitter, inject, Input, input, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http'; // Remove HttpClientModule import from here
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Post } from '../post/post-model';

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

  selectedFile: File | null = null;
  previewUrl: string | null = null; // For image/video preview

  @Input() posttoedit: Post | null = null;
  
  
  @Input() editing: {
    id:number;
    title: String;
    description: String;
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
    public router: Router
  ) {
    this.postForm = this.fb.group({
      title: [this.editing?.title || '', [Validators.required, Validators.minLength(6), Validators.maxLength(100)]],
      description: ['', [Validators.required, Validators.minLength(20)]],
      // media: [null, Validators.required]
    });
  }
  
  ngOnChanges() {
    if (this.editing) {
      this.postForm.patchValue({
        title: this.editing.title,
        description: this.editing.description
      });
    }
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
          this.isSubmitting = false;
          setTimeout(() => {
            this.router.navigate(['/']);
          }, 2000);
        },
        error: (error) => {
          this.isSubmitting = false;
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
          alert("Post updated successfully!")
          this.updateEvent.emit(data);
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
}