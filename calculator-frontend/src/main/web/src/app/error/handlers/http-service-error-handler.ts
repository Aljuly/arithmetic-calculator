
import {throwError as observableThrowError, Observable} from 'rxjs';
import {AppError} from '../errors/app-error';
import {BadInputForRequestError} from '../errors/bad-input-for-request-error';
import {NotFoundError} from '../errors/not-found-error';
import {ServiceActionMeta} from './service-action-meta';
// import {Response} from '@angular/http/src/static_response';


export class HttpServiceErrorHandler {
  constructor(private meta: ServiceActionMeta) {
  }

  handleError(erroneous: Response): Observable<Response> {
    const message = this.meta.getErrorMessage();
    let e: AppError;
    // check status and build appropriate error object
    if (erroneous.status === 400) {
      e = new BadInputForRequestError(message, erroneous);
    } else if (erroneous.status === 404) {
      e = new NotFoundError(message, erroneous);
    } else {
      e = new AppError(message, erroneous);
    }
    return observableThrowError(e);
  }
}
