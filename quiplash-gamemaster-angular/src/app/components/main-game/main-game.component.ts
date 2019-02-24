import { Component, OnInit, OnDestroy } from '@angular/core';
import { RestProxyService } from 'src/app/services/rest-proxy.service';
import { Constants } from 'src/app/constants/constants';

@Component({
  selector: 'app-main-game',
  templateUrl: './main-game.component.html',
  styleUrls: ['./main-game.component.css'],
  providers: [ RestProxyService ]
})
export class MainGameComponent implements OnInit, OnDestroy {

  readonly answerPhase = Constants.answerPhase;
  readonly joinPhase = Constants.joinPhase;
  readonly votePhase = Constants.votePhase;
  readonly waitPhase = Constants.waitPhase;
  readonly finalPhase = Constants.finalPhase;
  roundNum = 1;
  gameId: number;
  phase: string = this.joinPhase;
  realPhase: string = this.joinPhase;
  private interval: NodeJS.Timer;

  private nextPhase: string;
  private countdownInterval: NodeJS.Timer;

  constructor(private restProxy: RestProxyService) { }

  ngOnInit() {
    this.initGame(this);
  }

  ngOnDestroy() {
    this.cleanUp();
  }

  cleanUp(): void {
    clearInterval(this.interval);
  }

  initGame(outerThis: this) {
    outerThis.phase = outerThis.joinPhase;
    outerThis.realPhase = outerThis.joinPhase;
    outerThis.restProxy.initGame()
      .subscribe(
        resp => {
          console.log(resp);
          if (resp.status == 200) {
            outerThis.gameId = resp.body.gameId;
            outerThis.updatePhaseStart();
          }
        }, 
        errors => {
          outerThis.initGame(outerThis)
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
          outerThis.realPhase = resp.body.phase;
          clearInterval(outerThis.countdownInterval);
          switch(outerThis.phase) {
            case outerThis.finalPhase: {
              console.log(outerThis.finalPhase);
              setTimeout(outerThis.initGame, 30000, outerThis);
            }
            case outerThis.answerPhase: {
              outerThis.restProxy.getRound(outerThis.gameId).subscribe(
                resp => {
                  console.log(resp);
                  outerThis.roundNum = resp.body.round;
                }
              );
              // setTimeout(outerThis.goToNextPhase, 60000, outerThis, outerThis.startVoting);
              console.log(outerThis.answerPhase);
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
