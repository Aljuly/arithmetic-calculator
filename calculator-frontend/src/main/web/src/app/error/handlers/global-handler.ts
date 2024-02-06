import {ErrorHandler, Injectable, Injector} from '@angular/core';
import {LocationStrategy, PathLocationStrategy} from '@angular/common';

import {NGXLogger} from 'ngx-logger';

import * as StackTrace from 'stacktrace-js'; /* use StackTrace.js to get the full stack
and also add the current URL to the message going upstream to the server */

import {AlertService} from '../../services';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
    constructor(
        /*private alertService: AlertService,
        private logger: NGXLogger*/
        private injector: Injector) {}
    // since error handling is really important it needs to be loaded first, thus
    // making it not possible to use dependency injection in the constructor to get
    // other services such as the error handle api service to send the server our error details.
    // As a result, we have to manually call the injector with the service name in
    // the execution of the handleError function.

    handleError(error: any): void {
        const logger = this.injector.get(NGXLogger);
        const alertService = this.injector.get(AlertService);
        const location = this.injector.get(LocationStrategy);
        const url = location instanceof PathLocationStrategy ? location.path() : '';
        const message = error.message ? error.message : error.toString();

        // get the stack trace, lets grab the last 10 stacks only
        StackTrace.fromError(error).then(stackframes => {
            const stackString = stackframes
                .splice(0, 20)
                .map(function (sf) {
                    return sf.toString();
                }).join('\n');
            logger.error('GlobalErrorHandler: unknown unexpected error: ', { message, url, stack: stackString });
        });

        // display message to the user
        alertService.error(message);
        throw error; // TODO this implementation behaviour need to be rethinked with close analysis of Angular internals
    }
}
