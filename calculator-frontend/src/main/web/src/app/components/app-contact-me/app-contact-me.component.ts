import { Component, OnInit } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { NGXLogger } from 'ngx-logger';
import { MatSnackBar } from '@angular/material/snack-bar';
import { WidgetData } from './widget-data';
import { WidgetService } from '../../services/widget.service';

@Component({
    selector: 'app-contact-me',
    templateUrl: './app-contact-me.component.html',
    styleUrls: ['./app-contact-me.component.scss']
})
export class AppContactMeComponent implements OnInit {

    emailRegexp = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    isWidgetVisible = false;

    email = new FormControl('', [Validators.required,
        Validators.pattern(this.emailRegexp)]);
    username = new FormControl('', [Validators.required]);
    message = new FormControl('', [Validators.required]);
    unit = new FormControl('', [Validators.required]);
    telephone = new FormControl('', []);

    constructor(
        private logger: NGXLogger,
        private snackBar: MatSnackBar,
        private widgetService: WidgetService
        ) {
    }

    ngOnInit() {
        this.logger.trace('ContactMeComponent: ngOnInit()');
    }

    toggleWidget() {
        this.isWidgetVisible = !this.isWidgetVisible;
    }

    getErrorMessage(value: FormControl) {
        return value.hasError('required') ? 'You must enter a value' :
            value.hasError('pattern') ? 'Not a valid email' :
                '';
    }

    sendMessage() {
        const widgetData: WidgetData = {
            'username': this.username.value,
            'email': this.email.value,
            'unit': this.unit.value,
            'message': this.message.value,
            'phone': this.telephone.value
        };

        this.logger.trace('ContactMeComponent: sendMessage() widgetData:', widgetData);

        this.widgetService.postData(widgetData)
             .subscribe(responseData => {
                     this.snackBar.open('Message has been sent', 'Close', {duration: 2500});
                     this.logger.trace('ContactMeComponent: sendMessage() response:', responseData);
                 },
                 error => {
                     this.snackBar.open('An error occurred', 'Close', {duration: 2500});
                     this.logger.trace('ContactMeComponent: sendMessage() error:', error.error);
                 }
             );
    }
}
