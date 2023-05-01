import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HomeComponent } from './components/home/home.component';
import { SvgViewerModule } from './components/svg-viewer/svg-viewer';
import { FooterComponent, FooterModule } from './components/footer/footer.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { MaterialModule } from './shared/material.module';
import { LoginComponent } from './components/login/login.component';
import { PasswordLoginComponent } from './components/login/password-login/password-login.component';
import { ReactiveFormsModule } from '@angular/forms';
import { AlertComponent } from './components/alert/alert.component';
import { BreadcrumbComponent } from './components/breadcrumb/breadcrumb.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    NavbarComponent,
    LoginComponent,
    PasswordLoginComponent,
    AlertComponent,
    BreadcrumbComponent,
    
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    SvgViewerModule,
    FooterModule,
    MaterialModule,
    ReactiveFormsModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
