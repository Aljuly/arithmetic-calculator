import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NgForm, FormControl } from '@angular/forms';

import { NGXLogger } from 'ngx-logger';

import { User } from '../../../model/User';
import { UserService } from '../../../services/user.service';
import { RoleService } from '../../../services/role.service';
// import { mentorRole, adminRole } from '../../../helpers/fake-data/fake_roles';

@Component({
  selector: 'app-user-form-dialog',
  templateUrl: './user-form-dialog.component.html',
  styleUrls: ['./user-form-dialog.component.scss'],
})
export class UserFormDialogComponent implements OnInit {
  isNewUser = true;
  currentUser: User;
  allRoles = [];
  selectedRoles = new FormControl();
  selectedSex: string;

  constructor(
    public dialogRef: MatDialogRef<UserFormDialogComponent>,
    private logger: NGXLogger,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private userService: UserService,
    private roleService: RoleService,
    public snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.logger.trace('UserFormDialogComponent: ngOnInit()');
    this.roleService.getAll().subscribe((roles) => {
      this.allRoles = roles;
      if (!this.data.isNewUser) {
        this.isNewUser = this.data.isNewUser;
        this.currentUser = { ...this.data.user };

        /* Because in TypeScript object "new Role(id, name)" and object "new Role(id, name)" don't equals each other.
                we must pass to the selectedRole only those roles as objects which saved in allRoles and have the same role.id as user roles
                If you know better solution, try it */
        for (let i = 0; i < this.currentUser.userRoles.length; i++) {
          const filteredRoles = this.allRoles.filter(
            (role) => role.name === this.currentUser.userRoles[i].name
          );
          if (filteredRoles) {
            this.currentUser.userRoles[i] = filteredRoles[0];
          }
        }

        this.selectedRoles = new FormControl(this.currentUser.userRoles);
        // if (this.currentUser.gender) {
        //   this.selectedSex = this.currentUser.gender;
        // }
      }
    });
  }

  onEscape() {
    this.logger.trace('UserFormDialogComponent: onEscape()');
    this.dialogRef.close();
  }

  onSubmit(form: NgForm) {
    // create new user and fill his fields
    const user = User.fromLocaleStorage(form.value);
    // fill password field which was saved in passwordGroup
    user.password = form.value.passwordGroup.password;
    // fill email field which was saved in emailGroup
    user.email = form.value.emailGroup.email;
    // fill userRoles fields
    user.userRoles = this.selectedRoles.value.slice();

    // save user gender status
    // if (!this.selectedSex) {
    //   this.currentUser.gender = null;
    // } else if (this.selectedSex === 'male') {
    //   this.currentUser.gender = 'male';
    // } else {
    //   this.currentUser.gender = 'female';
    // }
    if (this.isNewUser) {
      this.logger.trace('Add new user', form.value.username, form.value.email);
      this.userService.create(user).subscribe(
        (response) => {
          if (response.status === 201) {
            this.dialogRef.close(response.body);
          } else {
            this.snackBar.open(`User wasn't crated!`, 'Ok');
          }
        },
        (error) => {
          this.logger.warn(
            'UserFormDialog: onSubmit(), cannot create user. Erorr: ',
            error
          );
          this.snackBar.open(
            `Something wrong with connection, please try later!`,
            'Ok'
          );
        }
      );
    } else {
      this.logger.trace(
        'Update user',
        form.value.id,
        form.value.username,
        form.value.email
      );
      this.copyValuesIfChanged(user, this.currentUser);
      this.userService.update(this.currentUser).subscribe(
        (response) => {
          if (response.status === 200 || response.status === 201) {
            this.dialogRef.close(response.body);
          } else {
            this.snackBar.open(`User wasn't updated!`, 'Ok');
          }
        },
        (error) => {
          this.logger.warn(
            'UserFormDialog: onSubmit(), cannot update user. Erorr: ',
            error
          );
          this.snackBar.open(
            `Something wrong with connection, please try later!`,
            'Ok'
          );
        }
      );
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
}
