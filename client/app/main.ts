import {bootstrap}    from 'angular2/platform/browser';
import {AppComponent} from './app.component';
import {MyService} from "./myservice";
import {ROUTER_PROVIDERS} from "angular2/router";
import {HttpService} from "./services/http.service";
import {HTTP_PROVIDERS} from "angular2/http";
import {AuthenticationService} from "./services/authentication.service";
import {RequestOptions} from "angular2/http";
import {DefaultOptions} from "./services/baserequest";
import {provide} from "angular2/core";
import {Http} from "angular2/http";

bootstrap(AppComponent,
  [
    ROUTER_PROVIDERS, HTTP_PROVIDERS, MyService, HttpService,
    provide(AuthenticationService, {
      useFactory: (http, defaultOptions) => new AuthenticationService(http, defaultOptions),
      deps: [Http, RequestOptions]
    }),
    provide(RequestOptions, {
      useClass: DefaultOptions
    })
  ]);
