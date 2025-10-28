// app/admin/reports.component.ts
import { Component, inject } from '@angular/core';
import { Report } from '../models/report';
import { ReportAdminService } from '../services/report-admin';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog';
import { MatDialogModule } from '@angular/material/dialog';
import { RouterLink } from '@angular/router';

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
  styleUrl: './reports.css'
})
export class AdminReportsComponent {
  private reportAdminService = inject(ReportAdminService);
  private dialog = inject(MatDialog);

  reports: Report[] = [];
  displayedColumns = ['reporter', 'reported', 'reason', 'status', 'actions'];

  ngOnInit() {
    this.reportAdminService.getReports().subscribe(reports => {
      this.reports = reports;
    });
  }

  resolveWithAction(report: Report, action: 'ban_user' | 'dismiss' | 'delete') {
    const message = action === 'ban_user'
      ? `Ban the reported user "${report.reportedUserId}"?`
      : action === 'delete' ? `Are you sure you want to delete this report ?` : `Dismiss this report without action?`;

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: { message, action, reportId: report.id }
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.reportAdminService.resolveReport(report.id, action).subscribe(() => {
          if (action === 'delete') {
            this.reports = this.reports.filter(r => r.id !== report.id);
          } else if (action === 'ban_user') {
            const existing = this.reports.find(r => r.id === report.id);
            if (existing) {
              existing.status = 'resolved';
            }
            alert("User banned successfully");
          } else {
            const existing = this.reports.find(r => r.id === report.id);
            if (existing) {
              existing.status = 'resolved';
            }
          }
        });
      }
    });
  }
}