import { Injectable } from '@angular/core';
import { HttpRequest, HttpResponse, HttpHandler, HttpEvent, HttpInterceptor, HTTP_INTERCEPTORS, HttpHeaders } from '@angular/common/http';
import { throwError as observableThrowError,  Observable, of } from 'rxjs';
import { delay, mergeMap, materialize, dematerialize } from 'rxjs/operators';
import { NGXLogger } from 'ngx-logger';

import config from '../config/index';
import { ALL_USERS } from './fake-data/fake_users';
import { Role } from '../model/Role';
import { ALL_ROLES } from './fake-data/fake_roles';
import { User } from '../model/User';

@Injectable()
export class MockBackendInterceptor implements HttpInterceptor {

    private userIdCnt: number;

    constructor(private logger: NGXLogger) {
        this.userIdCnt = 0;
     }

    //adminAccessToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjEwMSIsImxvZ2luIjoiYWRtaW4iLCJwYXNzd29yZCI6IjEyMzQ1NiIsImVtYWlsIjoiYWRtaW5AYWRtaW4iLCJmaXJzdE5hbWUiOiJhZG1pbl9maXJzdCIsImxhc3ROYW1lIjoiYWRtaW5fbGFzdCIsImF2YXRhciI6Ii9hcGkvc3RvcmFnZS9hZG1pbklkLTEucG5nIiwibGFzdGxvZ2luIjoiRnJpIEphbiAwNyAxOTgzIiwiZW5hYmxlZCI6dHJ1ZSwiYmFubmVkIjpmYWxzZSwidmVyaWZpZWQiOnRydWUsImJhblJlYXNvbiI6IiIsInVzZXJSb2xlcyI6W3siaWQiOiJST0xFX0FETUlOIiwibmFtZSI6ImFkbWluIn0seyJpZCI6IlJPTEVfVVNFUiIsIm5hbWUiOiJ1c2VyIn1dfQ.1xty0D99cBdvZCFUX_ZapmqMLqQbz-fJK2Hm3-McO3A';
    adminAccessToken = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7XCJpZFwiOjEwMDAwNSxcImxvZ2luXCI6XCJ0ZXN0VXNlclwiLFwicGFzc3dvcmRcIjpcIlwiLFwiZW1haWxcIjpcInRlc3RAdGVzdC5jb21cIixcImZpcnN0TmFtZVwiOlwiVGVzdFwiLFwibGFzdE5hbWVcIjpcIlRlc3RcIixcImF2YXRhclwiOlwiXCIsXCJsYXN0bG9naW5cIjpcIlwiLFwiZW5hYmxlZFwiOnRydWUsXCJiYW5uZWRcIjpmYWxzZSxcInZlcmlmaWVkXCI6dHJ1ZSxcImJhblJlYXNvblwiOlwiXCIsXCJ1c2VyUm9sZXNcIjpbe1wiaWRcIjoxMDAwMDMsXCJuYW1lXCI6XCJST0xFX0FETUlOXCIsXCJkZXNjcmlwdGlvblwiOlwiU3lzdGVtIEFkbWluaXN0cmF0b3JcIixcIm9wZXJhdGlvbnNcIjpbXCJSRUFEX1BSSVZJTEVHRVwiLFwiV1JJVEVfUFJJVklMRUdFXCIsXCJXUklURV9QUklWSUxFR0VcIixcIkNIQU5HRV9QQVNTV09SRF9QUklWSUxFR0VcIl19XX0iLCJyb2xlIjoiUk9MRV9BRE1JTiIsImV4cCI6MTc0NTg0NTMxMiwiaWF0IjoxNzQ1ODMwOTEyfQ.XGMtnuwbXOHW6nKD3Az3dX85aTJmZoquGvk9QTME0kk';
    userAccessToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6InVzZXJJZCIsInVzZXJuYW1lIjoidXNlciIsInBhc3N3b3JkIjoidXNlciIsInNhbHQiOiJzYWx0IiwiZW1haWwiOiJ1c2VyQHVzZXIiLCJmaXJzdE5hbWUiOiJ1c2VyX2ZpcnN0IiwibGFzdE5hbWUiOiJ1c2VybGFzdCIsIm1pZGRsZU5hbWUiOiJ1c2VyX21pZGRsZSIsImRpc3BsYXlOYW1lIjoidXNlcl9kaXNwbGF5IiwiZ2VuZGVyIjoibWFsZSIsImJpcnRoZGF0ZSI6IkZyaSBKYW4gMDcgMTk4MyIsInBob25lcyI6WyIxMjM0NTYiXSwiYXZhdGFyIjoiL2FwaS9zdG9yYWdlL3VzZXJJZC0xLnBuZyIsImxhc3RMb2dpbiI6IjEyMzIxMzIzIiwiZW5hYmxlZCI6dHJ1ZSwiYmFubmVkIjpmYWxzZSwidmVyaWZpZWQiOnRydWUsImJhblJlYXNvbiI6bnVsbCwicm9sZXMiOlt7ImlkIjoiUk9MRV9VU0VSIiwibmFtZSI6InVzZXIifV0sImV4cCI6NzAwMDAwMDAwMH0.Ru231tKypE9GIzUuQaTHZW8E5UwTBliefOey0K1PuLg';

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        // array in local storage for registered users
        // const users: any[] = JSON.parse(localStorage.getItem('users') || '') || [];

