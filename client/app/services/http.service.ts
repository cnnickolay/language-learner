import {Injectable} from "angular2/core";
import {Http} from "angular2/http";
import {Observable} from "rxjs/Observable";
import {Response} from "angular2/http";

@Injectable()
export class HttpService {
  constructor (private _http: Http) {}

  getMediaGroups(): Observable<Response> {
    return this._http.get('http://localhost:9000/mediaGroups');
  }
}