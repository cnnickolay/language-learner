import {Component} from "angular2/core";
import {ViewChild} from "angular2/core";
import {OnInit} from "angular2/core";
import {OnChanges} from "angular2/core";
import {SimpleChange} from "angular2/core";
import {Tracker} from "./tracker.directive";
import {ElementRef} from "angular2/core";

@Component({
  selector: 'component1',
  template: `
  <div>
    <h1>Audio component</h1>
    <audio #audio controls (timeupdate)="timeUpdated(audio)">
    </audio>

    <br/>
    Source audio: <input #sourceUrl value="https://www.dropbox.com/s/g5ocmx1s6qywa8j/Grammaire_en_dialogues_No02.mp3?raw=1"/>
    <button (click)="audio.src = sourceUrl.value">Play</button>
    <br/>
    Current time: {{currentTime}}

    <button (click)="clicked()">scroll</button>

    <div class="row">
      <div class="col-md-12">
        <button class="btn btn-success" (click)="skip10Seconds()">skip 10 seconds</button>

        <div class="card">
        <h3 class="card-title">Some table</h3>
          <table id="peopleTable" #peopleTable class="table" (keydown)="keyPressed($event)" tabindex="1">
          <thead class="thead-inverse">
            <tr>
              <th>Name</th>
              <th>Lastname</th>
              <th>Age</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="#person of people; #idx = index"
                [class.table-info]="idx == currentRowIdx"
                (click)="setCurrentRow(idx)" [tracker]="idx" [trackerCurrentIdx]="currentRowIdx"
                [tabindex]="idx + 2" (trackerFocused)="setCurrentRow(idx)">
              <td>{{person.name}}</td>
              <td>{{person.lastname}}</td>
              <td>{{person.age}}</td>
            </tr>
          </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
  `,
  directives: [Tracker]
})
export class Component1 {

  @ViewChild('audio') audio;
  @ViewChild('peopleTable') peopleTable;

  currentTime: number;

  people:Array<Person> = [
    {name: 'nikolai', lastname: 'cherkezishvili', age: 33},
    {name: 'nodar', lastname: 'cherkezishvili', age: 35},
    {name: 'veronika', lastname: 'bodiul', age: 29},
    {name: 'georgy', lastname: 'cherkezishvili', age: 15},
    {name: '1nikolai', lastname: 'cherkezishvili', age: 33},
    {name: '1nodar', lastname: 'cherkezishvili', age: 35},
    {name: '1veronika', lastname: 'bodiul', age: 29},
    {name: '1georgy', lastname: 'cherkezishvili', age: 15},
    {name: '2nikolai', lastname: 'cherkezishvili', age: 33},
    {name: '2nodar', lastname: 'cherkezishvili', age: 35},
    {name: '2veronika', lastname: 'bodiul', age: 29},
    {name: '2georgy', lastname: 'cherkezishvili', age: 15},
    {name: '3nikolai', lastname: 'cherkezishvili', age: 33},
    {name: '3nodar', lastname: 'cherkezishvili', age: 35},
    {name: '3veronika', lastname: 'bodiul', age: 29},
    {name: '3georgy', lastname: 'cherkezishvili', age: 15},
    {name: '4nikolai', lastname: 'cherkezishvili', age: 33},
    {name: '4nodar', lastname: 'cherkezishvili', age: 35},
    {name: '4veronika', lastname: 'bodiul', age: 29},
    {name: '4georgy', lastname: 'cherkezishvili', age: 15},
    {name: '5nikolai', lastname: 'cherkezishvili', age: 33},
    {name: '5nodar', lastname: 'cherkezishvili', age: 35},
    {name: '5veronika', lastname: 'bodiul', age: 29},
    {name: '5georgy', lastname: 'cherkezishvili', age: 15}
  ];

  currentRowIdx = 0;

  timeUpdated() {
    this.currentTime = parseFloat(this.audio.nativeElement.currentTime);
  }

  skip10Seconds() {
    this.audio.nativeElement.currentTime = this.audio.nativeElement.currentTime + 10;
  }

  keyPressed(e) {
    if (e.code == 'ArrowUp' && this.currentRowIdx >= 1) {
      e.preventDefault();
      this.setCurrentRow(this.currentRowIdx - 1);
    } else if (e.code == 'ArrowDown' && this.currentRowIdx < this.people.length - 1) {
      e.preventDefault();
      this.setCurrentRow(this.currentRowIdx + 1);
    }
  }

  setCurrentRow(idx) {
    this.currentRowIdx = idx;
  }

}

interface Person {
  name: string;
  lastname: string;
  age: number;
}