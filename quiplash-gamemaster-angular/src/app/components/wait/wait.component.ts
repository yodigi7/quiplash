import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-wait',
  templateUrl: './wait.component.html',
  styleUrls: ['./wait.component.css']
})
export class WaitComponent {

  @Input() gameId: number;
  @Input() visible: boolean;  

  constructor() { }
}
