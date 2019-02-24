import { Component, OnInit, Input, OnChanges } from '@angular/core';
import { CLEANUP } from '@angular/core/src/render3/interfaces/view';
import { RestProxyService } from 'src/app/services/rest-proxy.service';
import { environment } from 'src/environments/environment';
import { Constants } from 'src/app/constants/constants';

@Component({
  selector: 'app-final-results',
  templateUrl: './final-results.component.html',
  styleUrls: ['./final-results.component.css']
})
export class FinalResultsComponent implements OnChanges {

  @Input() visible: boolean;
  @Input() gameId: number;

  contenders: Array<any>;

  constructor(private restProxy: RestProxyService) { }

  ngOnChanges() {
    if (this.visible) {
      this.getContenders();
    } else { 
      this.cleanUp();
    }
  }

  cleanUp() {
    this.contenders = null;
  }

  getContenders() {
    this.restProxy.getFinalResults(this.gameId)
      .subscribe(
        resp => {
          console.log(resp);
          if (resp.status === 200) {
            this.contenders = resp.body.contenders;
            setTimeout(this.restProxy.endGame(this.gameId).subscribe, Constants.finalResultsTimeout);
          }
        },
        errors => {
          console.error(errors);
        });
  }

}
