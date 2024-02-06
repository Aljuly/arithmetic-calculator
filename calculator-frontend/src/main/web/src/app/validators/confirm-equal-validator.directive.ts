import { Directive, Input } from '@angular/core';
import { Validator, NG_VALIDATORS, AbstractControl, ValidatorFn, ValidationErrors } from '@angular/forms';
import { Subscription } from 'rxjs';

export function confirmEqualValidation(controleNameToCompare: string): ValidatorFn {
  return (c: AbstractControl): ValidationErrors | null => {
    if (c.value === null || c.value.length === 0 ) {
      return null;
    }
    const controleToCompare = c.root.get(controleNameToCompare);
    if (controleNameToCompare) {
      const subscriprion = controleToCompare?.valueChanges.subscribe(() => {
        c.updateValueAndValidity();
        subscriprion?.unsubscribe();
      });
    }
    return controleToCompare && controleToCompare.value !== c.value ? {'notEqual': true } : null;
  };
}
@Directive({
  selector: '[appConfirmEqual]',
  providers: [{
    provide: NG_VALIDATORS,
    useExisting: ConfirmEqualValidatorDirective,
    multi: true
  }]
})
export class ConfirmEqualValidatorDirective implements Validator {

  @Input() confirmEqual!: string;

  constructor() { }

  validate(c: AbstractControl): ValidationErrors | null {
    return confirmEqualValidation(this.confirmEqual)(c);
  }

}
