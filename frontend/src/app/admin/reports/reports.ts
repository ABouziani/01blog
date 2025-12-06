// app/admin/reports.component.ts
import { Component, inject } from '@angular/core';
import { PostReport, Report } from '../models/report';
import { ReportAdminService } from '../services/report-admin';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog';
import { MatDialogModule } from '@angular/material/dialog';
import { RouterLink } from '@angular/router';
import { AlertService } from '../../services/alert-service';


interface GroupedUserReport {
  id: number;
  reportedUserId: number;
  reportedUserUsername: string;
  reporters: {
    id: number;
    username: string;
    reason: string;
  }[];
  status: string;
}

interface GroupedPostReport {
  id: number;
  postId: number;
  postTitle: string;

  reporters: {
    id: number;
    username: string;
    reason: string;
  }[];
  status: string;
}


@Component({
  selector: 'app-admin-reports',
  standalone: true,
  imports: [
    MatTableModule,
    MatButtonModule,
    CommonModule,
    MatDialogModule,
    RouterLink,
  ],
  templateUrl: './reports.html',
  styleUrls: ['./reports.css', '../adminpanel/adminpanel.css']
})
export class AdminReportsComponent {
  private reportAdminService = inject(ReportAdminService);
  private dialog = inject(MatDialog);
  private myAlert = inject(AlertService);

  showPRPopup = false
  showURPopup = false

  userReports: Report[] = [];
  postReports: PostReport[] = [];
  groupedPostReports: GroupedPostReport[] = [];
  selectedPostReport: GroupedPostReport | null = null;
  groupedUserReports: GroupedUserReport[] = [];
  selectedUserReport: GroupedUserReport | null = null;
  displayedColumns = ['reporter', 'reported', 'reason', 'status', 'actions'];

  ngOnInit() {
    this.reportAdminService.getReports().subscribe(data => {
      // console.log(data);
      this.groupedUserReports = this.groupReportsByUser(data.userReports);
      this.groupedPostReports = this.groupReportsByPost(data.postReports);
    });
  }

  resolveUserReportWithAction(report: GroupedUserReport, action: 'ban_user' | 'dismiss' | 'delete') {
    const message = action === 'ban_user'
      ? `Ban the reported user "${report.reportedUserUsername}"?`
      : action === 'delete' ? `Are you sure you want to delete this report ?` : `Dismiss this report without action?`;

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: { message, action, reportId: report.reportedUserId }
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.reportAdminService.resolveUserReport(report.reportedUserId.toString(), report.id.toString(), action).subscribe({
          next: () => {
            this.myAlert.showAlert("Success", "Report resolved successfully!");
            this.groupedUserReports = this.groupedUserReports.filter(
              r => r.reportedUserId !== report.reportedUserId
            );
          },
          error: (err) => {
            console.error("Error resolving report:", err);
            this.myAlert.showAlert("Error", err.error);
          }
        });
      }
    });
  }

  resolvePostReportWithAction(report: GroupedPostReport, action: 'delete_post' | 'hide_post' | 'dismiss' | 'delete_report') {
    const messages = {
      'delete_post': `Are you sure you want to delete this post?`,
      'hide_post': `Are you sure you want to hide this post?`,
      'dismiss': `Dismiss this report without action?`,
      'delete_report': `Permanently delete this report? This cannot be undone.`,
    };

    const message = messages[action] || `Unknown action!.`;
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: { message, action, reportId: report.postId }
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.reportAdminService.resolvePostReport(report.postId.toString(), report.id.toString(), action).subscribe({
          next: () => {
            this.myAlert.showAlert("Success", "Report resolved successfully!");
            this.groupedPostReports = this.groupedPostReports.filter(
              r => r.postId !== report.postId
            );
          },
          error: (err) => {
            console.error("Error resolving post report:", err);
            this.myAlert.showAlert("Error", err.error);
          }
        });
      }
    });
  }

  groupReportsByUser(reports: any[]): GroupedUserReport[] {
    const map = new Map<number, GroupedUserReport>();

    reports.forEach(report => {
      const userId = report.reportedUserId;

      if (!map.has(userId)) {
        map.set(userId, {
          id: report.id,
          reportedUserId: report.reportedUserId,
          reportedUserUsername: report.reportedUserUsername,
          reporters: [],
          status: "pending"
        });
      }

      const entry = map.get(userId);

      entry!.reporters.push({
        id: report.reporterId,
        username: report.reporterUsername,
        reason: report.reason
      });
    });

    return Array.from(map.values());
  }

  groupReportsByPost(reports: any[]): GroupedPostReport[] {
    const map = new Map<number, GroupedPostReport>();

    reports.forEach(report => {
      const postId = report.reportedPost.id;

      if (!map.has(postId)) {
        map.set(postId, {
          id: report.id,
          postId,
          postTitle: report.reportedPost.title,
          reporters: [],
          status: "pending"
        });
      }

      const entry = map.get(postId);

      entry!.reporters.push({
        id: report.reporterId,
        username: report.reporterUsername,
        reason: report.reason
      });
    });

    return Array.from(map.values());
  }


  computeStatus(statuses: string[]) {
    return statuses.includes('pending')
      ? 'pending'
      : statuses.includes('resolved')
        ? 'resolved'
        : 'dismissed';
  }

  isLast(item: any, array: any[]) {
    return array[array.length - 1] === item;
  }

  openPRPopup() {
    this.showPRPopup = true;
  }

  closePRPopup() {
    this.showPRPopup = false;
    this.selectedPostReport = null;
  }

  openURPopup() {
    this.showURPopup = true;
  }

  closeURPopup() {
    this.showURPopup = false;
    this.selectedUserReport = null;
  }
}