import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { AuthenticationService } from '../services';
import { NGXLogger } from 'ngx-logger';

@Injectable()
export class AuthGuard implements CanActivate {
  constructor(
    private router: Router,
    private authService: AuthenticationService,
    private logger: NGXLogger
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    this.logger.trace('AuthGuard: canActivate(); route,state: ', route, state);
    if (this.authService.isLoggedIn()) {
      this.logger.debug('AuthGuard: user is logged so allow this route');
      // logged in so return true
      return true;
    }

    // not logged in so redirect to login page with the return url
    this.logger.warn(
      'AuthGuard: user is NOT logged-in so disallow this route and redirecting to /login'
    );
    this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
    return false;
  }
}
