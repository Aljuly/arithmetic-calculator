import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators, FormBuilder } from '@angular/forms';
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
//import { PhoneService } from '../../../services/phone.service';
//import { NoncorrectPhoneConfirmationDialogComponent } from './noncorrect-phone-confirmation-dialog/noncorrect-phone-confirmation-dialog.component';

/**
 * Component for update or create user
 *
 */
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

    //selectedSex: string;

    //countries?: {phoneCode: string, shortName: string, fullName: string, flagImagePozitionY: number}[];

    constructor(private dialogRef: MatDialogRef<MemberFormDialogComponent>,
        @Inject(MAT_DIALOG_DATA) private data: any,
        private logger: NGXLogger,
        private fb: FormBuilder,
        private userService: UserService,
        private roleService: RoleService,
        //private phoneService: PhoneService,
        private snackBar: MatSnackBar,
        private dialog: MatDialog) {}

        ngOnInit() {
            this.logger.trace('MemberFormDialogComponent: ngOnInit()');
            if (!this.data.isNewUser) {
                this.isNewUser = this.data.isNewUser;
                // copy data field from data
                const ret = this.jsonConvert.deserialize({...this.data.user}, User);
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
            /*        if (this.currentUser!.gender) {
                        this.selectedSex = this.currentUser!.gender;
                    } 
            */
                } else {
                    const roleUser = roles.filter(role => role.name === 'user')[0];
                    this.reactiveForm?.setControl('roles', new FormControl([roleUser]));
                }

            });

            //this.countries = this.phoneService.countries;

            // set initial value in field "Enter phone"
            //this.newPhone.setValue(this.selectedCountry.value.phoneCode + ' ');

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
            const bytes = 1048576; // 1048576 bytes = 1 Mb
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
                document.getElementById('avatar')!.setAttribute('src', <string> fileReader.result);
            };
            fileReader.readAsDataURL(file);

        }
    }
