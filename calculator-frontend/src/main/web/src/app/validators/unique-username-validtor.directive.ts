import { Directive } from '@angular/core';

import { Observable, of } from 'rxjs';
import { NG_ASYNC_VALIDATORS, AsyncValidatorFn, AbstractControl, ValidationErrors } from '@angular/forms';
import { debounceTime, distinctUntilChanged, switchMap, map } from 'rxjs/operators';

import { UserService } from '../services/user.service';

/**
 * Validator function that checks if username is unique,
 * if the parameter username exists and it equals control.value, that function doesn't
 * pass the request to the server, this needs because if we need update user information and in
 * this case if we make request, the server answer that username already exists
 *
 * @param userService service that can check if username is unique
 * @param oldUsername old username if we wont update user
 */
export function uniqueUsernameValidator(userService: UserService, oldUsername?: string): AsyncValidatorFn {
  return (control: AbstractControl): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> => {
    if (oldUsername && oldUsername === control.value) {
      return of(null);
    }
    return control.valueChanges.pipe(
      debounceTime(1000),
      distinctUntilChanged(),
      switchMap(value => userService.isUniqueUsername(value)),
      map(uniqueUsername => {
        if (uniqueUsername) {
          control.setErrors(null);
          return null;
        } else {
          control.setErrors({ 'dontUniqueUsername': true });
          return { 'dontUniqueUsername': true };
        }
      }));
  };
}

/**
 * Class for validating if username is unique. If you need to update existing user and
 * you don't need receive to the server request with old username in that case
 * use function uniqueEmailValidator
 *
 */
@Directive({
  selector: '[appUniqueUsername]',
  providers: [{
    provide: NG_ASYNC_VALIDATORS,
    useExisting: UniqueUsernameValidatorDirective,
    multi: true
  }]
})
export class UniqueUsernameValidatorDirective {

  constructor(private userService: UserService) { }

  validate(c: AbstractControl): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> {
    return uniqueUsernameValidator(this.userService)(c);
  }

}
