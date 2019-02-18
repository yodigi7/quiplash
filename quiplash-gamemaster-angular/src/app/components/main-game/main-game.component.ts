import { Component, OnInit, OnDestroy } from '@angular/core';
import { RestProxyService } from 'src/app/services/rest-proxy.service';

@Component({
  selector: 'app-main-game',
  templateUrl: './main-game.component.html',
  styleUrls: ['./main-game.component.css'],
  providers: [ RestProxyService ]
})
export class MainGameComponent implements OnInit, OnDestroy {

  readonly answerPhase = 'answering questions';
  readonly joinPhase = 'joining';
  readonly votePhase = 'voting';
  readonly waitPhase = 'waiting';
  readonly finalPhase = 'final results';
  roundNum = 1;
  gameId: number;
  phase = this.joinPhase;
  private interval: NodeJS.Timer;

  private nextPhase: string;
  private countdownInterval: NodeJS.Timer;

  constructor(private restProxy: RestProxyService) { }

  ngOnInit() { 
    this.initGame();
  }

  ngOnDestroy() {
    this.cleanUp();
  }

  cleanUp(): void {
    clearInterval(this.interval);
  }

  initGame() {
    this.restProxy.initGame()
      .subscribe(
        resp => {
          console.log(resp);
          if (resp.status == 200) {
            this.gameId = resp.body.gameId;
            this.updatePhaseStart();
          }
        }, 
        errors => {
          this.initGame()
          console.error(errors);
        });
  }

  updatePhaseStart() {
    this.interval = setInterval(this.updatePhase, 5000, this);
  }

  goToNextPhase(outerThis: this, updatePhaseFunction: Function) {
    if (outerThis.phase !== outerThis.nextPhase) {
      outerThis.phase = outerThis.nextPhase;

    }
  }

  startVoting(outerThis: this) {
    outerThis.restProxy.startVoting(outerThis.gameId).subscribe();
  }

  updatePhase(outerThis: this) {
    outerThis.restProxy.getPhase(outerThis.gameId).subscribe(
      resp => {
        if (resp.status == 200 && resp.body.phase !== outerThis.phase) {
          outerThis.phase = resp.body.phase;
          switch(outerThis.phase){
            case outerThis.finalPhase: {
              console.log('final phase');
            }
            case outerThis.answerPhase: {
              outerThis.countdownInterval = setInterval(outerThis.goToNextPhase, 60000, outerThis, outerThis.startVoting)
              console.log('answer phase');
            }
            default: {
              console.log('default');
            }
          }
        }
      },
      errors => {
        console.log(errors)
      });
  }

}
