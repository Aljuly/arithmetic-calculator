import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NGXLogger } from 'ngx-logger';

import { User } from '../../../model/User';
import { UserService } from 'src/app/services/user.service';

@Component({
    selector: 'app-member-view',
    templateUrl: './member-view.component.html',
    styleUrls: ['./member-view.component.scss']
})
export class MemberViewComponent implements OnInit {

    user!: User;

    isAvatarExist = false;
    avatar = '/assets/img/unknown-person.png';

    constructor(
        private route: ActivatedRoute,
        private userService: UserService,
        private logger: NGXLogger,
    ) { }

    ngOnInit() {
        this.logger.trace('MemberViewComponent: ngOnInit()');
        this.route.params.subscribe(parameter => {
            this.userService.getByUsername(parameter['login']).subscribe(user => {
                this.user = user;
                if (this.user.avatar.length > 0) {
                    this.avatar =  this.user.avatar;
                }
            });
        });
    }

}
