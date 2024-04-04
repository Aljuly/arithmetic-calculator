import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { ROUTING } from './app-routing.module';
import { LOGGING } from './app.logging';
import { AppComponent } from './components/app/app.component';
import { AppContactMeComponent } from './components/app-contact-me/app-contact-me.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HomeComponent } from './components/home/home.component';
import { SvgViewerModule } from './components/svg-viewer/svg-viewer';
import { FooterModule } from './components/footer/footer.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { MaterialModule } from './shared/material.module';
import { LoginComponent } from './components/login/login.component';
import { PasswordLoginComponent } from './components/login/password-login/password-login.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AlertComponent } from './components/alert/alert.component';
import { BreadcrumbComponent } from './components/breadcrumb/breadcrumb.component';
import { RoleListComponent } from './components/role-list/role-list.component';
import { RoleFormDialogComponent } from './components/role-form-dialog/role-form-dialog.component';
import { ConfirmDialogComponent } from './components/confirm-dilog/confirm-dialog.component';
import { UserListComponent } from './components/user-list/user-list.component';
import { UserListItemComponent } from './components/user-list/user-list-item/user-list-item.component';
import { UserFormDialogComponent } from './components/user-list/user-form-dialog/user-form-dialog.component';
import { ConfirmationDialogComponent } from './components/user-list/confirmation-dialog/confirmation-dialog.component';
import { MemberListComponent } from './components/member-list/member-list.component';
import { MemberFormDialogComponent } from './components/member-list/member-form-dialog/member-form-dialog.component';
import { MemberViewComponent } from './components/member-list/member-view/member-view.component';
import { MemberCardComponent } from './components/member-list/member-card/member-card.component';
import { ConfirmEqualValidatorDirective } from './validators/confirm-equal-validator.directive';
import { UniqueEmailValidatorDirective } from './validators/unique-email-validator.directive';
import { UniqueUsernameValidatorDirective } from './validators/unique-username-validtor.directive';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
//import { APP_BASE_HREF } from '@angular/common';
import { NGXLogger } from 'ngx-logger';
import { AuthGuard, RoleGuard } from './guards';
import { jwtInterceptor } from './helper/jwt-interceptor';
import { AlertService, AuthenticationService } from './services';
import { LocalStorageService } from './services/local-storage.service';
import { RoleService } from './services/role.service';
import { UserService } from './services/user.service';
import { MockBackendInterceptor, fakeBackendProvider } from './helper/mock-backend-interceptor';
import { NoAccessComponent } from './components/no-access/no-access.component';
import { NotFoundComponent } from './components/not-found/not-found.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    NavbarComponent,
    LoginComponent,
    PasswordLoginComponent,
    AlertComponent,
    BreadcrumbComponent,
    RoleListComponent,
    RoleFormDialogComponent,
    ConfirmDialogComponent,
    UserListComponent,
    UserListItemComponent,
    UserFormDialogComponent,
    ConfirmationDialogComponent,
    MemberListComponent,
    MemberFormDialogComponent,
    MemberViewComponent,
    MemberCardComponent,
    UniqueEmailValidatorDirective,
    UniqueUsernameValidatorDirective,
    ConfirmEqualValidatorDirective,
    NotFoundComponent,
    NoAccessComponent,
    AppContactMeComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    ROUTING,
    LOGGING,
    BrowserAnimationsModule,
    MaterialModule,
    FooterModule,
    SvgViewerModule,
  ],
  providers: [
  // logging & global error handler
    NGXLogger,
  /* {provide: ErrorHandler, useClass: GlobalErrorHandler},*/
            
    // auth stuff
    AuthGuard,
    RoleGuard,
    //{ provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    jwtInterceptor,
    //fakeBackendProvider,
    // { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: MockBackendInterceptor, multi: true },
    //{ provide: APP_BASE_HREF, useValue: '/' },
    // {provide: AuthHttp, useFactory: authHttpServiceFactory, deps: [Http, RequestOptions, LocalStorageService]},
    // services
    AlertService,
    AuthenticationService,
    RoleService,
    UserService,
    // PostService,
    // ImageService,
    // GithubFollowersService,
    LocalStorageService,
    // provider used to create fake backend
    // UserBuilder
  ],
  entryComponents: [
    ConfirmDialogComponent,
    ConfirmationDialogComponent,
    UserFormDialogComponent,
    //MemberFormDialogComponent,
    // NoncorrectPhoneConfirmationDialogComponent,
    //ContactMeComponent,
    // NoncorrectPhoneConfirmationDialogComponent,
    LoginComponent,
    RoleFormDialogComponent,
    //PhonesDialogComponent
],
  bootstrap: [AppComponent]
})
export class AppModule { }
