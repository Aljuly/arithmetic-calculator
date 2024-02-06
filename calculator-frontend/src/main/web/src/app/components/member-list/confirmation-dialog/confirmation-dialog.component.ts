import {Component, Inject, OnInit} from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { NGXLogger } from 'ngx-logger';

@Component({
  selector: 'app-confirmation-dialog',
  templateUrl: './confirmation-dialog.component.html',
  styleUrls: ['./confirmation-dialog.component.scss']
})
export class ConfirmationDialogComponent {

  constructor(public dialogRef: MatDialogRef<ConfirmationDialogComponent>,
              private logger: NGXLogger,
              @Inject(MAT_DIALOG_DATA) public data: any) {
  }

  onEscape(): void {
      this.logger.trace('ConfirmationDialogComponent: onEscape()');
      this.dialogRef.close();
  }
}
