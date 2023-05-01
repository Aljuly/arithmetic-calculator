import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { NGXLogger } from 'ngx-logger';

import { JwtHelperService } from '@auth0/angular-jwt';

import config from '../config/index';
import { User } from '../model/User';
import { LocalStorageService } from './local-storage.service';
import { Role } from '../model/Role';

@Injectable()
export class AuthenticationService {
    private readonly oauthClientUsername = 'microlms360';
    private readonly oauthClientPassword = 'microlms360';
    private readonly authorizationHeader;

    constructor(private http: HttpClient,
                private localStorageService: LocalStorageService,
                private logger: NGXLogger) { 
        this.authorizationHeader = `Basic ${btoa(this.oauthClientUsername + ':' + this.oauthClientPassword)}`;
    }

    /**
     *
     * @param {any} loginObject
     * @returns {Observable<any>} authentication token pak
     */
    login(loginObject: any) {
        this.logger.debug('AuthenticationService: login(); loginObgect: ', loginObject);
        const body: FormData = new FormData();
        body.append('scope', 'webclient');
        Object.keys(loginObject).forEach(key => body.append(key, loginObject[key]));
        const httpOptions = {
            headers: new HttpHeaders({
                'Authorization': this.authorizationHeader
            })
        };

        this.logger.debug('AuthenticationService: HTTP POST to url: ', config.endpoint.auth.issueNewToken);
        return this.http.post<any>(config.endpoint.auth.issueNewToken, body, httpOptions)
            .pipe(map(payload => {
                this.logger.debug('AuthenticationService: received payload: ', payload);

                // login successful if there's a jwt token in the response
                if (payload && payload.access_token) {
                    // store user details and jwt token in local storage to keep user logged in between page refreshes
                    const currentUser = new User(payload);
                    this.localStorageService.saveCurrentUser(currentUser);
                    console.log('Was saved next user', currentUser);
                    this.logger.info('AuthenticationService: user logged-in successfully');
                } else {
                    this.logger.warn('AuthenticationService: invalid format, no access token in the payload!');
                }

                return payload;
            }));
    }

    logout() {
        // remove user from local storage to log user out
        this.localStorageService.removeCurrentUser();
        this.logger.info('AuthenticationService: user logged-out successfully');
    }

    isLoggedIn(): boolean {
        this.logger.trace('AuthenticationService: isLoggedIn()');
        /*Unfortunately we cannot use this coz it desn't use internally the `tokenGetter` function to obtain
        jwt-token, but in a mean time we use nonstandard (from @angular2-jwt point of view) token place (the scheme,
        we store it deeply (currentUser._tokens.access_token) inside currentUser key in localStorage.
        return tokenNotExpired();*/

        const jwtHelper = new JwtHelperService();
        const user = this.localStorageService.readCurrentUser();
        if (!user) {
            this.logger.trace('AuthenticationService: logged-in user not found');
            return false;
        }
        const token = user.tokens.access_token;
        if (!token) {
            this.logger.error('AuthenticationService: access_token not found');
            return false;
        }
        // return !jwtHelper.isTokenExpired(token);
        if (jwtHelper.isTokenExpired(token, 1)) {
            this.logger.warn('AuthenticationService: access_token expired');
            return false;
        } else {
            this.logger.trace('AuthenticationService: isLoggedIn() : true(success,user is found)');
            return true;
        }
    }

    /**
     * Determines whether current loggedin user has one of specified roles.
     *
     * @param {string} requiredRoles
     * @returns {boolean} false if user is anonymous, true if logged-in user has required role
     */
    hasRole(requiredRoles: Role[]): boolean {
        this.logger.trace('AuthenticationService: checking whether current user has role ', requiredRoles);
        if (!this.isLoggedIn()) {
            this.logger.trace('AuthenticationService: current user is not loggedin, anon have no permissions!');
            return false;
        }
        const currentUser = this.localStorageService.readCurrentUser();

        let has = false;
        currentUser.userRoles.forEach((curRole: { name: any; }) => {
            requiredRoles.forEach(expRole => {
                // Here we have check what Role info stored in Tocken and add 'Role.id'
                if (expRole.description === curRole.name) {
                    has = true;
                }
            });
        });

        if (has) {
            this.logger.warn('AuthenticationService: success, current user is in role ', requiredRoles);
        } else {
            this.logger.warn('AuthenticationService: error, current user is NOT in required role. All roles: '
                , currentUser.userRoles);
        }
        return has;
    }


    get currentUser(): User {
        return this.localStorageService.readCurrentUser();
    }
}