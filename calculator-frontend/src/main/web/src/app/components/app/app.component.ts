import {Component, ViewEncapsulation} from '@angular/core';

@Component({
    selector: 'app',
    templateUrl: 'app.component.html',
    styleUrls: ['./app.component.scss'],
    encapsulation: ViewEncapsulation.None /*turns on styles for 'app' tag*/
})
export class AppComponent {
    isLightTheme = false; // alternative theme is disabled by default

    toggleTheme() {
        // TODO save flag into user properties
        this.isLightTheme = !this.isLightTheme;
        // apply alternative theme to the body to affect overlay-based components (e.g. menu, dialog, etc.)
        if (this.isLightTheme) {
            document.body.classList.add('uniportal-light-theme');
        } else {
            document.body.classList.remove('uniportal-light-theme');
        }
    }
}