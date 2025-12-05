import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserDto } from '../models/userDto';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AlertService } from '../services/alert-service';

@Component({
  selector: 'app-editprofile',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './editprofile.html',
  styleUrl: './editprofile.css'
})
export class EditprofileComponent {
  userForm: FormGroup;
  isSubmitting = false;
  noPhoto = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  selectedFile: File | null = null;
  previewUrl: string | null = null; // For image/video preview

  @Input() editing: {
    id: number;
    username: String;
    bio: String;
    email:string;
    photo:string;
  } | null = null;


  @Output() cancelEvent = new EventEmitter<string>();
  @Output() updateEvent = new EventEmitter<UserDto>();

  cancelEdit() {
    this.editing = null;
    this.noPhoto = false;
    this.cancelEvent.emit('cancel');
  }

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    public router: Router,
    private myAlert: AlertService
  ) {
    this.userForm = this.fb.group({
      bio: ['', [Validators.required, Validators.maxLength(500)]],
      email: ['', [Validators.required, Validators.email]],
      // media: [null, Validators.required]
    });
  }

  ngOnChanges() {
    if (this.editing) {
      this.userForm.patchValue({
        username: this.editing.username,
        bio: this.editing.bio,
        email: this.editing.email,
        photo: this.editing.photo
      });
    }
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];

      // Optional: Validate file type
      // const allowedTypes = ['image/jpeg', 'image/png', 'image/gif'];
      // if (!allowedTypes.includes(file.type)) {
      //   this.errorMessage = 'Only images (JPEG, PNG, GIF) are allowed.';
      //   this.selectedFile = null;
      //   this.previewUrl = null;
      //   return;
      // }

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


  updateProfile(): void {
    this.isSubmitting = true;
    this.errorMessage = null;
    this.successMessage = null;

    const formData = new FormData();
    formData.append('bio', this.userForm.get('bio')?.value);
    formData.append('noPhoto', this.noPhoto ? "true" : "false");
    if (this.userForm.get('email')?.value != this.editing?.email) {
      formData.append('email', this.userForm.get('email')?.value);
    }
    if (this.selectedFile) {
      formData.append('media', this.selectedFile); // 'media' should match your backend field name
    }

    const token = localStorage.getItem('access_token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
      // ⚠️ DO NOT set Content-Type! Let the browser set it with boundary
    });

    this.http.put<UserDto>(`http://localhost:8080/api/users/edit`, formData, { headers })
      .subscribe({
        next: (data) => {
          this.myAlert.showAlert("Success", "User updated successfully!");
          this.noPhoto = false;
          this.updateEvent.emit(data);
        },
        error: (error) => {
          this.isSubmitting = false;
          if (error.status == 400 || error.status == 409){
            this.errorMessage = error.error;
            setTimeout(() => {
              this.errorMessage = '';
            }, 1500);
          } else {
            this.errorMessage = this.getErrorMessage(error);
          }
          console.error('Error editing post:', error);
        }
      });
  }

  private markFormGroupTouched(): void {
    Object.keys(this.userForm.controls).forEach(key => {
      const control = this.userForm.get(key);
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

  removePhoto() {
    this.noPhoto = true;
    if (this.editing) this.editing.photo = '';
  }

  // Helper methods
  get username() { return this.userForm.get('username'); }
  get bio() { return this.userForm.get('bio'); }
  get email() { return this.userForm.get('email'); }
  // Add these getters
  get isImage(): boolean {
    return this.selectedFile !== null && this.selectedFile.type.startsWith('image/');
  }

  get isVideo(): boolean {
    return this.selectedFile !== null && this.selectedFile.type.startsWith('video/');
  }
}
