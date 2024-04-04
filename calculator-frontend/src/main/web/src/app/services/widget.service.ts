import { HttpHeaders, HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { WidgetData } from '../components/app-contact-me/widget-data';

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json;charset=utf-8'})
};

@Injectable({
  providedIn: 'root'
})
export class WidgetService {
  private serverURL = 'http://backoffice-service:8888';

  constructor(private http: HttpClient) {
  }

  postData(messageData: WidgetData): Observable<string> {
      const body = messageData;
      return this.http.post<string>(this.serverURL, body, httpOptions);
  }
}
