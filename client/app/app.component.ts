import {Component} from 'angular2/core';
import {MyService} from "./myservice";
import {RouteConfig} from "angular2/router";
import {ROUTER_DIRECTIVES} from "angular2/router";
import {Component1} from "./component1.component";
import {Component2} from "./component2.component";
import {AuthenticationService} from "./authentication.service";

@Component({
  selector: 'my-app',
  template: `
    <h2>12323</h2>
    <button class="btn btn-warning" (click)="auth()">auth</button><br/>
    <button class="btn btn-warning" (click)="logoff()">logoff</button><br/>
    <span>{{x}}</span>
    <input (keyup.enter)="addName(myName)" #myName/>
    <button (click)="addName(myName)">add</button>
    <br/>
    <ol>
      <li *ngFor="#name of list">{{name.name}} <button (click)="delete(name.id)">x</button></li>
    </ol>

    <section>
      <form (ngSubmit)="submitted(form)" #form="ngForm">
        <div>
          <label>Name:</label>
          <input ngControl="name" id="firstname"/>
        </div>
        <div>
          <label>Lastname:</label>
          <input ngControl="lastname" id="lastname"/>
        </div>
        <button type="submit">Send</button>
      </form>
    </section>

    <section>
      <a [routerLink]="['Component1']">Home</a>
      <a [routerLink]="['Component2']">Component2</a>
      <router-outlet></router-outlet>
    </section>
    `,
  directives: [ROUTER_DIRECTIVES]
})
@RouteConfig([
  {path: '/', name: 'Component1', component: Component1},
  {path: '/component2', name: 'Component2', component: Component2}
])
export class AppComponent {
  list = Array<Name>();

  constructor(private _myService: MyService, private _authService: AuthenticationService) {}

  addName(name) {
    var newName = <Name>{name: name.value, id: this.list.length + 1};
    this.list.push(newName);
    name.value = '';
    this._myService.saySomething();
  }

  delete(id: number) {
    var idx = this.list.findIndex((elt: Name) => elt.id == id);
    this.list.splice(idx, 1);
  }

  submitted(form) {
    console.log(form.value);
  }

  auth() {
    this._authService.authenticate('god', 'Dsch1982');
  }

  logoff() {
    this._authService.logoff();
  }
}

interface Name {
  id: number
  name: string
}