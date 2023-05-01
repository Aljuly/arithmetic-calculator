import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import {NGXLogger} from 'ngx-logger';
const { version: appVersion } = require('../../../../package.json');

@Component({
  moduleId: module.id.toString(),
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  providers: [NGXLogger],
  encapsulation: ViewEncapsulation.None
})
export class HomeComponent implements OnInit {

  private appVersion: String;

  constructor(private logger: NGXLogger) {
      this.appVersion = appVersion;
  }

  getVersion(): String {
    return this.appVersion;
  }

  ngOnInit(): void {
    this.logger.trace('HomeComponent: ngOnInit()');
  }

}
