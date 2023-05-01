import { Injectable } from '@angular/core';
import { HttpRequest, HttpResponse, HttpHandler, HttpEvent, HttpInterceptor, HTTP_INTERCEPTORS } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { delay, mergeMap, materialize, dematerialize } from 'rxjs/operators';
import { NGXLogger } from 'ngx-logger';

@Injectable()
export class MockBackendInterceptor implements HttpInterceptor {

    constructor(private logger: NGXLogger) { }

    adminAccessToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6ImFkbWluSWQiLCJ1c2VybmFtZSI6ImFkbWluIiwicGFzc3dvcmQiOiJhZG1pbiIsInNhbHQiOiJzYWx0IiwiZW1haWwiOiJhZG1pbkBhZG1pbiIsImZpcnN0TmFtZSI6ImFkbWluX2ZpcnN0IiwibGFzdE5hbWUiOiJhZG1pbl9sYXN0IiwibWlkZGxlTmFtZSI6ImFkbWluX21pZGRsZSIsImRpc3BsYXlOYW1lIjoiYWRtaW5fZGlzcGxheSIsImdlbmRlciI6ImZlbWFsZSIsImJpcnRoZGF0ZSI6IkZyaSBKYW4gMDcgMTk4MyIsInBob25lcyI6WyIxMjM0NTYiXSwiYXZhdGFyIjoiL2FwaS9zdG9yYWdlL2FkbWluSWQtMS5wbmciLCJsYXN0TG9naW4iOiIxMjMyMTMyMyIsImVuYWJsZWQiOnRydWUsImJhbm5lZCI6ZmFsc2UsInZlcmlmaWVkIjp0cnVlLCJiYW5SZWFzb24iOm51bGwsInJvbGVzIjpbeyJpZCI6IlJPTEVfQURNSU4iLCJuYW1lIjoiYWRtaW4ifSx7ImlkIjoiUk9MRV9NRU5UT1IiLCJuYW1lIjoibWVudG9yIn1dLCJleHAiOjc1OTgyMTIzMzJ9.INjY_Mef8-7zo_gHBNU7mavGqz1K5j85ASC1ZuLBmjs';
    userAccessToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6InVzZXJJZCIsInVzZXJuYW1lIjoidXNlciIsInBhc3N3b3JkIjoidXNlciIsInNhbHQiOiJzYWx0IiwiZW1haWwiOiJ1c2VyQHVzZXIiLCJmaXJzdE5hbWUiOiJ1c2VyX2ZpcnN0IiwibGFzdE5hbWUiOiJ1c2VybGFzdCIsIm1pZGRsZU5hbWUiOiJ1c2VyX21pZGRsZSIsImRpc3BsYXlOYW1lIjoidXNlcl9kaXNwbGF5IiwiZ2VuZGVyIjoibWFsZSIsImJpcnRoZGF0ZSI6IkZyaSBKYW4gMDcgMTk4MyIsInBob25lcyI6WyIxMjM0NTYiXSwiYXZhdGFyIjoiL2FwaS9zdG9yYWdlL3VzZXJJZC0xLnBuZyIsImxhc3RMb2dpbiI6IjEyMzIxMzIzIiwiZW5hYmxlZCI6dHJ1ZSwiYmFubmVkIjpmYWxzZSwidmVyaWZpZWQiOnRydWUsImJhblJlYXNvbiI6bnVsbCwicm9sZXMiOlt7ImlkIjoiUk9MRV9VU0VSIiwibmFtZSI6InVzZXIifV0sImV4cCI6NzAwMDAwMDAwMH0.Ru231tKypE9GIzUuQaTHZW8E5UwTBliefOey0K1PuLg';

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        // array in local storage for registered users
        const users: any[] = JSON.parse(localStorage.getItem('users') || '') || [];

        // wrap in delayed observable to simulate server api call
        return <Observable<HttpEvent<any>>> of(null).pipe(mergeMap(() => {
            this.logger.info('FakeBackend: start request matching for URL ', request.url);
            const url = request.url.valueOf();
            // mock authenticate
            if (request.url.endsWith('/users/authenticate') && request.method === 'POST') {
                // find if any user matches login credentials
                const filteredUsers = users.filter(user => {
                    return user.userName === request.body.username && user.password === request.body.password;
                });
                if (filteredUsers.length) {
                    // if login details are valid return 200 OK with user details and fake jwt token
                    const user = filteredUsers[0];
                    const body = {
                        name: user.userName,
                        login: user.login,
                        email: user.email,
                        token: 'mock-jwt-token'
                    };
                    return of(new HttpResponse({ status: 200, body: body }));
                } else {
                    // else return 400 bad request
                    return throwError({ error: { message: 'Username or password is incorrect' } });
                }
            }

            // mock register user
            if (request.url.endsWith('/users/register') && request.method === 'POST') {
                // get new user object from post body
                const newUser = request.body;

                // validation
                const duplicateUser = users.filter(user => user.username === newUser.userName).length;
                if (duplicateUser) {
                    return throwError({ error: { message: 'Username "' + newUser.username + '" is already registered!' } });
                }

                // save new user
                newUser.id = users.length + 1;
                users.push(newUser);
                localStorage.setItem('users', JSON.stringify(users));

                // respond 200 OK
                return of(new HttpResponse({ status: 200 }));
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