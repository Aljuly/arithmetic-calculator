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
    return stringed === null
      ? null
      : User.fromJson(stringed);
  }

  saveCurrentUser(user: User) {
    localStorage.setItem('currentUser', user.toJson());
    this.logger.debug('Saved User to Storage: ', user.toJson());
  }

  removeCurrentUser() {
    localStorage.removeItem('currentUser');
  }
}
