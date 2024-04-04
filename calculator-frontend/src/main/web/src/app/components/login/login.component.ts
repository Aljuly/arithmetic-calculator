import { Component, Inject, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { NGXLogger } from 'ngx-logger';

import { AlertService, AuthenticationService } from '../../services/index';

@Component({
    moduleId: module.id.toString(),
    templateUrl: 'login.component.html',
    providers: [NGXLogger],
    styleUrls: ['./login.component.scss']
})

export class LoginComponent implements OnInit {
    loading = false;
    returnUrl: String = '';
    loginObject: any;
    userNotFound = false;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private alertService: AlertService,
        private authenticationService: AuthenticationService,
        private logger: NGXLogger,
        private dialogRef: MatDialogRef<LoginComponent>,
        private dialog: MatDialog,
        @Inject(MAT_DIALOG_DATA) private data: any) {}

    ngOnInit() {
        // reset login status
        this.logger.debug('LoginComponent: reset login status (logging out)');
        this.authenticationService.logout();

        // get return url from route parameters or default to '/'
        this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
        this.logger.debug(`LoginComponent: memorize return url: ${this.returnUrl}`);
    }

    login() {
        this.loading = true;
        this.logger.trace('LoginComponent: login() creds: ', this.loginObject);
        this.authenticationService.login(this.loginObject)
            .subscribe((authentication: any) => {
                    this.logger.debug('LoginComponent: login-ing successful, got tokens: ', authentication);
                    this.logger.debug('LoginComponent: navigating to: ', this.returnUrl);
                    this.router.navigate([this.returnUrl])
                        .then((isNavigated: Boolean) => {
                            if (isNavigated) {
                                this.logger.debug('LoginComponent: navigated. Bye!');
                            } else {
                                this.logger.warn('LoginComponent: cannot navigate to ', this.returnUrl);
                            }
                        }).catch( error => this.logger.error('LoginComponent: cannot navigate, error', error));
                    this.dialogRef.close();
                },
                (error: any)=> {
                    this.logger.warn('LoginComponent: login-ing failed! creds, error: ',
                        this.loginObject, error);
                    let msg = error;
                    if (typeof error !== 'string') {
                        const e = <HttpErrorResponse> error;
                        msg = e.message;
                    }
                    this.alertService.error(msg);
                    this.userNotFound = true;
                    // this.onUserNotFound();
                    this.loading = false;
                });
    }

    registerNewUser() {
        const EMAIL_REGEXP = /^(?=.{1,254}$)(?=.{1,64}@)[-!#$%&'*+/0-9=?A-Z^_`a-z{|}~]+(\.[-!#$%&'*+/0-9=?A-Z^_`a-z{|}~]+)*@[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?(\.[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*$/;
        let registrationData: any;

        if (this.loginObject.loginType === 'password') {
            if (EMAIL_REGEXP.test(this.loginObject.login)) {
                registrationData = {email: this.loginObject.login};
            } else {
                registrationData = {username: this.loginObject.login};
            }
        } else {
            registrationData = {
                email: this.loginObject.email || '',
                avatar: this.loginObject.image || '',
                displayName: this.loginObject.name || ''};
        }
        this.router.navigate(['/register', registrationData]);
        this.dialogRef.close();
    }

    onSubmit() {
        this.login();
    }

    receiveSignin($event: any) {
        this.loginObject = $event;
    }

    onEscape() {
        this.logger.trace('LoginComponent: onEscape()');
        this.dialogRef.close();
    }
}

