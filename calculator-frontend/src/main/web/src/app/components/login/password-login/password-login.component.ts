import { Component, EventEmitter, Output } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';

@Component({
  selector: 'app-password-login',
  templateUrl: './password-login.component.html',
  styleUrls: ['./password-login.component.scss']
})
export class PasswordLoginComponent {
  @Output() signinEvent = new EventEmitter<object>();
  hide = true;
  username = new FormControl('', [
    Validators.required,
    Validators.minLength(4),
    Validators.pattern('^(?=.*[A-Za-z0-9]$)[A-Za-z][A-Za-z\d.-]{0,19}$'),
  ]);
  password = new FormControl('', [
    Validators.required,
    Validators.minLength(4)
  ]);
  loginData: any;

  constructor() {}

  public signin() {
    if (this.username.valid && this.password.valid) {
      this.loginData = {
        loginType: 'password',
        email: this.username.value,
        password: this.password.value,
      };
    } else {
      this.loginData = null;
    }
    this.signinEvent.emit(this.loginData);
  }
}
