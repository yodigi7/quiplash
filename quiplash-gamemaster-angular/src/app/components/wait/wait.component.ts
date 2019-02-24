import { Component, OnInit, Input } from '@angular/core';
import { RestProxyService } from 'src/app/services/rest-proxy.service';

@Component({
  selector: 'app-wait',
  templateUrl: './wait.component.html',
  styleUrls: ['./wait.component.css']
})
export class WaitComponent {

  @Input() gameId: number;
  @Input() visible: boolean;

  constructor(private restProxy: RestProxyService) { }
}
