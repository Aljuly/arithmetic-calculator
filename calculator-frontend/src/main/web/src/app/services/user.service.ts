import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { HttpResponse } from '@angular/common/http';

import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Page } from '../model/Page';

import { User } from '../model/User';
import { NGXLogger } from 'ngx-logger';
import config from '../config/index';

@Injectable({
    providedIn: 'root'
})
export class UserService {

    private readonly BASE_URL : any;
    private readonly BASE_IMAGE_URL : any;
    private readonly user_endpoints: any;
    private readonly image_endpoints: any;

    constructor(
        private http: HttpClient,
        private logger: NGXLogger) {
            // Check the detailed reference in the chapter "JsonConvert class properties and methods"
            this.BASE_URL = config.api.protocol + '://' + config.api.host + ':' + config.api.port;
            this.BASE_IMAGE_URL = config.api.protocol + '://' + config.api.host + ':' + config.api.port;
            this.user_endpoints = config.endpoint.users;
            this.image_endpoints = config.endpoint.images;
    }

    /**
     * Get all users
     *
     * @returns {Observable<User[]>} an array with all users
     */
    getAll(): Observable<Page<User>> {
        this.logger.info('UserService: getAll()');
        return this.http.get<any>(this.BASE_URL + this.user_endpoints.getAll).pipe(
            map(users => {
                    return new Page<User>(users);
                }
            ));
    }
    /**
     * Get users with pagination
     *
     * @param {number} page is the page number
     * @param {number} size is the number of users per page
     * @returns {Observable<Page<User>>} a paginated list of users
     */
    getPaginated(page: number, size: number, login?: string, sortBy?: string, sortOrder?: string): Observable<Page<User>> {
        this.logger.info('UserService: getPaginated()');
        let params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString());
        if (login) {
            params = params.set('login', login);
        }
        if (sortBy) {
            params = params.set('sortBy', sortBy);
        }
        if (sortOrder) {
            params = params.set('sortOrder', sortOrder);
        }

        return this.http.get<any>(this.BASE_URL + this.user_endpoints.getAll, { params }).pipe(
            map(response => {
                return new Page<User>(response);
            })
        );
    }

    /**
     * Delete user by his id
     *
     * @param {Number} id is id of specific user
     * @returns {Observable<HttpResponse<Object>>} full http response
     */
    delete(id: Number): Observable<HttpResponse<Object>> {
        this.logger.info('UserService: delete()');
        return this.http.delete(`${this.BASE_URL + this.user_endpoints.delete}/${id}`, { observe: 'response' });
    }

    /**
     * get user by his username
     *
     * @param {string} username is username of specific user
     * @returns {Observable<User>} user
     */
    getByUsername(username: string): Observable<User> {
        this.logger.info('UserService: getByUsername()');
        return this.http.get<String>(`${this.BASE_URL + this.user_endpoints.getById}/by-name/${username}`).pipe(
            map(res => {
                return User.fromJson(res.toString());
            })
        );
    }

    /**
     * Create user
     *
     * @param {User} user is user which is needed to create
     * @returns {Observable<HttpResponse<User>>} user
     */
    create(user: User): Observable<HttpResponse<User>> {
        this.logger.info('UserService: create()');
        return this.http.post<User>(this.BASE_URL + this.user_endpoints.create, user, { observe: 'response' });
    }

    /**
     * Update user
     *
     * @param {User} user is user which is needed to update
     * @returns {Observable<HttpResponse<User>>} user
     */
    update(user: User): Observable<HttpResponse<User>> {
        this.logger.info('UserService: update()');
        return this.http.put<User>(`${this.BASE_URL + this.user_endpoints.update}`, user, { observe: 'response' });
    }

    /**
     * Check if email is unique and doesn't belong any users
     *
     * @param {string} email is user email
     * @returns {Observable<boolean>} true if email is unique, otherwise return false
     */
    isUniqueEmail(email: string): Observable<boolean> {
        this.logger.info('UserService: isUniqueEmail()');
        return this.http.get<{ isUniqueEmail: boolean }>(this.BASE_URL + this.user_endpoints.email,
            { params: new HttpParams().set('email', email) })
            .pipe(
                map(body => {
                    return <boolean>body.isUniqueEmail;
                })
            );
    }

    /**
     * Check if username is unique and doesn't belong any users
     *
     * @param {string} username is user username
     * @returns {Observable<boolean>} true if username is unique, otherwise return false
     */
    isUniqueUsername(username: string): Observable<boolean> {
        this.logger.info('UserService: isUniqueUsername');
        return this.http.get<{isUniqueUsername: boolean}>(this.BASE_URL + this.user_endpoints.name, {
            params: new HttpParams().set('login', username)
        }).pipe(
            map(body => {
                return <boolean> body.isUniqueUsername;
            })
        );
    }

    /**
     * Load a file to the storage in backend
     * @param {File} file is the file which is loading
     * @returns {Observable<any>} all response
     */
    loadFile(file: File): Observable<any> {
        const formData = new FormData();
        formData.append('file', file, file.name);
        return this.http.post<any>(`${this.BASE_IMAGE_URL + this.image_endpoints.uploadImage}`, formData, { observe: 'response' });
    }

    getImageUrl(avatarId: string): string {
      // Use the configured endpoint to get the image
      return `${this.BASE_IMAGE_URL}${this.image_endpoints.getImage}${avatarId}`;
    }
    /**
     * Read an image file
     * @param avatarId
     */
    getAvatarBlob(avatarId: string): Observable<Blob> {
      return this.http.get(`${this.BASE_IMAGE_URL}${this.image_endpoints.getImage}${avatarId}`, {
        responseType: 'blob'
      });
    }

}

