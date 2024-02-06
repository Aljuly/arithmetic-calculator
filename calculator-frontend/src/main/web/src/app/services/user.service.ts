import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { HttpResponse } from '@angular/common/http';

import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { User } from '../model/User';
import { NGXLogger } from 'ngx-logger';
import { LocalStorageService } from './local-storage.service';
import { JsonConvert } from 'json2typescript';

@Injectable({
    providedIn: 'root'
})
export class UserService {

    private BASE_URL = '/api/users';
    private jsonConvert: JsonConvert;

    constructor(private http: HttpClient,
        private localStorageService: LocalStorageService,
        private logger: NGXLogger, ) {
            // Check the detailed reference in the chapter "JsonConvert class properties and methods"
            this.jsonConvert = new JsonConvert();
            this.jsonConvert.ignorePrimitiveChecks = false; // don't allow assigning number to string etc.
    }

    /**
     *Get all users
     *
     * @returns {Observable<User[]>} an array with all users
     */
    getAll(): Observable<User[]> {
        this.logger.info('UserService: getAll()');
        return this.http.get<any>(this.BASE_URL)
            .pipe(map(res => {
                    // json converter returns an Array?
                    //const jsonstr = JSON.stringify(res)
                    const ret = this.jsonConvert.deserialize(JSON.parse(res), User);
                    return Array.isArray(ret) ? ret : new Array(ret);
                }
            ));
    }

    /**
     * Delete user by its id
     *
     * @param {Number} id is id of specific user
     * @returns {Observable<HttpResponse<Object>>} full http response
     */
    delete(id: Number): Observable<HttpResponse<Object>> {
        this.logger.info('UserService: delete()');
        return this.http.delete(`${this.BASE_URL}/${id}`, { observe: 'response' });
    }

    /**
     * get user by its username
     *
     * @param {string} username is username of specific user
     * @returns {Observable<User>} user
     */
    getByUsername(username: string): Observable<User> {
        this.logger.info('UserService: getByUsername()');
        return this.http.get<String>(`${this.BASE_URL}/${username}`).pipe(
            map(res => {
                const _user = this.jsonConvert.deserialize(res, User);
                return Array.isArray(_user) ? _user[0] : _user
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
        return this.http.post<User>(this.BASE_URL, user, { observe: 'response' });
    }

    /**
     * Update user
     *
     * @param {User} user is user which is needed to update
     * @returns {Observable<HttpResponse<User>>} user
     */
    update(user: User): Observable<HttpResponse<User>> {
        this.logger.info('UserService: update()');
        return this.http.put<User>(`${this.BASE_URL}/${user.id}`, user, { observe: 'response' });
    }

    /**
     * Check if email is unique and doesn't belong any users
     *
     * @param {string} email is user email
     * @returns {Observable<boolean>} true if email is unique, otherwise return false
     */
    isUniqueEmail(email: string): Observable<boolean> {
        this.logger.info('UserService: isUniqueEmail()');
        return this.http.get<{ isUniqueEmail: boolean }>(this.BASE_URL,
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
        return this.http.get<{isUniqueUsername: boolean}>(this.BASE_URL, {
            params: new HttpParams().set('username', username)
        }).pipe(
            map(body => {
                return <boolean> body.isUniqueUsername;
            })
        );
    }

    /**
     * Load a file to the storage in backend
     *
     * @param {number} userId is user id which the file belongs
     * @param {string} description is short description about what the file is, for example "avatar" for user avatar
     * @param {File} file is the file which is loading
     * @returns {Observable<any>} all response
     */
    loadFile(userId: number, description: string, file: File): Observable<any> {
        const formData = new FormData();
        formData.append('userId', userId.toString());
        formData.append('description', description);
        formData.append('file', file, file.name);
        return this.http.post<any>(`${this.BASE_URL}/${userId}/storage`, formData, { observe: 'response' });
    }

    /**
     * Convert User JSON to User class
     * 
     * @param {String} asJson is a JSON representation of the User class
     * @returns {User} class
     */
    mapSingleUser(asJson: String): User {
        const _user = this.jsonConvert.deserialize(asJson, User);
        return Array.isArray(_user) ? _user[0] : _user
    }

}

