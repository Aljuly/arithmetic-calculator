import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';

import { NGXLogger } from 'ngx-logger';

import { User } from '../../model/User';
import { UserService } from '../../services/user.service';
import { ConfirmationDialogComponent } from './confirmation-dialog/confirmation-dialog.component';
import { MemberFormDialogComponent } from '../member-list/member-form-dialog/member-form-dialog.component';

@Component({
    selector: 'app-user-list',
    templateUrl: 'user-list.component.html',
    styleUrls: ['user-list.component.scss']
})
export class UserListComponent implements OnInit, OnDestroy, AfterViewInit {
    users: User[];
    displayedColumns = ['position', 'avatar', 'title', 'firstName', 'lastName', 'email', 'userRoles', 'enabled', 'verified', 'banned', 'lastLogin', 'menuAction'];
    pageSizeOptions: number[] = [10, 20, 50];
    dataSource: MatTableDataSource<User> = new MatTableDataSource<User>();
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

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    ngOnDestroy(): void {
    }

    getValue(event: Event): string {
        return (event.target as HTMLInputElement).value;
    }

    onPageChange(event: any) {
        this.logger.info('UserListComponent: onPageChange()', event);
        this.updateUserList();
    }

    private updateUserList() {
        this.logger.info('UserListComponent: updateUserList()');
        const pageIndex = this.paginator?.pageIndex || 0;
        const pageSize = this.paginator?.pageSize || 10;

        this.service.getPaginated(pageIndex, pageSize)
            .subscribe(
                (response) => {
                    this.logger.info('UserListComponent: received paginated users', response);
                    this.users = response.content.slice();
                    this.dataSource.data = response.content.slice();
                    setTimeout(() => {
                        if (this.dataSource.paginator) {
                            this.dataSource.paginator.length = response.totalElements;
                            this.dataSource.paginator.pageIndex = pageIndex;
                            this.dataSource.paginator.pageSize = pageSize;
                        }
                    });
                    this.logger.info('UserListComponent: total records', response.totalElements);
                },
                (error) => {
                    this.logger.error('UserListComponent: error fetching paginated users', error);
                    this.snackBar.open('Failed to fetch user list', '', {
                        duration: 5000,
                    });
                }
            );
    }
    applyFilter(filterValue: string) {
        filterValue = filterValue.trim(); // Remove whitespace
        filterValue = filterValue.toLowerCase(); // MatTableDataSource defaults to lowercase matches
        this.dataSource.filter = filterValue;
    }
    onEditUser(user: User) {
        this.logger.debug('UserListComponent: onEditUser()');
        const dialogRef = this.dialog.open(MemberFormDialogComponent, {
            width: '50%',
            height: '57%',
            disableClose: true,
            panelClass: ['no-padding-dialog'],
            data: {
                isNewUser: false,
                user: user
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
            panelClass: ['no-padding-dialog'],
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
    getUserAvatar(user: User): string {
      const avatarId = user?.avatar?.toString() || 'default';
      // Return the direct URL - let the browser handle loading
      return this.service.getImageUrl(avatarId);
    }
}
