import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators, FormBuilder, AbstractControl } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

import { NGXLogger } from 'ngx-logger';

import { User } from '../../../model/User';
import { RoleService } from '../../../services/role.service';
import { UserService } from '../../../services/user.service';
import { uniqueEmailValidator } from '../../../validators/unique-email-validator.directive';
import { uniqueUsernameValidator } from 'src/app/validators/unique-username-validtor.directive';
import { Role } from 'src/app/model/Role';
import { JsonConvert } from 'json2typescript';

@Component({
    selector: 'app-member-form-dialog',
    templateUrl: './member-form-dialog.component.html',
    styleUrls: ['./member-form-dialog.component.scss']
})
export class MemberFormDialogComponent implements OnInit {

    reactiveForm?: FormGroup;
    isNewUser = true;
    currentUser?: User;
    allRoles?: Role[];
    selectedRoles?: FormControl;
    isAvatarExist = false;
    fileAvatar: File | undefined;
    isAvatarLoading = false;

    private jsonConvert: JsonConvert = new JsonConvert();

    constructor(private dialogRef: MatDialogRef<MemberFormDialogComponent>,
        @Inject(MAT_DIALOG_DATA) private data: any,
        private logger: NGXLogger,
        private fb: FormBuilder,
        private userService: UserService,
        private roleService: RoleService,
        private snackBar: MatSnackBar,
        private dialog: MatDialog) { }

    ngOnInit() {
        this.logger.trace('MemberFormDialogComponent: ngOnInit()');
        if (!this.data.isNewUser) {
            this.isNewUser = this.data.isNewUser;
            // copy data field from data
            const ret = this.jsonConvert.deserialize({ ...this.data.user }, User);
            this.currentUser = Array.isArray(ret) ? ret[0] : ret;
            this.isAvatarExist = this.currentUser!.avatar.length > 0;
        }
        this.createForm();
        this.roleService.getAll().subscribe(roles => {
            this.allRoles = roles;
            if (!this.data.isNewUser) {

                /* Because in TypeScript object "new Role(id, name)" and object "new Role(id, name)" don't equal each other.
                we must pass to the selectedRole only those roles as objects which saved in allRoles and have the same role.id as user roles
                If you know better solution, try it */
                for (let i = 0; i < this.currentUser!.userRoles.length; i++) {
                    const filteredRoles = this.allRoles.filter(role => role.name === this.currentUser!.userRoles[i].name);
                    if (filteredRoles) {
                        this.currentUser!.userRoles[i] = filteredRoles[0];
                    }
                }
                this.reactiveForm!.setControl('roles', new FormControl(this.currentUser!.userRoles));
            } else {
                const roleUser = roles.filter(role => role.name === 'user')[0];
                this.reactiveForm?.setControl('roles', new FormControl([roleUser]));
            }
        });
        // Check the detailed reference in the chapter "JsonConvert class properties and methods"
        this.jsonConvert.ignorePrimitiveChecks = false; // don't allow assigning number to string etc.
    }
    onEscape() {
        this.logger.trace('MemberFormDialogComponent: onEscape()');
        this.dialogRef.close();
    }

    /**
     * Focus on the field with specific id, when event has occurred and prevent
     * event default behaviour
     *
     */
    toAnotherField(event: KeyboardEvent, fieldID: string) {
        event.preventDefault();
        document.getElementById(fieldID)!.focus();
    }

    // Because Material doesn't support input type=file,
    // we need use this crutch
    clickOnInputFile(event: MouseEvent) {
        this.logger.trace('MemberFormDialogComponent: clickOnInputFile()');
        event.preventDefault();
        document.getElementById('fileInput')!.click();
    }

    onFileInput(event: { target: { files: File[]; }; }) {
        this.logger.trace('MemberFormDialogComponent: onFileInput()');
        this.addFile(event.target.files[0]);
    }

    dropHandler(event: DragEvent) {
        this.logger.trace('MemberFormDialogComponent: dropHandler()');
        event.preventDefault();
        this.addFile(event.dataTransfer!.files[0]);
    }

    dragOverHandler(event: DragEvent) {
        this.logger.trace('MemberFormDialogComponent: dragOverHandler()');
        event.preventDefault();
    }

    private addFile(file: File) {
        this.avatar?.setErrors(null);
        if (file) {
            const bytes = 1048576; 
            if (file.size > bytes) {
                this.avatar?.setErrors({ 'invalidFile': 'Please upload a picture smaller than 1 MB.' });
                return;
            } else if (file.type !== 'image/jpeg' && file.type !== 'image/png' && file.type !== 'image/gif') {
                this.avatar?.setErrors({ 'invalidFile': 'We only support PNG, GIF, or JPG pictures.' });
                return;
            }
            this.fileAvatar = file;
            // view file after adding
            this.isAvatarLoading = true;
            this.isAvatarExist = true;
            const fileReader = new FileReader();
            fileReader.onload = (event) => {
                this.isAvatarLoading = false;
                document.getElementById('avatar')!.setAttribute('src', <string>fileReader.result);
            };
            fileReader.readAsDataURL(file);
        }
    }

