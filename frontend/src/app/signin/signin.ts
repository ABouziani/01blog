// src/app/signin/signin.component.ts
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-signin',
  imports: [FormsModule, CommonModule],
  templateUrl: './signin.html',
  styleUrls: ['./signin.css']
})
export class SigninComponent {
  credentials = { identifier: '', password: '' };
  loading = false;
  error = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  onSubmit() {
    if (!this.credentials.identifier || !this.credentials.password) return;

    this.loading = true;
    this.error = '';

    this.authService.login(this.credentials.identifier, this.credentials.password)
      .subscribe({
        next: () => {
          this.router.navigate(['/']);
        },
        error: (err) => {
          if (err.status == 403) {
            this.error = 'You are banned!';
          } else {
            this.error = 'Invalid username or password';
          }
          this.loading = false;
        }
      });
  }
}