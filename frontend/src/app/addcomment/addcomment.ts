import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommentService } from '../services/comment';
import { finalize } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { AlertService } from '../services/alert-service';

@Component({
  selector: 'app-add-comment',
  imports: [ReactiveFormsModule],
  templateUrl: './addcomment.html',
  styleUrl: './addcomment.css'
})
export class AddCommentComponent {
  @Input({ required: true }) postId!: number;
  @Output() commentPosted = new EventEmitter<any>();

  private fb = inject(FormBuilder);
  private http = inject(HttpClient);
  private myAlert = inject(AlertService);
  isLoading = false;

  commentForm = this.fb.group({
    content: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(500)]]
  });

  onSubmit(): void {
    if (this.commentForm.invalid) return;

    this.isLoading = true;
    const { content } = this.commentForm.value;


    this.http
      .post('http://localhost:8080/api/comments/add', {
        postId: this.postId,
        content: content!.trim()
      })
      .subscribe({
        next: (response) => {
          console.log('Comment added successfully:', response);
          this.commentForm.reset();
          this.isLoading = false;
          this.commentPosted.emit(response);
        },
        error: (err) => {
          console.error('Failed to add comment:', err);
          this.isLoading = false;
          if (err.statucode == 400) {
            this.myAlert.showAlert("Error", "internal server error!");
          } else {
            this.myAlert.showAlert("Error", err.error);
          }
          // Optional: show user-friendly error message
        }
      });

  }
}