    onSubmit() {
        this.logger.trace('MemberFormDialogComponent: onSubmit()');
        const user: User = User.fromUserDto(this.reactiveForm?.value);
        user.userRoles = this.roles?.value.slice();
        if (this.isNewUser) {
            this.logger.trace('Add new user', this.login?.value, this.email?.value);
            this.userService.create(user).subscribe(response => {
                if (response.status === 201) {
                    if (this.fileAvatar) {
                        this.userService.loadFile(response.body?.id!, 'avatar', this.fileAvatar).subscribe();
                    }
                    this.dialogRef.close(response.body);
                } else {
                    this.snackBar.open(`User wasn't crated!`, 'Ok');
                }
            }, error => {
                this.logger.warn('UserFormDialog: onSubmit(), cannot create user. Erorr: ', error);
                this.snackBar.open(`Something wrong with connection, please try later!`, 'Ok');
            });
        } else {
            this.logger.trace('Update user', user.id, user.login, user.email);
            this.copyValuesIfChanged(user, this.currentUser!);
            this.userService.update(this.currentUser!).subscribe(response => {
                if (response.status === 200 || response.status === 201) {
                    if (this.fileAvatar) {
                        this.userService.loadFile(response.body?.id!, 'avatar', this.fileAvatar).subscribe();
                    }
                    this.dialogRef.close(response.body);
                } else {
                    this.snackBar.open(`User wasn't updated!`, 'Ok');
                }
            },
                error => {
                    this.logger.warn('UserFormDialog: onSubmit(), cannot update user. Erorr: ', error);
                    this.snackBar.open(`Something wrong with connection, please try later!`, 'Ok');
                }
            );
        }
    }

    private createForm() {
        this.reactiveForm = new FormGroup({
            login: new FormControl(this.currentUser?.login, [Validators.required], 
                uniqueUsernameValidator(this.userService, this.isNewUser ? '' : this.currentUser?.login)
            ),
            firstName: new FormControl(this.currentUser?.firstName, [Validators.required]),
            lastName: new FormControl(this.currentUser?.lastName, [Validators.required]),
            email: new FormControl(this.currentUser?.email, [Validators.required, Validators.email],
                uniqueEmailValidator(this.userService, this.isNewUser ? '' : this.currentUser?.email)
            ),
            avatar: new FormControl(this.currentUser?.avatar),
            password: new FormControl(this.currentUser?.password, [Validators.required, Validators.minLength(6)]),
            roles: new FormControl(this.currentUser?.userRoles),
            enabled: new FormControl(this.currentUser?.enabled),
            verified: new FormControl(this.currentUser?.verified),
            banned: new FormControl(this.currentUser?.banned),
            banReason: new FormControl(this.currentUser?.banReason)
        });
    }

    private getFormErrors(form: AbstractControl) {
        if (form instanceof FormControl) {
            // Return FormControl errors or null
            return form.errors ?? null;
        }
        if (form instanceof FormGroup) {
            const groupErrors = form.errors;
            // Form group can contain errors itself, in that case add'em
            const formErrors = groupErrors ? {groupErrors} : {};
            Object.keys(form.controls).forEach(key => {
                // Recursive call of the FormGroup fields
                const error = this.getFormErrors(form.get(key));
                if (error !== null) {
                    // Only add error if not null
                    formErrors[key] = error;
                }
            });
            // Return FormGroup errors or null
            return Object.keys(formErrors).length > 0 ? formErrors : null;
        }
    }

    /**
     *
     * Copy values from user to another user if they are different
     *
     * @param fromUser user from which values are copying
     *
     * @param toUser user in which values are copying
     */
    private copyValuesIfChanged(fromUser: User, toUser: User) {
        if (fromUser.login && fromUser.login !== toUser.login) {
            toUser.login = fromUser.login;
        }
        if (fromUser.password && fromUser.password !== toUser.password) {
            toUser.password = fromUser.password;
        }
        if (fromUser.email && fromUser.email !== toUser.email) {
            toUser.email = fromUser.email;
        }
        if (fromUser.firstName && fromUser.firstName !== toUser.firstName) {
            toUser.firstName = fromUser.firstName;
        }
        if (fromUser.lastName && fromUser.lastName !== toUser.lastName) {
            toUser.lastName = fromUser.lastName;
        }
        if (fromUser.userRoles) {
            toUser.userRoles = fromUser.userRoles.slice();
        }

    }

    get login() {
        return this.reactiveForm?.get('login');
    }

    get firstName() {
        return this.reactiveForm?.get('firstName');
    }

    get lastName() {
        return this.reactiveForm?.get('lastName');
    }

    get email() {
        return this.reactiveForm?.get('email');
    }

    get password() {
        return this.reactiveForm?.get('password');
    }

    get avatar() {
        return this.reactiveForm?.get('avatar');
    }

    get roles() {
        return this.reactiveForm?.get('roles');
    }

    get enabled() {
        return this.reactiveForm?.get('enabled');
    }

    get verified() {
        return this.reactiveForm?.get('verified');
    }

    get banned() {
        return this.reactiveForm?.get('banned');
    }

    get banReason() {
        return this.reactiveForm?.get('banReason');
    }

    get isValid(): boolean {
        return this.getFormErrors(this.reactiveForm) === null;
    }

}

