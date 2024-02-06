import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';

import { NGXLogger } from 'ngx-logger';

import { User } from '../../model/User';
import { UserService } from 'src/app/services/user.service';
import { ConfirmationDialogComponent } from './confirmation-dialog/confirmation-dialog.component';
//import { UserFormDialogComponent } from './user-form-dialog/user-form-dialog.component';
import { MemberFormDialogComponent } from '../member-list/member-form-dialog/member-form-dialog.component';
import { JsonConvert } from 'json2typescript';

@Component({
    selector: 'app-user-list',
    templateUrl: 'user-list.component.html',
    styleUrls: ['user-list.component.scss']
})
export class UserListComponent implements OnInit, AfterViewInit {
    users: User[];
    displayedColumns = ['position', 'avatar', 'title', 'firstName', 'lastName', 'email', 'userRoles', 'enabled', 'verified', 'banned', 'lastLogin', 'menuAction'];
    dataSource: MatTableDataSource<User> = new MatTableDataSource<User>();
    private jsonConvert: JsonConvert = new JsonConvert();
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;

    constructor(private service: UserService,
                private logger: NGXLogger,
                public dialog: MatDialog,
                public snackBar: MatSnackBar) {
        this.users = [];
    }

    ngOnInit(): void {
        this.logger.trace('UserListComponent: ngOnInit()');
        this.updateUserList();
    }

    pageEvent() {
        // Use MatTableDataSource for paginatin and filtering
        this.users = this.dataSource._pageData(this.dataSource.filteredData);
    }

    getValue(event: Event): string {
        return (event.target as HTMLInputElement).value;
    }

    private updateUserList() {
        this.logger.info('UserListComponent: updateUserList()');
        this.service.getAll()
            .subscribe(
                (list) => {
                    this.logger.info('UserListComponent: received users ', list);
                    this.users = list.slice();
                    this.dataSource.data = list.slice();
                }
            );
    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    applyFilter(filterValue: string) {
        filterValue = filterValue.trim(); // Remove whitespace
        filterValue = filterValue.toLowerCase(); // MatTableDataSource defaults to lowercase matches
        this.dataSource.filter = filterValue;
    }

    onEditUser(user: User) {
        this.logger.debug('UserListComponent: onEditUser()');
        let userData: string = this.jsonConvert.serialize(user, User);
        const dialogRef = this.dialog.open(MemberFormDialogComponent, {
            width: '50%',
            height: '57%',
            disableClose: true,
            panelClass: ['no-padding-dialog'], // delete padding in this dialog https://material.angular.io/guide/customizing-component-styles
            data: {
                isNewUser: false,
                user: userData
            }
        });
        dialogRef.afterClosed().subscribe(user_ => {
            if (user_) {
                this.updateUserList();
                this.logger.debug('UserListComponent: onEditUser(). User was updated', user_);
                this.snackBar.open(`The user "${user_.login}" was updated`, '', {
                    duration: 5000
                });
            }
        });
    }

    onAddUser() {
        this.logger.trace('UserListComponent: onAddUser()');
        const dialogRef = this.dialog.open(MemberFormDialogComponent, {
            width: '50%',
            height: '57%',
            panelClass: ['no-padding-dialog'], // delete padding in this dialog https://material.angular.io/guide/customizing-component-styles
            disableClose: true,
            data: {isNewUser: true}
        });
        dialogRef.afterClosed().subscribe(user => {
            if (user) {
                this.updateUserList();
                this.logger.trace('UserListComponent: onAddUser(). User was added', user);
                this.snackBar.open(`The user "${user.login}" was added`, '', {
                    duration: 5000
                });
            }
        });
    }

    onDeleteUser(user: User) {
        this.logger.trace('UserListComponent: onDeleteUser()');
        const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
            width: '22rem',
            data: {username: user.login}
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result === true) {
                this.service.delete(user.id).subscribe(response => {
                        if (response.status === 204) { // TODO : eliminate violation of Separation principle
                            this.snackBar.open(`User "${user.login}" was deleted!`, '', {
                                duration: 5000,
                            });
                            this.updateUserList();
                            this.logger.trace('UserListComponent: onDeleteUser(). User was deleted', user);
                        }
                    }
                );
            }
        });
    }

}