/*
    addPhone(event?: KeyboardEvent) {
        this.logger.trace('MemberFormDialogComponent: addPhone()');
        if (event) {
            event.preventDefault();
        }
        if (this.newPhone.value.length > 0 &&
                ((this.newPhone.value.startsWith(this.selectedCountry.value.phoneCode) &&
                    this.newPhone.value.trim().length > this.selectedCountry.value.phoneCode.length)
                    || (!this.newPhone.value.startsWith(this.selectedCountry.value.phoneCode)))) {
            if (this.phoneService.isCorrectNumber(this.selectedCountry.value, this.newPhone.value)) {
                const newPhone = this.phoneService.parsePhone(this.selectedCountry.value, this.newPhone.value);
                this.phones.value.push(newPhone);
                this.newPhone.setValue(this.selectedCountry.value.phoneCode + ' ');

            } else {
                this.openConfirmationDialog(this.newPhone.value);
            }
        } else {
            this.newPhone.markAsTouched();
            this.newPhone.setErrors({empty: 'Enter phone please'});
        }
    }
*/
/*
    private openConfirmationDialog(nonCorrectPhone: string) {
        const confirmDialogRef = this.dialog.open(NoncorrectPhoneConfirmationDialogComponent, {
            width: '400px',
            panelClass: ['no-padding-dialog'], // delete padding in this dialog https://material.angular.io/guide/customizing-component-styles
            data: {phone: nonCorrectPhone}
        });
        confirmDialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.phones.value.push(nonCorrectPhone.toLocaleUpperCase());
                this.newPhone.setValue(this.selectedCountry.value.phoneCode + ' ');
            } else {
                this.newPhone.setValue(nonCorrectPhone);
            }
        });
    }
*/
/*
    removePhone(phone: string) {
        this.logger.trace('MemberFormDialogComponent: removePhone()');
        const updated = this.phones.value.filter(value => value !== phone);
        this.phones.setValue(updated);
    }
*/
    /**
     * Find country by the beginning of the phone number and change flag in country code to flag of the found country
     */
    /*
    findCountryByCode() {

        this.phoneService.countries.forEach(country => {
            if (this.newPhone.value.startsWith(country.phoneCode)) {
                this.selectedCountry.setValue(country);
            }
        });
    }
*/
    onSubmit() {
        this.logger.trace('MemberFormDialogComponent: onSubmit()');
        // create new user and fill his fields
        const _user = this.jsonConvert.deserialize(this.reactiveForm?.value, User);
        const user: User = Array.isArray(_user) ? _user[0] : _user;
        // save user gender status
        /*
        if (!this.selectedSex) {
            user.gender = null;
        } else if (this.selectedSex === 'male') {
            user.gender = 'male';
        } else {
            user.gender = 'female';
        }
        */
        // fill userRoles fields
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
        this.reactiveForm = this.fb.group({
            login: ['', Validators.required, uniqueUsernameValidator(this.userService, this.isNewUser ? '' : this.currentUser?.login)],
            firstName: ['', Validators.required],
            lastName: ['', Validators.required],
            email: [
                '',
                [Validators.required, Validators.email],
                uniqueEmailValidator(this.userService, this.isNewUser ? '' : this.currentUser?.email)
            ],
            avatar: [''],
            password: ['', [Validators.required, Validators.minLength(6)]],
            middleName: [''],         
            roles: [''],
            enabled: [false],
            verified: [false],
            banned: [false],
            banReason: ['', { disabled: true }, Validators.required],
        });

        if (!this.isNewUser) {
            this.login?.setValue(this.currentUser?.login);
            //this.displayName?.setValue(this.currentUser?.displayName);
            this.email?.setValue(this.currentUser?.email);
            this.password?.setValue(this.currentUser?.password);
            this.firstName?.setValue(this.currentUser?.firstName);
            this.lastName?.setValue(this.currentUser?.lastName);
/*
            if (this.currentUser?.birthdate && this.currentUser.birthdate.length > 0) {
                this.birthdate?.setValue(new Date(this.currentUser.birthdate));
            } else {
                this.birthdate.setValue('');
            }
            this.phones.setValue(this.currentUser.phones.slice());
*/
            // roles control is setting when we get all roles
/*
            this.enabled.setValue(this.currentUser.enabled);
            this.verified.setValue(this.currentUser.verified);
            this.banned.setValue(this.currentUser.banned);
            this.banReason.setValue(this.currentUser.banReason);
*/
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
        // if (fromUser.salt && fromUser.salt !== toUser.salt) {
        //     toUser.salt = fromUser.salt;
        // }
        if (fromUser.email && fromUser.email !== toUser.email) {
            toUser.email = fromUser.email;
        }
        if (fromUser.firstName && fromUser.firstName !== toUser.firstName) {
            toUser.firstName = fromUser.firstName;
        }
        if (fromUser.lastName && fromUser.lastName !== toUser.lastName) {
            toUser.lastName = fromUser.lastName;
        }
        // if (fromUser.middleName && fromUser.middleName !== toUser.middleName) {
        //     toUser.middleName = fromUser.middleName;
        // }
        // if (fromUser.displayName && fromUser.displayName !== toUser.displayName) {
        //     toUser.displayName = fromUser.displayName;
        // }
        // if (fromUser.gender && fromUser.gender !== toUser.gender) {
        //     toUser.gender = fromUser.gender;
        // }
        // if (fromUser.birthdate && fromUser.birthdate !== toUser.birthdate) {
        //     toUser.birthdate = fromUser.birthdate;
        // }
        // if (fromUser.phones && fromUser.phones !== toUser.phones) {
        //     toUser.phones = fromUser.phones;
        // }
        // if (fromUser.lastLogin && fromUser.lastLogin !== toUser.lastLogin) {
        //     toUser.lastLogin = fromUser.lastLogin;
        // }
        // if (fromUser.enabled !== undefined && fromUser.enabled !== toUser.enabled) {
        //     toUser.enabled = fromUser.enabled;
        // }
        // if (fromUser.banned !== undefined && fromUser.banned !== toUser.banned) {
        //     toUser.banned = fromUser.banned;
        // }
        // if (fromUser.verified !== undefined && fromUser.verified !== toUser.verified) {
        //     toUser.verified = fromUser.verified;
        // }
        // if (fromUser.banReason && fromUser.banReason !== toUser.banReason) {
        //     toUser.banReason = fromUser.banReason;
        // }
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

    /* get confirmEmail(){
        return this.reactiveForm.get('confirmEmail');
    } */

    get password() {
        return this.reactiveForm?.get('password');
    }

    /* get confirmPassword(){
        return this.reactiveForm.get('confirmPassword');
    } */

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

    // get selectedCountry() {
    //     return this.reactiveForm?.get('selectedCountry');
    // }

}

