import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { RoleFormDialogComponent } from '../role-form-dialog/role-form-dialog.component';
import { Role } from '../../model/Role';
import { RoleService } from '../../services/role.service';
import { NGXLogger } from 'ngx-logger';
import { ConfirmDialogComponent } from '../confirm-dilog/confirm-dialog.component';

@Component({
    selector: 'app-roles',
    templateUrl: './role-list.component.html',
    styleUrls: ['./role-list.component.scss']
})
export class RoleListComponent implements OnInit {

    loaded = false;
    roles: Role[];
    dataSource: MatTableDataSource<Role> = new MatTableDataSource<Role>();
    displayedColumns: string[] = ['name', 'description', 'controls'];
    @ViewChild(MatSort) sort: MatSort;

    constructor(private service: RoleService,
                private logger: NGXLogger,
                private dialog: MatDialog,
                public snackBar: MatSnackBar) {
    }

    ngOnInit() {
        this.logger.info('RoleListComponent: ngOnInit()');
        this.updateRoles();
        this.dataSource.sort = this.sort;
    }

    onEditRole(role: Role) {
        let isNewRecord: boolean = false;
        if (!role) {
            role = new Role();
            isNewRecord = true;
        }
        this.logger.info('RoleListComponent: onEditRole(). Role to edit', role);
        //let roleData: string = role.toJson().toString();
        const dialogRef = this.dialog.open(RoleFormDialogComponent, {
            disableClose: true,
            minWidth: '25%',
            panelClass: ['no-padding-dialog'], // delete padding in this dialog https://material.angular.io/guide/customizing-component-styles
            data: {
                role: role,
                isNewRole: isNewRecord
            }
        });
        dialogRef.afterClosed().subscribe(updatedRole => {
            if (updatedRole) {
                this.logger.info('RoleFormDialogComponent: onEditRole(). Role was updated', updatedRole);
                this.updateRoles();
                this.snackBar.open(`The role "${updatedRole.name}" was updated`, undefined, {
                    duration: 5000
                });
            }
        });
    }

    onDeleteRole(role: Role) {
        const dialogRef = this.dialog.open(ConfirmDialogComponent, {
            panelClass: ['no-padding-dialog'], // delete padding in this dialog https://material.angular.io/guide/customizing-component-styles
            disableClose: true,
            data: {
                confirmHeader: 'Delete role',
                confirmText: [`Are you sure to delete the role with name '${role.name}'?`],
                buttonText: 'Delete',
                cancelButtonText: 'Cancel',
                buttonColor: 'warn',
                buttonIcon: 'delete_forever'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result === true) {
                this.service.delete(role.id).subscribe((response: { status: number; }) => {
                    if (response.status === 204) { // TODO : eliminate violation of Separation principle
                        this.logger.debug('RoleListComponent: onDeleteRole(). Role was deleted', role);
                        this.updateRoles();
                        this.snackBar.open(`Role '${role.description}' was deleted!`, undefined, {
                            duration: 5000,
                        });
                    }
                });
            }
        });
    }

    public updateRoles() {
        this.logger.trace('RoleListComponent: updateRoles()');
        this.service.getAll()
            .subscribe(
                (list: Role[]) => {
                    this.logger.trace('RoleListComponent: received roles ', list);
                    this.dataSource.data = list.slice();
                    this.roles = this.dataSource._pageData(this.dataSource.filteredData);
                    this.loaded = true;
                }
            );
    }
}

