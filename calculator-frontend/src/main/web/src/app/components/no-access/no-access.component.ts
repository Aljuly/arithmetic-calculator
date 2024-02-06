import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NGXLogger } from 'ngx-logger';

@Component({
  selector: 'app-no-access',
  templateUrl: './no-access.component.html',
  styleUrls: ['./no-access.component.scss']
})
export class NoAccessComponent implements OnInit {
  requestedUrl!: string;

  constructor(private route: ActivatedRoute,
              private logger: NGXLogger) {
  }

  ngOnInit() {
      this.logger.trace('NoAccessComponent: welcome on access denied page, thug!');
      this.route.queryParamMap.subscribe(params => {
          this.requestedUrl = params.get('requestedUrl') + '';
          this.logger.trace('NoAccessComponent: requestedUrl ', this.requestedUrl);
      });
  }
}
