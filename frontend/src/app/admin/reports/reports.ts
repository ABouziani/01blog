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


interface GroupedUserReport {
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
      ? `Ban the reported user "${report.reportedUserId}"?`
      : action === 'delete' ? `Are you sure you want to delete this report ?` : `Dismiss this report without action?`;

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: { message, action, reportId: report.reportedUserId }
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.reportAdminService.resolveUserReport(report.reportedUserId.toString(), action).subscribe(() => {
            this.groupedUserReports = this.groupedUserReports.filter(r => r.reportedUserId !== report.reportedUserId);
          // if (action === 'delete') {
          //   this.userReports = this.userReports.filter(r => r.id !== report.id);
          // } else if (action === 'ban_user') {
          //   const existing = this.userReports.find(r => r.id === report.id);
          //   if (existing) {
          //     existing.status = 'resolved';
          //   }
          //   alert("User banned successfully");
          // } else {
          //   const existing = this.userReports.find(r => r.id === report.id);
          //   if (existing) {
          //     existing.status = 'resolved';
          //   }
          // }
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
        this.reportAdminService.resolvePostReport(report.postId.toString(), action).subscribe(() => {
          alert("Report resolved successfully!")
          this.groupedPostReports = this.groupedPostReports.filter(r => r.postId !== report.postId);
          // if (action === 'delete_post') {
          //   this.postReports = this.postReports.filter(r => r.id !== report.id);
          // } else if (action === 'hide_post') {
          //   const existing = this.postReports.find(r => r.id === report.id);
          //   if (existing) {
          //     existing.status = 'resolved';
          //   }
          //   alert("Post hidded successfully");
          // } else if (action === 'delete_report') {
          //   this.postReports = this.postReports.filter(r => r.id !== report.id);
          // } else {
          //   const existing = this.postReports.find(r => r.id === report.id);
          //   if (existing) {
          //     existing.status = 'resolved';
          //   }
          // }
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
          reportedUserId:report.reportedUserId,
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