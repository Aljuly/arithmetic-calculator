import { Directive } from '@angular/core';

import { AbstractControl, AsyncValidator, AsyncValidatorFn, NG_ASYNC_VALIDATORS, ValidationErrors } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { debounceTime, distinctUntilChanged, map, switchMap } from 'rxjs/operators';

import { UserService } from '../services/user.service';

/**
 * Validator function that checks if email is unique,
 * if the parameter oldEmail exists and it equals control.value, that function doesn't
 * pass the request to the server, this needs because if we need update user information and in
 * this case if we make request, the server answer that email already exists
 *
 * @param userService service that can check if email is unique
 * @param oldEmail old user email if we wont update user
 */
export function uniqueEmailValidator(userService: UserService, oldEmail?: string): AsyncValidatorFn {
  return (control: AbstractControl): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> => {
    if (oldEmail && oldEmail === control.value) {
      return of(null);
    }
    return control.valueChanges.pipe(
      debounceTime(1000),
      distinctUntilChanged(),
      switchMap(value => userService.isUniqueEmail(value)),
      map(isUniqueEmail => {
        if (isUniqueEmail) {
          control.setErrors(null);
          return null;
        } else {
          control.setErrors({ 'dontUniqueEmail': true });
          return { 'dontUniqueEmail': true };
        }
      }));
  };
}

/**
 * Class for validating if email is unique. If you need to update existing user and
 * you don't need receive to the server request with old user email in that case
 * use function uniqueEmailValidator
 *
 */

@Directive({
  selector: '[appUniqueEmail]',
  providers: [{
    provide: NG_ASYNC_VALIDATORS,
    useExisting: UniqueEmailValidatorDirective,
    multi: true
  }]
})
export class UniqueEmailValidatorDirective implements AsyncValidator {

  constructor(private userService: UserService) { }

  validate(c: AbstractControl): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> {
    return uniqueEmailValidator(this.userService)(c);
  }
}
