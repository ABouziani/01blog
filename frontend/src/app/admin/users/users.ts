// app/admin/users.component.ts
import { Component, inject } from '@angular/core';
import { User } from '../models/user';
import { UserAdminService } from '../services/user-admin';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [MatTableModule, MatButtonModule, CommonModule, MatDialogModule, RouterLink],
  templateUrl: './users.html',
  styleUrl: './users.css'
})
export class AdminUsersComponent {
  private userAdminService = inject(UserAdminService);
  private dialog = inject(MatDialog);

  users: User[] = [];
  displayedColumns = ['username', 'email', 'since', 'status', 'actions'];

  ngOnInit() {
    this.userAdminService.getUsers().subscribe(users => {
      console.log(users)
      this.users = users;
    });
  }

  openConfirmDialog(action: 'ban' | 'activate' | 'delete', userId: string, username: string) {
    const message = action === 'ban'
      ? `Ban user "${username}"? They will lose access.`
      : action === 'activate' ? `Activate user "${username}"? They will gain access again.` :`Permanently delete user "${username}"? This cannot be undone.`;

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: { message, action, userId, username }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        if (action === 'ban') {
          this.userAdminService.banUser(userId).subscribe(() => {
            const user = this.users.find(u => u.id === userId);
            if (user) user.isBanned = true;
          });
        } else if (action === 'activate') {
          this.userAdminService.activateUser(userId).subscribe(() => {
            const user = this.users.find(u => u.id === userId);
            if (user) user.isBanned = false;
          });
        } else if (action === 'delete') {
          this.userAdminService.deleteUser(userId).subscribe(() => {
            this.users = this.users.filter(u => u.id !== userId);
          });
        }
      }
    });
  }
}