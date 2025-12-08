// app/admin/posts.component.ts
import { Component, inject } from '@angular/core';
import { Post } from '../models/post';
import { PostAdminService } from '../services/post-admin';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog';
import { MatDialogModule } from '@angular/material/dialog';
import { AlertService } from '../../services/alert-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-posts',
  standalone: true,
  imports: [
    MatTableModule,
    MatButtonModule,
    CommonModule,
    MatDialogModule,
  ],
  templateUrl: './posts.html',
  styleUrls: ['./posts.css', '../adminpanel/adminpanel.css']
})
export class AdminPostsComponent {
  private postAdminService = inject(PostAdminService);
  private dialog = inject(MatDialog);
  private myAlert = inject(AlertService);
  public router = inject(Router);

  posts: Post[] = [];
  displayedColumns = ['author', 'preview', 'status', 'actions'];

  ngOnInit() {
    this.postAdminService.getPosts().subscribe(posts => {
      
      this.posts = posts;
    });
  }

  openConfirmDialog(action: 'hide' | 'unhide' | 'delete', postId: string, authorName: string) {
    const message = action === 'hide'
      ? `Hide post by "${authorName}"? It will no longer appear publicly.`
      : action === 'unhide' ? `Unhide this post ? It will appear publicly again.` : `Permanently delete this post by "${authorName}"? This cannot be undone.`;

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: { message, action, postId, authorName }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        if (action === 'hide') {
          this.postAdminService.hidePost(postId).subscribe({
            next: () => {
              const post = this.posts.find(p => p.id === postId);
              if (post) post.isHidden = true;
              this.myAlert.showAlert("Success", "Post hidden successfully!");
            },
            error: (err) => {
              console.error(err);
              this.myAlert.showAlert("Error", "Failed to hide the post. Please try again.");
            }
          });
        } else if (action === 'unhide') {
          this.postAdminService.unhidePost(postId).subscribe({
            next: () => {
              const post = this.posts.find(p => p.id === postId);
              if (post) post.isHidden = false;
              this.myAlert.showAlert("Success", "Post unhidden successfully!");
            },
            error: (err) => {
              console.error(err);
              this.myAlert.showAlert("Error", "Failed to unhide the post. Please try again.");
            }
          });
        } else if (action === 'delete') {
          this.postAdminService.deletePost(postId).subscribe({
            next: () => {
              this.posts = this.posts.filter(p => p.id !== postId);
              this.myAlert.showAlert("Success", "Post deleted successfully!");
            },
            error: (err) => {
              console.error(err);
              this.myAlert.showAlert("Error", "Failed to delete the post. Please try again.");
            }
          });

        }
      }
    });
  }
}