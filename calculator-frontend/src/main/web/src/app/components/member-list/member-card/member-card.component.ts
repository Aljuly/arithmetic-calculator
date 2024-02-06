import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';

import { NGXLogger } from 'ngx-logger';

import { User } from '../../../model/User';
import { ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog.component';
import { MemberFormDialogComponent } from '../member-form-dialog/member-form-dialog.component';
import { UserService } from 'src/app/services/user.service';

@Component({
    selector: 'app-member-card',
    templateUrl: './member-card.component.html',
    styleUrls: ['./member-card.component.scss']
})
export class MemberCardComponent implements OnInit {

    @Input() user!: User;
    @Output() updateUsers = new EventEmitter();

    avatar = '/assets/img/unknown-person.png';

    constructor(private logger: NGXLogger,
        private service: UserService,
        public dialog: MatDialog,
        public router: Router,
        public snackBar: MatSnackBar
    ) { }

    ngOnInit() {
        /*
        if (this.user.avatar && this.user.avatar.length > 0) {
            this.avatar = this.user.avatar;
        }
        */

    }

    onViewUser(user: User) {
        this.logger.trace('MemberCardComponent: onViewUser()');
        this.router.navigate(['users', user.login]);
    }

    stopPropogationEvent(event: Event) {
        event.stopPropagation();
    }

    onEditUser(user: User) {
        this.logger.trace('MemberCardComponent: onEditUser()');
        const dialogRef = this.dialog.open(MemberFormDialogComponent, {
            width: '95%',
            height: '80%',
            panelClass: ['no-padding-dialog'], // delete padding in this dialog https://material.angular.io/guide/customizing-component-styles
            disableClose: true,
            data: {
                isNewUser: false,
                user: user
            }
        });
        dialogRef.afterClosed().subscribe(updatedUser => {
            if (updatedUser) {
                this.logger.trace('MemberCardComponent: onEditUser(). User was updated', updatedUser);
                this.updateUsers.emit();
                this.snackBar.open(`The user "${updatedUser.username}" was updated`, '', {
                    duration: 5000
                });
            }
        });
    }

    onDeleteUser(user: User) {
        this.logger.trace('MemberCardComponent: onDeleteUser()');
        const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
            width: '22rem',
            panelClass: ['no-padding-dialog'], // delete padding in this dialog https://material.angular.io/guide/customizing-component-styles
            data: { username: user.login }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result === true) {
                this.service.delete(user.id).subscribe(response => {
                    if (response.status === 204) { // TODO : eliminate violation of Separation principle
                        this.logger.trace('MemberCardComponent: onDeleteUser(). User was deleted', user);
                        this.updateUsers.emit();
                        this.snackBar.open(`User "${user.login}" was deleted!`, '', {
                            duration: 5000,
                        });
                    }
                }
                );
            }
        });
    }

}
