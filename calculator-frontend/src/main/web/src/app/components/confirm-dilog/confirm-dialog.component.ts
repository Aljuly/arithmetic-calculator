import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { NGXLogger } from 'ngx-logger';

@Component({
    selector: 'app-confirm-dialog',
    templateUrl: './confirm-dialog.component.html',
    styleUrls: ['./confirm-dialog.component.scss']
})
export class ConfirmDialogComponent {

    constructor(public dialogRef: MatDialogRef<ConfirmDialogComponent>,
                private logger: NGXLogger,
                @Inject(MAT_DIALOG_DATA) public data: any) {
    }

    onEscape(): void {
        this.logger.trace('ConfirmDialogComponent: onEscape()');
        this.dialogRef.close();
    }

    onSubmit(): void {
        this.logger.trace('ConfirmDialogComponent: onSubmit()');
        this.dialogRef.close(true);
    }
}