        // wrap in delayed observable to simulate server api call
        this.logger.info('FakeBackend: intercept()');
        // wrap in delayed observable to simulate server api call
        return <Observable<HttpEvent<any>>> of(null).pipe(
          mergeMap(() => {
            this.logger.info('FakeBackend: start request matching for URL ', request.url);
            const url = request.url.valueOf();

            // Images
            if ((url === config.endpoint.images.getImage) && (url === config.endpoint.images.uploadImage)) {
                return next.handle(request);
            }
// ---------------------------------AuthenticationService------------------------------------------

            // authenticate
            if (url.endsWith(config.endpoint.auth.issueNewToken)) {
                // request.body.forEach((value, key) => { console.log(key + ' ' + value); });
                this.logger.info('FakeBackend: fake answer for AuthentificationService.login()');
                const loginType = request.body['loginType'];
                const authentication = {
                    'access_token': null,
                    'token_type': 'bearer',
                    'refresh_token': 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJvcmdhbml6YXRpb25JZCI6IjQyZDNkNGY1LTlmMzMtNDJmNC04YWNhLWI3NTE5ZDZhZjFiYiIsInVzZXJfbmFtZSI6ImFkbWluIiwic2NvcGUiOlsid2ViY2xpZW50Il0sImF0aSI6ImRmMmExNzQ1LWY0ODUtNDhiZi1hNGRjLTc2OTViYmY5NmJmMSIsImV4cCI6MTUyMTIyMDk1NSwiYXV0aG9yaXRpZXMiOlsiUk9MRV9BRE1JTiIsIlJPTEVfVVNFUiJdLCJqdGkiOiI3ZjQ4MDVkNC0yZjBkLTQyNzgtYWQ2ZS01ZWQ2MjkxODQ2YTEiLCJjbGllbnRfaWQiOiJtaWNyb2xtczM2MCJ9.EZQyobGe3V1V4AuDtoXSOheg8dmzfuc010w4k5PGw7M',
                    'expires_in': 86400000,
                    'scope': 'webclient',
                    'organizationId': '42d3d4f5-9f33-42f4-8aca-b7519d6af1bb',
                    'jti': 'df2a1745-f485-48bf-a4dc-7695bbf96bf1'
                };
                if (loginType === 'password') {
                    this.logger.info('FakeBackend: password login');
                    const username = request.body['email'];
                    const password = request.body['password'];
                    this.logger.info('UserName and Password: ', username, password);
                    if (username === 'testUser' && password === 'Passw0rd!') {
                        authentication['access_token'] = this.adminAccessToken;
                    }
                    if (username === 'user' && password === '123456') {
                        authentication['access_token'] = this.userAccessToken;
                    }

                } else if (loginType === 'oAuth2') {
                    const fakeAdminEmail = 'test.adm@gmail.com';
                    const provider = request.body['provider'];
                    const code = request.body.get('code');
                    const httpOptions = {
                        headers: new HttpHeaders({
                            'Accept': 'application/json'
                        })
                    };
                    //const providerData = this.oAuthProvidersData.find(p => p.provider === provider);
                }
                if (authentication['access_token']) {
                    this.logger.info('FakeBackend: password auth successful, new token issued.');
                    return of(new HttpResponse({ status: 200, body: authentication }));
                // else return 400 bad request
                } else {
                    this.logger.info('FakeBackend: auth error, username\\password don\'t match the criteria! Throwing error');
                    return observableThrowError('Username or password is incorrect');
                }
            }

// ---------------------------------UserService------------------------------------------
            // get users, is email unique, is username unique
            if (url.endsWith(config.endpoint.users.getAll) && request.method === 'GET') {
                // check for fake auth token in header and return users if valid
                if (request.headers.get('Authorization') === `Bearer ${this.adminAccessToken}`) {
                    // is email unique
                    if (request.params.has('email')) {
                        this.logger.info('FakeBackend: fake answer for UserService.isUniqueEmail');
                        const email = request.params.get('email');
                        let isUniqueEmail = true;
                        if (ALL_USERS.filter(user => user.email === email).length > 0) {
                            isUniqueEmail = false;
                        }
                        return of(new HttpResponse({status: 200, body: {isUniqueEmail: isUniqueEmail}}));
                    }
                    // is username unique
                    if (request.params.has('login')) {
                        this.logger.info('FakeBackend: fake answer for UserService.isUniqueUsername');
                        const login = request.params.get('login');
                        let isUniqueUsername = true;
                        if (ALL_USERS.filter(user => user.login === login).length > 0) {
                            isUniqueUsername = false;
                        }
                        return of(new HttpResponse({status: 200, body: {isUniqueUsername: isUniqueUsername}}));
                    }
                    // get users
                    this.logger.info('FakeBackend: fake answer for UserService.getAll()');
                    return of(new HttpResponse({ status: 200, body: ALL_USERS }));
                } else {
                    // return 401 not authorised if token is null or invalid
                    return observableThrowError('Unauthorised');
                }
            }

            // delete user :id
            if (url.indexOf(config.endpoint.users.delete) > 0 && request.method === 'DELETE') {
                this.logger.info('FakeBackend: fake answer for UserService.delete()');
                // check for fake auth token in header and return users if valid
                if (request.headers.get('Authorization') === `Bearer ${this.adminAccessToken}`) {
                    for (let i = 0; i < ALL_USERS.length; i++) {
                        if (url.endsWith(ALL_USERS[i].id.toString()) ) {
                            ALL_USERS.splice(i, 1);
                        }
                    }
                    return of(new HttpResponse({ status: 204 }));
                } else {
                    // return 401 not authorised if token is null or invalid
                    return observableThrowError('Unauthorised');
                }
            }

            // get user by username
            if (url.match(/\/api\/users\/\w+$/) && request.method === 'GET') {
                // check for fake auth token in header and return user if valid, this security is implemented server side in a real application
                if (request.headers.get('Authorization') === `Bearer ${this.adminAccessToken}`) {
                    // find user by id in users array
                    const urlParts = request.url.split('/');
                    const username = urlParts[urlParts.length - 1];
                    const matchedUsers = ALL_USERS.filter(item => item.login === username);
                    const user = matchedUsers.length ? matchedUsers[0] : null;

                    return of(new HttpResponse({ status: 200, body: user }));
                } else {
                    // return 401 not authorised if token is null or invalid
                    return observableThrowError('Unauthorised');
                }
            }

            // create user
            if (request.url.endsWith(config.endpoint.users.create) && request.method === 'POST') {
                this.logger.info('FakeBackend: fake answer for UserService.create()');
                if (request.headers.get('Authorization') === `Bearer ${this.adminAccessToken}`) {
                    // get new user object from post body
                    const newUser = User.fromJson(request.body);
                    let isExistsUsername = false;
                    let isExistsEmail = false;
                    // check if username and email are unique
                    ALL_USERS.forEach(user => {
                        if (user.login === newUser.login) {
                            isExistsUsername = true;
                        }
                        if (user.email === newUser.email) {
                            isExistsEmail = true;
                        }
                    });
                    if (isExistsUsername) {
                        // NOTE: validation exists in component
                        return observableThrowError(`User with username "${newUser.login}" already exists`);
                    }
                    if (isExistsEmail) {
                        // NOTE: validation exists in component
                        return observableThrowError(`User with email "${newUser.email}" already exists`);
                    }
                    newUser.id = this.userIdCnt++;
                    ALL_USERS.push(newUser);
                    return of(new HttpResponse({ status: 201, body: newUser }));
                } else {
                    // return 401 not authorised if token is null or invalid
                    return observableThrowError('Unauthorised');
                }
            }

            // update user
            if (request.url.indexOf(config.endpoint.users.update) > 0 && request.method === 'PUT') {
                this.logger.info('FakeBackend: fake answer for UserService.update()');
                if (request.headers.get('Authorization') === `Bearer ${this.adminAccessToken}`) {
                    const updatedUser = request.body;
                    let isExistsUsername = false;
                    let isExistsEmail = false;
                    for (let i = 0; i < ALL_USERS.length; i++) {
                        // find user in database
                        if (ALL_USERS[i].id === updatedUser.id) {
                            // check if username and email are unique
                            ALL_USERS.forEach(user => {
                                // check if username unique and thisn't current user
                                if (user.login === updatedUser.login && user.id !== updatedUser.id) {
                                    isExistsUsername = true;
                                }
                                // check if email unique and thisn't current user
                                if (user.email === updatedUser.email  && user.id !== updatedUser.id) {
                                    isExistsEmail = true;
                                }
                            });
                            if (isExistsUsername) {
                                // NOTE: validation exists in component
                                return observableThrowError(`User with username "${updatedUser.username}" already exists`);
                            }
                            if (isExistsEmail) {
                                // NOTE: validation exists in component
                                return observableThrowError(`User with email "${updatedUser.email}" already exists`);
                            }
                            ALL_USERS[i] = updatedUser;
                            return of(new HttpResponse({ status: 200, body: updatedUser }));
                        }
                    }
                } else {
                    // return 401 not authorised if token is null or invalid
                    return observableThrowError('Unauthorised');
                }
            }

            // load file
            if (request.url.endsWith(config.endpoint.users.update) && request.url.endsWith('storage') && request.method === 'POST') {
                this.logger.info('FakeBackend: fake answer for UserService.loadFile()');
                if (request.headers.get('Authorization') === `Bearer ${this.adminAccessToken}`) {
                    const formData: FormData = request.body;
                    const file = <File> formData.get('file');
                    const userId = formData.get('userId');
                    const description = formData.get('description');

                    if (description === 'avatar') {
                        const fileReader = new FileReader();
                        fileReader.onload = (event) => {
                            ALL_USERS.find(user => user.id === <number> userId.valueOf()).avatar = <string> fileReader.result;
                        };
                        fileReader.readAsDataURL(file);
                        return of(new HttpResponse({ status: 201, body: {status: true, source: 'some source'} }));
                    }

                } else {
                    // return 401 not authorised if token is null or invalid
                    return observableThrowError('Unauthorised');
                }
            }

// ---------------------------------RoleService------------------------------------------
            // get all roles
            if (url.endsWith(config.endpoint.roles.getAll) && request.method === 'GET') {
                this.logger.info('FakeBackend: fake answer for RoleService.getAll()');
                // check for fake auth token in header and return users if valid
                if (request.headers.get('Authorization') === `Bearer ${this.adminAccessToken}`) {
                    const roles: Role[] = ALL_ROLES.slice();
                    this.logger.info(roles);
                    return of(new HttpResponse({ status: 200, body: roles }));
                } else {
                    // return 401 not authorised if token is null or invalid
                    return observableThrowError('Unauthorised');
                }
            }

            // delete role :id
            if (url.indexOf(config.endpoint.roles.delete) > 0 && request.method === 'DELETE') {
                this.logger.info('FakeBackend: fake answer for RoleService.delete()');
                // check for fake auth token in header and return users if valid
                if (request.headers.get('Authorization') === `Bearer ${this.adminAccessToken}`) {
                    for (let i = 0; i < ALL_ROLES.length; i++) {
                        if (url.endsWith(ALL_ROLES[i].name)) {
                            ALL_ROLES.splice(i, 1);
                        }
                    }
                    return of(new HttpResponse({status: 204}));
                } else {
                    // return 401 not authorised if token is null or invalid
                    return observableThrowError('Unauthorised');
                }
            }

              // create  or update role
              if (request.url.endsWith(config.endpoint.roles.create) && request.method === 'POST') {
                  this.logger.info('FakeBackend: fake answer for RoleService.create()');

                  if (request.headers.get('Authorization') === `Bearer ${this.adminAccessToken}`) {

                      // get new role object from post body
                      const newRole = request.body;
                      const existingRoleIndex = ALL_ROLES.findIndex(role => role.name === newRole.name);
                      if (existingRoleIndex !== -1) {
                          ALL_ROLES[existingRoleIndex] = newRole; // Replace existing role
                      } else {
                          ALL_ROLES.push(newRole); // Add as new role
                      }
                      return of(new HttpResponse({ status: 201, body: newRole }));
                  } else {
                      // return 401 not authorised if token is null or invalid
                      return observableThrowError('Unauthorised');
                  }
              }

            // pass through any requests not handled above
            return next.handle(request);
        }))

        // call materialize and dematerialize to ensure delay
        // even if an error is thrown (https://github.com/Reactive-Extensions/RxJS/issues/648)
        .pipe(materialize())
        .pipe(delay(500))
        .pipe(dematerialize());
    }
}

export let fakeBackendProvider = {
    // use fake backend in place of Http service for backend-less development
    provide: HTTP_INTERCEPTORS,
    useClass: MockBackendInterceptor,
    multi: true
};