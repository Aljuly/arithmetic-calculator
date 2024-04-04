import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpResponse } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { NGXLogger } from 'ngx-logger';
import { Role } from '../model/Role';

@Injectable({
  providedIn: 'root'
})
export class RoleService {

  constructor(private http: HttpClient, private logger: NGXLogger) { }

  getAll(): Observable<Role[]> {
    this.logger.info('RoleService: getAll()');
    return this.http.get<Role[]>('/api/roles').pipe(
      map(roles => {
        return roles;
      })
    );
  }

  delete(id: String): Observable<HttpResponse<Object>> {
      this.logger.info('RoleService: delete()');
      return this.http.delete(`/api/roles/${id}`, {observe: 'response'});
  }

  create(role: Role): Observable<HttpResponse<Role>> {
      this.logger.info('RoleService: create()');
      return this.http.post<Role>('/api/roles', role, {observe: 'response'});
  }

  update(role: Role): Observable<HttpResponse<Role>> {
      this.logger.info('RoleService: update()');
      return this.http.put<Role>(`/api/roles/${role.name}`, role, {observe: 'response'});
  }
}