import { Component, OnInit } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { User } from 'src/app/model/User';
import { LocalStorageService } from 'src/app/services/local-storage.service';

@Component({
  selector: 'app-profile-home',
  templateUrl: './profile-home.component.html',
  styleUrls: ['./profile-home.component.scss']
})
export class ProfileHomeComponent implements OnInit {
  currentUser!: User;

  constructor(
      private localService: LocalStorageService,
      private logger: NGXLogger
  ) {}

  ngOnInit() {
      this.logger.trace('ProfileHomeComponent: ngOnInit()');
      this.currentUser = this.localService.readCurrentUser();
      this.logger.debug('ProfileHomeComponent: welcome, ', this.currentUser.login);
  }
}
