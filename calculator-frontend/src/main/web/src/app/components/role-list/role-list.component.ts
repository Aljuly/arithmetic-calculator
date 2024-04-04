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
import { JsonConvert } from 'json2typescript';

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
    private jsonConvert: JsonConvert = new JsonConvert();
    @ViewChild(MatSort) sort: MatSort;

    constructor(private service: RoleService,
                private logger: NGXLogger,
                private dialog: MatDialog,
                public snackBar: MatSnackBar) {
    }

    ngOnInit() {
        this.logger.trace('RoleListComponent: ngOnInit()');
        this.updateRoles();
        this.dataSource.sort = this.sort;
    }

    onEditRole(role: Role) {
        let isNewRecord: boolean = false;
        if (!role) {
            role = new Role();
            isNewRecord = true;
        }
        let roleData: string = this.jsonConvert.serialize(role, Role);
        const dialogRef = this.dialog.open(RoleFormDialogComponent, {
            disableClose: true,
            minWidth: '25%',
            panelClass: ['no-padding-dialog'], // delete padding in this dialog https://material.angular.io/guide/customizing-component-styles
            data: {
                role: roleData,
                isNewRole: isNewRecord
            }
        });
        dialogRef.afterClosed().subscribe(updatedRole => {
            if (updatedRole) {
                this.logger.trace('RoleFormDialogComponent: onEditRole(). Role was updated', updatedRole);
                this.updateRoles();
                this.snackBar.open(`The role "${updatedRole.name}" was updated`, null, {
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
                this.service.delete(role.name).subscribe((response: { status: number; }) => {
                    if (response.status === 204) { // TODO : eliminate violation of Separation principle
                        this.logger.trace('RoleListComponent: onDeleteRole(). Role was deleted', role);
                        this.updateRoles();
                        this.snackBar.open(`Role '${role.description}' was deleted!`, null, {
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
                    // Use MatTableDataSource for paginatin and filtering
                    this.dataSource.data = list.slice();
                    this.roles = this.dataSource._pageData(this.dataSource.filteredData);
                    this.loaded = true;
                }
            );
    }
}

