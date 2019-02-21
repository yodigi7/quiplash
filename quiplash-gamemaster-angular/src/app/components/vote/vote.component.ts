import { Component, OnInit, Input, OnChanges } from '@angular/core';
import { RestProxyService } from 'src/app/services/rest-proxy.service';

@Component({
  selector: 'app-vote',
  templateUrl: './vote.component.html',
  styleUrls: ['./vote.component.css']
})
export class VoteComponent implements OnChanges {

  @Input() visible: boolean;
  @Input() gameId: number;

  prompt: string;
  leftAnswer: string;
  rightAnswer: string;
  private leftAnswerId: number;
  showVotes: boolean;
  leftScore: number;
  rightScore: number;

  constructor(private restProxy: RestProxyService) { }

  ngOnChanges() {
    if (this.visible) {
      this.restProxy.getQuestionToScore(this.gameId).subscribe(
        resp => {
          console.log(resp);
          if (resp.status == 200 && this.leftAnswerId !== resp.body.questionAnswers[0].id) {
            this.prompt = resp.body.questionAnswers[0].question
            this.leftAnswer = resp.body.questionAnswers[0].answer;
            this.rightAnswer = resp.body.questionAnswers[1].answer;
            this.leftAnswerId = resp.body.questionAnswers[0].id;
            this.showVotes = false;
          }
        },
        errors => {
          console.error(errors);
        });
    } else {
      this.cleanUp();
    }
  }

  updateQuestionToScore() {
    // TODO: finish this up
  }

  updateScores() {
    this.restProxy.getQuestionVotes(this.gameId).subscribe(
      resp => {
        console.log(resp);
        if (resp.status == 200) {
          this.leftScore = resp.body.questionAnswers[0].score;
          this.rightScore = resp.body.questionAnswers[1].score;
          this.showVotes = true;
        }
      },
      errors => {
        console.error(errors);
      });
  }

  cleanUp() {
    this.leftAnswer = null;
    this.leftAnswerId = null;
    this.rightAnswer = null;
    this.leftScore = null;
    this.rightScore = null;
  }

}
