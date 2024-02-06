/**
 * Provide single point to access global state stored in local storage.
 */
import { User } from '../model/User';
import { Injectable } from '@angular/core';
import { NGXLogger } from 'ngx-logger';

@Injectable()
export class LocalStorageService {

   /**
   * Reads user from local storage.
   * @returns { User } null if user is not stored
   */
  constructor(private logger: NGXLogger) {}

  readCurrentUser(): User | any {
    const stringed = localStorage.getItem('currentUser');
    //this.logger.debug('Read User from Storage: ', stringed === null ? 'empty' : stringed);
    //console.log('Read User from Storage: ' + new String(stringed));
    return stringed === null
      ? null
      : User.fromJsonString(stringed);
  }

  saveCurrentUser(user: User) {
    localStorage.setItem('currentUser', user.toJsonString());
  }

  removeCurrentUser() {
    localStorage.removeItem('currentUser');
  }
}
