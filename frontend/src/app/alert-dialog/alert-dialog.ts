import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';


@Component({
  selector: 'app-alert-dialog',
  imports: [MatDialogModule,
    MatButtonModule,CommonModule],
  standalone: true,
  templateUrl: './alert-dialog.html',
  styleUrl: './alert-dialog.css'
})
export class AlertDialogComponent {
constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<AlertDialogComponent>
  ) {}
}
