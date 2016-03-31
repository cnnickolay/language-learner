import {Injectable} from "angular2/core";
import {Http} from "angular2/http";
import {DefaultOptions} from "./baserequest";
import {RequestOptions} from "angular2/http";

@Injectable()
export class AuthenticationService {

  constructor(private _http:Http, private _requestOptions:DefaultOptions) {
  }

  authenticate(login, password) {
    this._http.post('http://localhost:9000/auth', JSON.stringify({login: login, password: password}))
      .subscribe(res => {
        this._requestOptions.setAuthToken(res.json().token);
      });
  }

  logoff() {
    this._http.delete('http://localhost:9000/auth') .subscribe(_ => {
      this._requestOptions.unsetAuthToken();
    });
  }

}