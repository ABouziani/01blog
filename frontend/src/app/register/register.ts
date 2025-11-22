import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './register.html',
  styleUrl: './register.css'
})

export class RegisterComponent {
  registerForm: FormGroup;
  submitted = false;
  isSubmitting = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;


  selectedFile: File | null = null;
  previewUrl: string | null = null;

  constructor(private fb: FormBuilder, private http: HttpClient, public router: Router) {
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3),Validators.pattern(/^[a-zA-Z0-9_]+$/)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      passverif: ['', [Validators.required, Validators.minLength(6)]],
      bio: [''],
      photo: ['']
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];

      // Optional: Validate file type
      const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'video/mp4', 'video/webm', 'video/ogg'];
      if (!allowedTypes.includes(file.type)) {
        this.errorMessage = 'Only images (JPEG, PNG, GIF) are allowed.';
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
    this.submitted = true;
    if (this.registerForm.get('password')?.value != this.registerForm.get('passverif')?.value) {
      this.errorMessage = "passwords doesn't match";
      return
    }
    if (this.registerForm.invalid) {
      this.errorMessage = "Please fix the errors above.";
      return;
    }



    const formData = new FormData();
    formData.append('username', this.registerForm.get('username')?.value);
    formData.append('email', this.registerForm.get('email')?.value);
    formData.append('password', this.registerForm.get('password')?.value);
    formData.append('passverif', this.registerForm.get('passverif')?.value);
    formData.append('bio', this.registerForm.get('bio')?.value);
    if (this.selectedFile) {
      formData.append('photo', this.selectedFile); // 'media' should match your backend field name
    }

    const token = localStorage.getItem('access_token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
      // ⚠️ DO NOT set Content-Type! Let the browser set it with boundary
    });

    this.isSubmitting = true;
    this.errorMessage = null;
    this.successMessage = null;
    this.http.post('http://localhost:8080/api/users/register', formData).subscribe({
      next: (response) => {
        this.successMessage = 'Registration successful!';
        this.errorMessage = '';
        this.registerForm.reset();
        this.submitted = false;
        setTimeout(() => {
          this.router.navigate(['/signin']);
        }, 1500);
      },
      error: (error) => {
        if (error.status == 409) {
          this.errorMessage = error.error;
        } else {
          this.errorMessage = 'Registration failed. Please try again.';
        }
        this.isSubmitting = false;
        this.successMessage = null;
        console.error(error);
      }
    });
  }

  get f() {
    return this.registerForm.controls;
  }

  get username() { return this.registerForm.get('username'); }
  get email() { return this.registerForm.get('email'); }
  get password() { return this.registerForm.get('password'); }
  get passverif() { return this.registerForm.get('passverif'); }
  get bio() { return this.registerForm.get('bio'); }
}
