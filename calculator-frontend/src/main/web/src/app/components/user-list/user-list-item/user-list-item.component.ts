import {Component, Input} from '@angular/core';
import {User} from '../../../model/User';

@Component({
    selector: 'app-user-list-item',
    template: '<div>{{ user.id }}: {{ user.login }}</div> '
})
export class UserListItemComponent {
    @Input() user!: User;
}

