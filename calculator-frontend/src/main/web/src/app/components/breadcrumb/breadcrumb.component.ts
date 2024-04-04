import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import { filter, distinctUntilChanged, map } from 'rxjs/operators';

import { NGXLogger } from 'ngx-logger';

import { BreadCrumb } from './breadcrumb';

@Component({
  selector: 'app-breadcrumb',
  templateUrl: './breadcrumb.component.html',
  styleUrls: ['./breadcrumb.component.scss'],
})
export class BreadcrumbComponent implements OnInit {
  breadcrumbs$ = this.router.events.pipe(
    filter((event) => event instanceof NavigationEnd),
    distinctUntilChanged(),
    map((event) => this.buildBreadCrumb(this.activatedRoute.root))
  );

  constructor(
    private logger: NGXLogger,
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit() {
    this.logger.trace('BreadcrumbComponent: ngOnInit()');
  }

  buildBreadCrumb(
    route: ActivatedRoute,
    url: string = '',
    breadcrumbs: Array<BreadCrumb> = []
  ): Array<BreadCrumb> {
    let label = 'Home';

    // If no routeConfig is avalailable we are on the root path
    if (route.routeConfig) {
      if (route.routeConfig.data && route.routeConfig.data['breadcrumb']) {
        label = route.routeConfig.data['breadcrumb'];
      } else {
        return breadcrumbs;
      }
    }

    let path = route.routeConfig ? route.routeConfig.path + '' : '';
    // In the routeConfig the complete path is not available,
    // so we rebuild it each time
    let nextUrl = '';
    let breadcrumb: BreadCrumb;
    const newBreadcrumbs = [...breadcrumbs];

    // if parameters exist then build one level breadcrumb for each parameter
    // otherwise create one level breadcrumb
    if (Object.keys(route.snapshot.params).length > 0) {
      Object.keys(route.snapshot.params).forEach((key) => {
        path = path.replace('/:' + key, '');
      });
      nextUrl = `${url}${path}/`;
      breadcrumb = {
        label: label,
        url: nextUrl,
      };
      newBreadcrumbs.push(breadcrumb);
      Object.keys(route.snapshot.params).forEach((key) => {
        label = route.snapshot.params[key];
        path = path + '/' + label;
        nextUrl = `${url}${path}/`;
        breadcrumb = {
          label: label,
          url: nextUrl,
        };
        newBreadcrumbs.push(breadcrumb);
      });
    } else {
      nextUrl = `${url}${path}/`;
      breadcrumb = {
        label: label,
        url: nextUrl,
      };
      newBreadcrumbs.push(breadcrumb);
    }

    if (route.firstChild) {
      // If we are not on our current path yet,
      // there will be more children to look after, to build our breadcumb
      return this.buildBreadCrumb(route.firstChild, nextUrl, newBreadcrumbs);
    }
    return newBreadcrumbs;
  }
}
