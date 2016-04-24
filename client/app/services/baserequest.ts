import {BaseRequestOptions} from "angular2/http";
import {Headers} from "angular2/http";
import {AuthenticationService} from "./authentication.service";
import {Injectable} from "angular2/core";
import {Injector} from "angular2/core";
import {ResolvedProvider} from "angular2/core";

export class DefaultOptions extends BaseRequestOptions {
  headers = new Headers();

  constructor() {
    this.headers.append('Content-Type', 'application/json');
  }

  setAuthToken(token) {
    console.log('settings auth token ' + token);
    this.headers.append('token', token);
  }

  unsetAuthToken() {
    this.headers.delete('token');
  }
}