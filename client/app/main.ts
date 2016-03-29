import {bootstrap}    from 'angular2/platform/browser';
import {AppComponent} from './app.component';
import {MyService} from "./myservice";
import {ROUTER_PROVIDERS} from "angular2/router";
import {HttpService} from "./http.service";
import {HTTP_PROVIDERS} from "angular2/http";

bootstrap(AppComponent, [ROUTER_PROVIDERS, HTTP_PROVIDERS, MyService, HttpService]);
