import {Injectable} from '@angular/core';
import {HttpRequest, HttpHandler, HttpEvent, HttpInterceptor} from '@angular/common/http';
import {Observable} from 'rxjs';
import {LocalStorageService} from '../services/local-storage.service';
import {User} from '../model/User';
import {NGXLogger} from 'ngx-logger';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {

    constructor(
        private localService: LocalStorageService,
        private logger: NGXLogger) {
    }

// add authorization header with jwt token if available
    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        this.logger.trace('JwtInterceptor: intercept()');

        // fetch user from local storage
        // let currentUser = JSON.parse(localStorage.getItem('currentUser'));
        const currentUser: User = this.localService.readCurrentUser();

        // check if it exists and has token
        if (currentUser && currentUser.tokens) {
            // add token to http headers
            request = request.clone({
                setHeaders: {
                    Authorization: `Bearer ${currentUser.tokens.access_token}`
                }
            });
            this.logger.trace('JwtInterceptor: augment request with: ',
                `Authorization: \`Bearer ${currentUser.tokens.access_token}\``);
        } else {
            this.logger.trace('JwtInterceptor: user doesn\'t exists');
        }
        // further request processing
        return next.handle(request);
    }
}