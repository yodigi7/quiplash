import { Component, OnInit, Input, OnChanges } from '@angular/core';
import { RestProxyService } from 'src/app/services/rest-proxy.service';

@Component({
  selector: 'app-wait',
  templateUrl: './wait.component.html',
  styleUrls: ['./wait.component.css']
})
export class WaitComponent implements OnChanges {

  @Input() gameId: number;
  @Input() showStartButton: boolean = false;

  constructor(private restProxy: RestProxyService) { }

  ngOnChanges() {
  }

  startGame() {
    this.restProxy.startGame(this.gameId).subscribe();
  }

}
