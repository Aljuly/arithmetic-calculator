import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { PageEvent, MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import { NGXLogger } from 'ngx-logger';
import { User } from '../../model/User';
import { UserService } from 'src/app/services/user.service';
import { MemberFormDialogComponent } from './member-form-dialog/member-form-dialog.component';

@Component({
    selector: 'app-member-list',
    templateUrl: 'member-list.component.html',
    styleUrls: ['member-list.component.scss']
})
export class MemberListComponent implements OnInit, AfterViewInit {

    loaded = false;
    users!: User[];

    // Use MatTableDataSource for paginatin and filtering
    dataSource: MatTableDataSource<User> = new MatTableDataSource<User>();

    @ViewChild(MatPaginator)
    paginator!: MatPaginator;

    constructor(private service: UserService,
                private logger: NGXLogger,
                public dialog: MatDialog,
                public snackBar: MatSnackBar) {
    }

    ngOnInit(): void {
        this.logger.trace('MemberListComponent: ngOnInit()');
        this.updateUserList();
    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
    }

    getValue(event: Event): string {
      return (event.target as HTMLInputElement).value;
    }

    applyFilter(filterValue: string) {
        filterValue = filterValue.trim(); // Remove whitespace
        filterValue = filterValue.toLowerCase(); // MatTableDataSource defaults to lowercase matches

        // Use MatTableDataSource for paginatin and filtering
        this.dataSource.filter = filterValue;
        this.users = this.dataSource._pageData(this.dataSource.filteredData);
    }

    pageEvent() {
        // Use MatTableDataSource for paginatin and filtering
        this.users = this.dataSource._pageData(this.dataSource.filteredData);
    }

    public updateUserList() {
        this.logger.trace('MemberListComponent: updateUserList()');
        this.service.getAll()
            .subscribe(
                (list) => {
                    this.logger.trace('MemberListComponent: received users ', list);

                    // Use MatTableDataSource for paginatin and filtering
                    this.dataSource.data = list.slice();
                    this.users = this.dataSource._pageData(this.dataSource.filteredData);
                    this.loaded = true;
                }
        );
    }

    onAddUser() {
        this.logger.trace('MemberListComponent: onAddUser()');
        const dialogRef = this.dialog.open(MemberFormDialogComponent, {
            width: '95%',
            height: '80%',
            panelClass: ['no-padding-dialog'], // delete padding in this dialog https://material.angular.io/guide/customizing-component-styles
            disableClose: true,
            data: {isNewUser: true}
        });
        dialogRef.afterClosed().subscribe(user => {
            if (user) {
                this.updateUserList();
                this.logger.trace('MemberListComponent: onAddUser(). User was added', user);
                this.snackBar.open(`The user "${user.username}" was added`, '', {
                    duration: 5000
                });
            }
        });
    }
}

