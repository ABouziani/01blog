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

@Component({
  selector: 'app-admin-posts',
  standalone: true,
  imports: [
    MatTableModule,
    MatButtonModule,
    CommonModule,
    MatDialogModule
  ],
  templateUrl: './posts.html',
  styleUrl: './posts.css'
})
export class AdminPostsComponent {
  private postAdminService = inject(PostAdminService);
  private dialog = inject(MatDialog);

  posts: Post[] = [];
  displayedColumns = ['author', 'preview', 'status', 'actions'];

  ngOnInit() {
    this.postAdminService.getPosts().subscribe(posts => {
      this.posts = posts;
    });
  }

  openConfirmDialog(action: 'hide' | 'delete', postId: string, authorName: string) {
    const message = action === 'hide'
      ? `Hide post by "${authorName}"? It will no longer appear publicly.`
      : `Permanently delete this post by "${authorName}"? This cannot be undone.`;

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
       data: { message, action, postId, authorName }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        if (action === 'hide') {
          this.postAdminService.hidePost(postId).subscribe(() => {
            const post = this.posts.find(p => p.id === postId);
            if (post) post.isHidden = true;
          });
        } else if (action === 'delete') {
          this.postAdminService.deletePost(postId).subscribe(() => {
            this.posts = this.posts.filter(p => p.id !== postId);
          });
        }
      }
    });
  }
}