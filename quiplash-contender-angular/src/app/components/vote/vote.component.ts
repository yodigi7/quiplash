import { Component, OnInit, Input, Output, EventEmitter, OnDestroy, OnChanges } from '@angular/core';
import { RestProxyService } from 'src/app/services/rest-proxy.service';

@Component({
  selector: 'app-vote',
  templateUrl: './vote.component.html',
  styleUrls: ['./vote.component.css']
})
export class VoteComponent implements OnChanges, OnDestroy {

  @Input() gameId: number;
  @Input() name: string;
  @Input() visible: boolean;

  @Output() emitter = new EventEmitter<boolean>();

  error = false;
  leftAnswerId: number;
  rightAnswerId: number;
  voted = false;
  private interval: NodeJS.Timer;
  private votePhase = 'voting';

  constructor(private restProxy: RestProxyService) { }

  ngOnChanges() {
    if (this.visible){
      this.updateVote();
    }
  }

  ngOnDestroy() {
    if (this.interval) {
      clearInterval(this.interval);
    }
  }

  updateVote() {
    this.waitForUpdate(this);
    this.interval = setInterval(this.waitForUpdate, 2000, this)
  }

  waitForUpdate(outerThis: this) {
    console.log(outerThis.leftAnswerId);
    outerThis.restProxy.getQuestionToScore(outerThis.gameId)
    .subscribe(
      resp => {
        console.log(resp);
        if (resp.status == 200) {
          console.log(resp.body.questionAnswers[0].id);
          console.log(outerThis.leftAnswerId);
          if (resp.body.questionAnswers[0].id !== outerThis.leftAnswerId) {
            outerThis.leftAnswerId = resp.body.questionAnswers[0].id;
            outerThis.rightAnswerId = resp.body.questionAnswers[1].id;
            outerThis.voted = false;
          }
        }
      },
      errors => {
        console.error(errors);
      });
  }

  endFunction() {
    if (this.interval) {
      clearInterval(this.interval);
    }
    this.emitter.emit(true);
  }

  voteLeft() {
    this.restProxy.submitVote(this.gameId, this.name, this.leftAnswerId).subscribe();
    console.log('Voted successfully');
    this.voted = true;
  }

  voteRight() {
    this.restProxy.submitVote(this.gameId, this.name, this.rightAnswerId).subscribe();
    console.log('Voted successfully');
    this.voted = true;
  }

}
