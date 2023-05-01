import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { LoginComponent } from '../login/login.component';

/**
 * keys are url parts, values are displayed texts
 *
 * A key must be a simple word, you cannot add parameters
 * to it e.g. this 'followers?page=1&order=newest' will be escaped to 'followers%3Fpage%3D1&order%3Dnewest'
 * anyway you would have to use
 *   <a class="nav-link"
 *      routerLink="/followers"
 *      [queryParams]="{page: 1, order: 'newest'}">
 *   Followers</a>
 * so to add parameters we should rework the approach.
 */
export const SECTIONS: any = {
  'users': 'Users',
  'roles': 'Roles',
};
const SECTIONS_KEYS = Object.keys(SECTIONS);

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
})
export class NavbarComponent implements OnInit {
  @Output() toggleTheme = new EventEmitter<void>();
  loginDialogRef!: MatDialogRef<LoginComponent>;

  constructor(
    public authService: AuthenticationService,
    public router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit() {}

  get sections() {
    return SECTIONS;
  }

  get sectionKeys() {
    return SECTIONS_KEYS;
  }

  onLogin() {
    this.loginDialogRef = this.dialog.open(LoginComponent, {
      disableClose: true,
      panelClass: ['no-padding-dialog'], // delete padding in this dialog https://material.angular.io/guide/customizing-component-styles
    });
  }
}
