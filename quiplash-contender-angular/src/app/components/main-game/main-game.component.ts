import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { RestProxyService } from '../../services/rest-proxy.service'
import { InternalJoinGameDto } from 'src/app/dto/InternalJoinGameDto';
import { Subscription } from 'rxjs';
import { VoteComponent } from '../vote/vote.component';
import { WaitComponent } from '../wait/wait.component';
import { JoinGameComponent } from '../join-game/join-game.component';
import { AnswerComponent } from '../answer/answer.component';
import { GameIdNameDto } from 'src/app/dto/GameIdNameDto';

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
  private roundNum = 1;

  name: string;
  gameId: number;
  phase = this.joinPhase;
  realPhase = this.joinPhase;
  private interval: NodeJS.Timer;
  private updateRealPhaseInterval: NodeJS.Timer;

  constructor(private restProxy: RestProxyService ) { }

  joined(info) {
    this.setGameIdName(info);
    this.wait(this.answerPhase, this);
    this.updateRealPhaseInterval = setInterval(this.updateRealPhase, 3000, this);
  }
  
  answered() {
    this.wait(this.votePhase, this);
  }

  voted() {
    this.roundNum++;
    if (this.roundNum < 3) {
      this.wait(this.answerPhase, this);
    } else {
      this.reset();
    }
  }

  reset () {
    this.roundNum = 1;
    this.phase = this.joinPhase
    this.name = null;
    this.gameId = null;
  }

  setGameIdName(info) {
    this.name = info.name;
    this.gameId = info.gameId;
    console.log(`Game id: ${this.gameId}, name: ${this.name}`);
  }

  wait(targetPhase: string, outerThis: this) {
    outerThis.phase = outerThis.waitPhase;
    outerThis.interval = setInterval(outerThis.repeatedWait, 2000, targetPhase, outerThis);
  }

  repeatedWait(targetPhase: string, outerThis: this) {
    outerThis.restProxy.getPhase(outerThis.gameId).subscribe(
      resp => {
        outerThis.realPhase = resp.body.phase;
        if (resp.status == 200 && resp.body.phase === targetPhase) {
          clearInterval(outerThis.interval);
          outerThis.phase = resp.body.phase;
        } else if (resp.status == 200 && resp.body.phase === outerThis.finalPhase) {
          console.log('final phase');
        }
      },
      errors => {
        console.log(errors)
      });
  }

  ngOnInit() { }

  updateRealPhase(outerThis: this) {
    outerThis.restProxy.getPhase(outerThis.gameId).subscribe(
      resp => {
        outerThis.realPhase = resp.body.phase;
        if (resp.status == 200 && outerThis.phase !== outerThis.waitPhase) {
          clearInterval(outerThis.interval);
          outerThis.phase = resp.body.phase;
        } else if (resp.status == 200 && resp.body.phase === outerThis.finalPhase) {
          console.log('final phase');
        }
      },
      errors => {
        console.log(errors)
      });
  }

  ngOnDestroy() { 
    if (this.interval) {
      clearInterval(this.interval);
    }
    if (this.updateRealPhaseInterval) {
      clearInterval(this.updateRealPhaseInterval);
    }
  }

}
