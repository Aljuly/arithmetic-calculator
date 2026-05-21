import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { NGXLogger } from 'ngx-logger';
import { Role } from '../../model/Role';
import { RoleService } from '../../services/role.service';
import { ALL_OPERATIONS } from '../../helper/fake-data/fake_operations';

@Component({
    selector: 'app-role',
    templateUrl: './role-form-dialog.component.html',
    styleUrls: ['./role-form-dialog.component.scss']
})
export class RoleFormDialogComponent implements OnInit {
    isNewRole = true;
    currentRole: Role;
    reactiveForm: FormGroup;
    allOperations = ALL_OPERATIONS;

    constructor(private log: NGXLogger,
                private roleService: RoleService,
                private formBuilder: FormBuilder,
                private snackBar: MatSnackBar,
                private dialogRef: MatDialogRef<RoleFormDialogComponent>,
                @Inject(MAT_DIALOG_DATA) private data: any) {}

    ngOnInit() {
        this.log.info('RoleFormDialogComponent: ngOnInit()');
        this.log.info('Data In: ' + this.data.isNewRole);
        this.isNewRole = this.data.isNewRole;
        if (!this.isNewRole) {
            // copy data field from data
            // const ret: Role = Role.fromJson(this.data.role);
            this.log.info('RoleFormDialogComponent: ngOnInit(). Role to edit', this.data.role);
            this.currentRole = this.data.role;
        }
        this.log.info('Current Role: ' + JSON.stringify(this.currentRole));
        this.createForm();
    }

    createForm() {
        this.log.trace('RoleFormDialogComponent: createForm()');
        this.reactiveForm = this.formBuilder.group({
            name: [
                {value: null, disabled: !this.isNewRole},
                [Validators.required, Validators.pattern('^ROLE_([A-Z]+_)*[A-Z]+$')]],
            description: ['', [Validators.required, Validators.pattern('([a-zA-Z]+\\s)*[a-zA-Z]+')]],
            operations: this.formBuilder.array([]),
            showOperationName: [false],
            operationFilter: ['']});

        this.allOperations.map(o => this.operations.push(new FormGroup({
            checked: new FormControl(false),
            name: new FormControl(o.name),
            description: new FormControl(o.description)})));
        if (!this.isNewRole) {
            if (this.name) {
                this.name.setValue(this.currentRole.name);
            }
            if (this.currentRole?.description) {
                if (this.description) {
                    this.description.setValue(this.currentRole.description);
                }
            }
            this.operations.value.forEach((o, i) => {
                this.reactiveForm.get(`operations.${i}.checked`)
                    ?.setValue(this.currentRole.operations.includes(o.name));
            });
        }
    }

    onSubmit() {
        if (this.reactiveForm.valid) {
            this.saveRole();
        }
    }

    saveRole() {
        const role: Role = Role.fromRoleDto(this.reactiveForm.value);
        this.log.info('RoleFormDialogComponent: onSubmit(). Data: ' + JSON.stringify(role));
        if (this.isNewRole) {
            this.roleService.create(role.toJson()).subscribe(response => {
                if (response.status === 201) {
                    this.log.trace('RoleFormDialogComponent: onSubmit(). Role was crated.');
                    this.dialogRef.close(response.body);
                } else {
                    this.snackBar.open(`Role wasn't crated!`, 'Ok');
                }
            }, error => {
                this.log.warn('RoleFormDialog: onSubmit(), cannot create role. Erorr: ', error);
                this.snackBar.open(error, 'Ok');
            });
        } else {
            this.log.trace('Update role', role.name, role.description);
            this.copyValuesIfChanged(role, this.currentRole);
            this.roleService.update(this.currentRole.toJson()).subscribe(response => {
                    if (response.status === 200 || response.status === 201) {
                        this.log.trace('RoleFormDialogComponent: onSubmit(). Role was updated');
                        this.dialogRef.close(response.body);
                    } else {
                        this.snackBar.open(`Role wasn't updated!`, 'Ok');
                    }
                },
                error => {
                    this.log.warn('RoleFormDialog: onSubmit(), cannot update role. Erorr: ', error);
                    this.snackBar.open(`Something wrong with connection, please try later!`, 'Ok');
                }
            );
        }
    }

    private arraysContainSameValues(arr1: string[], arr2: string[]): boolean {
        if (arr1.length !== arr2.length) {
            return false;
        }
        const sortedArr1 = [...arr1].sort();
        const sortedArr2 = [...arr2].sort();
        return sortedArr1.every((value, index) => value === sortedArr2[index]);
    }
    private copyValuesIfChanged(fromRole: Role, toRole: Role) {
        this.log.trace('RoleFormDialogComponent: copyValuesIfChanged()');
        if (fromRole.description && fromRole.description !== toRole.description) {
            toRole.description = fromRole.description as string;
        }
        if (!this.arraysContainSameValues(fromRole.operations, toRole.operations)) {
            toRole.operations = fromRole.operations.map(op => op.toString());
        }
    }

    filterOperations(text: string) {
        const filters = this.reactiveForm.value.operationFilter.split(' ');
        return filters.every(f => text.toUpperCase().search(f.toUpperCase()) !== -1);
    }

    operationTitle(value) {
        return ((this.showOperationName?.value ?? false) ? value.name + ': ' : '') + value.description;
    }

    onEscape() {
        this.dialogRef.close();
    }

    get name() {
        return this.reactiveForm.get('name');
    }

    get description() {
        return this.reactiveForm.get('description');
    }

    get showOperationName() {
        return this.reactiveForm.get('showOperationName');
    }

    get filter() {
        return this.reactiveForm.get('filter');
    }

    get operations() {
        return this.reactiveForm.get('operations') as FormArray;
    }

    get isValid(): boolean {
        return this.reactiveForm.valid;
    }
}

