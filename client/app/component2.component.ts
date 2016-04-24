import {Component} from "angular2/core";
import {HttpService} from "./services/http.service";
import {OnInit} from "angular2/core";

@Component({
  selector: 'component2',
  template: `
    <h1>Component2</h1>
    <ol>
      <li *ngFor="#mediaGroup of mediaGroups">{{mediaGroup.name}}</li>
    </ol>
  `
})
export class Component2 implements OnInit {

  mediaGroups: {};

  constructor(private httpService: HttpService) { }

  ngOnInit() {
    this.httpService.getMediaGroups().subscribe(
      response => { this.mediaGroups = response.json();},
      error => console.log(error)
    )
  }

}