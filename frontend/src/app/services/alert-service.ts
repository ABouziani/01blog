import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AlertDialogComponent } from '../alert-dialog/alert-dialog';

@Injectable({
  providedIn: 'root'
})
export class AlertService {
  constructor(private dialog: MatDialog) {}

  showAlert(type: string, msg: string) {
    this.dialog.open(AlertDialogComponent, {
      data: { title: type, message: msg }
    });
  }
}
