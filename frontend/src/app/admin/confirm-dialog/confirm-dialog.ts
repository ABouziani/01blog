// app/admin/confirm-dialog.component.ts
import { Component, Inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [MatDialogModule, MatButtonModule],
  templateUrl: './confirm-dialog.html'
})
export class ConfirmDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ConfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      message: string;
      action: string;
      userId?: string;
      username?: string;
    }
  ) {}

  onYes(): void {
    this.dialogRef.close(true);
  }

  onNo(): void {
    this.dialogRef.close(false);
  }
}