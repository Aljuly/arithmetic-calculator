import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpResponse } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { NGXLogger } from 'ngx-logger';
import { Role } from '../model/Role';
import config from '../config/index';

@Injectable({
  providedIn: 'root'
})
export class RoleService {

  private readonly url: any;
  private readonly endpoints: any;

  constructor(private http: HttpClient, private logger: NGXLogger) {
    this.url = config.api.protocol + '://' + config.api.host + ':' + config.api.port;
    this.endpoints = config.endpoint.roles;
  }

  getAll(): Observable<Role[]> {
    this.logger.info('RoleService: getAll()');
    return this.http.get<Role[]>(this.url + this.endpoints.getAll).pipe(
      map(roles => {
        return roles;
      })
    );
  }

  delete(id: number): Observable<HttpResponse<Object>> {
      this.logger.info('RoleService: delete()');
      return this.http.delete(this.url + this.endpoints.delete + '/' + `${id}`, {observe: 'response'});
  }

  create(role: Object): Observable<HttpResponse<Role>> {
      this.logger.info('RoleService: create()');
      return this.http.post<Role>(this.url + this.endpoints.create, role, {observe: 'response'});
  }

  update(role: Object): Observable<HttpResponse<Role>> {
      this.logger.info('RoleService: update()');
      return this.http.post<Role>(this.url + this.endpoints.update, role, {observe: 'response'});
  }
}