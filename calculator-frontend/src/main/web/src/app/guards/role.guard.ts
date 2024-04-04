import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  RouterStateSnapshot,
  RouteReuseStrategy,
} from '@angular/router';

import { NGXLogger } from 'ngx-logger';

import { Role } from '../model/Role';
import { LocalStorageService } from '../services/local-storage.service';
import { AuthenticationService } from '../services';

@Injectable()
export class RoleGuard implements CanActivate {
  private userRoles: Role[] = [];

  constructor(
    private router: Router,
    private storageService: LocalStorageService,
    private authService: AuthenticationService,
    private logger: NGXLogger
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    this.logger.trace('RoleGuard: checking role');
    // this will be passed from the route config
    // on the data property

    const expectedRoles = route.data['expectedRoles'];

    const permission = this.authService.hasRole(expectedRoles);
    if (permission) {
      return true;
    } else {
      this.router.navigate(['/no-access'], {
        queryParams: { requestedUrl: state.url },
      });
      return false;
    }
  }
}
