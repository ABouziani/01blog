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
import { AlertService } from '../../services/alert-service';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [MatTableModule, MatButtonModule, CommonModule, MatDialogModule, RouterLink],
  templateUrl: './users.html',
  styleUrls: ['./users.css', '../adminpanel/adminpanel.css']
})
export class AdminUsersComponent {
  private userAdminService = inject(UserAdminService);
  private dialog = inject(MatDialog);
  private myAlert = inject(AlertService);

  users: User[] = [];
  displayedColumns = ['username', 'email', 'since', 'status', 'actions'];

  ngOnInit() {
    this.userAdminService.getUsers().subscribe(users => {
      console.log(users)
      this.users = users;
    });
  }

  openConfirmDialog(action: 'ban' | 'activate' | 'delete' | 'make-admin' | 'remove-admin', userId: string, username: string) {
    const messages = {
      'ban': `Ban user "${username}"? They will lose access.`,
      'activate': `Activate user "${username}"? They will gain access again.`,
      'make-admin': `Make user "${username}" an Admin? They will gain full admin privileges.`,
      'delete': `Permanently delete user "${username}"? This cannot be undone.`,
      'remove-admin': `Remove admin privilages from "${username}"? They will lose admin privileges.`
    };

    const message = messages[action] || `Unknown action for user "${username}".`;


    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: { message, action, userId, username }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        if (action === 'ban') {
          this.userAdminService.banUser(userId).subscribe({
            next: () => {
              const user = this.users.find(u => u.id === userId);
              if (user) user.isBanned = true;

              this.myAlert.showAlert("Success", "User banned successfully!");
            },
            error: (err) => {
              console.error("Error banning user:", err);
              this.myAlert.showAlert("Error", "Failed to ban user. Please try again.");
            }
          });
        } else if (action === 'activate') {
          this.userAdminService.activateUser(userId).subscribe({
            next: () => {
              const user = this.users.find(u => u.id === userId);
              if (user) user.isBanned = false;

              this.myAlert.showAlert("Success", "User activated successfully!");
            },
            error: (err) => {
              console.error("Error activating user:", err);
              this.myAlert.showAlert("Error", "Failed to activate user. Please try again.");
            }
          });

        } else if (action === 'delete') {
          this.userAdminService.deleteUser(userId).subscribe({
            next: () => {
              this.users = this.users.filter(u => u.id !== userId);

              this.myAlert.showAlert("Success", "User deleted successfully!");
            },
            error: (err) => {
              console.error("Error deleting user:", err);
              this.myAlert.showAlert("Error", "Failed to delete user. Please try again.");
            }
          });
        }
        // else if (action === 'make-admin') {
        //   this.userAdminService.makeAdmin(userId).subscribe(() => {
        //     const user = this.users.find(u => u.id === userId);
        //     if (user) user.isAdmin = true;
        //   });
        // } else if (action === 'remove-admin') {
        //   this.userAdminService.removeAdmin(userId).subscribe(() => {
        //     const user = this.users.find(u => u.id === userId);
        //     if (user) user.isAdmin = false;
        //   });
        // }
      }
    });
  }
}