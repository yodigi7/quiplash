import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-final-results',
  templateUrl: './final-results.component.html',
  styleUrls: ['./final-results.component.css']
})
export class FinalResultsComponent implements OnInit {

  @Input() visible: boolean;
  @Input() gameId: number;

  constructor() { }

  ngOnInit() {
  }

}
