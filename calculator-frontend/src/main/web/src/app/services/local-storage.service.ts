/**
 * Provide single point to access global state stored in local storage.
 */
import { User } from '../model/User';
import { Injectable } from '@angular/core';
import { JsonConvert } from 'json2typescript';

@Injectable()
export class LocalStorageService {
  jsonConvert: JsonConvert = new JsonConvert();
  /**
   * Reads user from local storage.
   * @returns { User } null if user is not stored
   */
  readCurrentUser(): User | any {
    const stringed = localStorage.getItem('currentUser');
    return stringed === null
      ? null
      : this.jsonConvert.deserialize(JSON.parse(stringed), User);
  }

  saveCurrentUser(user: User) {
    localStorage.setItem('currentUser', user.toJsonString());
  }

  removeCurrentUser() {
    localStorage.removeItem('currentUser');
  }
}
