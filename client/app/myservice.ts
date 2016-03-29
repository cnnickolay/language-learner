import {Injectable} from "angular2/core";

@Injectable()
export class MyService {

  saySomething() {
    console.log("hi");
  }

}