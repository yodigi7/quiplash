import { Component, OnInit, Input, OnChanges, OnDestroy } from '@angular/core';
import { RestProxyService } from 'src/app/services/rest-proxy.service';
import { element } from '@angular/core/src/render3';

@Component({
  selector: 'app-join',
  templateUrl: './join.component.html',
  styleUrls: ['./join.component.css']
})
export class JoinComponent implements OnChanges, OnDestroy {

  @Input() gameId: number;
  @Input() visible: boolean;
  contenders: Array<string> = new Array<string>();
  private interval: NodeJS.Timer;

  constructor(private restProxy: RestProxyService) { }

  ngOnChanges() { 
    if (this.gameId) {
      this.updateContenderNames(this);
      this.constantUpdateContenderNames();
    }
    if (!this.visible) {
      this.cleanUp();
    }
  }

  ngOnDestroy() {
    this.cleanUp();
  }

  cleanUp(): void {
    clearInterval(this.interval);
  }

  constantUpdateContenderNames(): void {
    this.interval = setInterval(this.updateContenderNames, 5000, this)
  }

  updateContenderNames(outerThis: this): void {
    outerThis.restProxy.getContenderNames(outerThis.gameId)
    .subscribe(
      resp => {
        console.log(resp);
        console.log(outerThis.contenders);
        if (resp.status == 200 && outerThis.contenders.length !== resp.body.names.length) {
          resp.body.names.forEach(element => {
            if (!outerThis.contenders.includes(element)) {
              outerThis.contenders.push(element);
            }
          });
        }
      },
      errors => {
        console.error(errors);
      });
  }

}
