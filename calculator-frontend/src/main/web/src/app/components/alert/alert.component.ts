import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';

import { AlertService } from '../../services/index';

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'app-alert',
  templateUrl: 'alert.component.html'
})

export class AlertComponent implements OnInit, OnDestroy {
  private subscription!: Subscription;
  message: any;

  constructor(private alertService: AlertService) { }

  ngOnInit() {
    this.subscription = this.alertService.getMessage().subscribe(message => {
      this.message = message;
    });
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}

