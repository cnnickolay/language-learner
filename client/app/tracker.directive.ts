import {Input} from "angular2/core";
import {Output} from "angular2/core";
import {Directive} from "angular2/core";
import {OnChanges} from "angular2/core";
import {ElementRef} from "angular2/core";
import {HostListener} from "angular2/core";
import {EventEmitter} from "angular2/core";
import {HostBinding} from "angular2/core";

@Directive({
  selector: '[tracker]'
})
export class Tracker implements OnChanges {

  @Input() tracker;
  @Input() trackerCurrentIdx;
  @Output() trackerFocused:EventEmitter<any> = new EventEmitter();

  @HostListener('focus')
  focused() {
    this.trackerFocused.emit(null);
  }

  constructor (private _elt: ElementRef) {}

  ngOnChanges(changes) {
    if (this.tracker == this.trackerCurrentIdx) {
      this._elt.nativeElement.focus();
      console.log('Current index is ' + this.trackerCurrentIdx);
    }
  }

}