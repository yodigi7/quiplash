import { Component, OnInit, Input, OnChanges } from '@angular/core';
import { RestProxyService } from 'src/app/services/rest-proxy.service';
import { Constants } from 'src/app/constants/constants';

@Component({
  selector: 'app-vote',
  templateUrl: './vote.component.html',
  styleUrls: ['./vote.component.css']
})
export class VoteComponent implements OnChanges {

  @Input() visible: boolean;
  @Input() gameId: number;

  readonly VOTE_COUNTDOWN: number = Constants.voteTimeout;
  readonly SHOW_VOTE_COUNTDOWN: number = Constants.voteResultsCountdown;

  prompt: string;
  leftAnswer: string;
  rightAnswer: string;
  private leftAnswerId: number;
  showVotes: boolean;
  leftScore: number;
  rightScore: number;
  countdown: number;
  private countdownInterval: NodeJS.Timer;
  private votesSubmittedInterval: NodeJS.Timer;

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
            this.countdown = this.VOTE_COUNTDOWN;
          }
        },
        errors => {
          console.error(errors);
        });
      this.countdownInterval = setInterval(this.countdownFn, 1000, this);
      this.votesSubmittedInterval = setInterval(this.allVotesSubmitted, 1000, this);
    } else {
      this.cleanUp();
    }
  }

  countdownFn(outerThis: this) {
    if (outerThis.countdown <= 0) {
      clearInterval(outerThis.countdownInterval);
      clearInterval(outerThis.votesSubmittedInterval);
      outerThis.updateScores(outerThis);
      outerThis.showVotes = true;
    } else {
      outerThis.countdown--;
    }
  }

  allVotesSubmitted(outerThis: this) {
    outerThis.restProxy.getWhetherAllVotesSubmitted(outerThis.gameId).subscribe(
      resp => {
        console.log(resp);
        console.log(resp.allVotesSubmitted);
        if (resp.allVotesSubmitted) {
          outerThis.countdown = 0;
        }
      }, 
      errors => {
        console.error(errors);
      }
    )
  }

  updateQuestionToScore(outerThis: this) {
    outerThis.restProxy.setNextToScore(outerThis.gameId).subscribe(
      resp => {
        outerThis.restProxy.getQuestionToScore(outerThis.gameId).subscribe(
          resp => {
            console.log(resp);
            if (resp.status == 200 && outerThis.leftAnswerId !== resp.body.questionAnswers[0].id) {
              outerThis.prompt = resp.body.questionAnswers[0].question
              outerThis.leftAnswer = resp.body.questionAnswers[0].answer;
              outerThis.rightAnswer = resp.body.questionAnswers[1].answer;
              outerThis.leftAnswerId = resp.body.questionAnswers[0].id;
              outerThis.showVotes = false;
              outerThis.countdown = outerThis.VOTE_COUNTDOWN;
              outerThis.countdownInterval = setInterval(outerThis.countdownFn, 1000, outerThis);
              outerThis.votesSubmittedInterval = setInterval(outerThis.allVotesSubmitted, 1000, outerThis);
            } else {
              console.error("THE LEFT ANSWER ID WAS THE SAME OR IT FAILED");
            }
          },
          errors => {
            console.error(errors);
          });
      }
    );
  }

  updateScores(outerThis: this) {
    outerThis.restProxy.getQuestionVotes(outerThis.gameId).subscribe(
      resp => {
        console.log(resp);
        if (resp.status == 200) {
          outerThis.leftScore = resp.body.questionAnswers[0].score;
          outerThis.rightScore = resp.body.questionAnswers[1].score;
          outerThis.showVotes = true;
          // If has more to score then update question to score else go to next round
          outerThis.restProxy.getWhetherMoreToVoteOn(outerThis.gameId).subscribe(
            resp => {
              console.log(resp);
              console.log(resp.moreToVoteOn);
              if (resp.moreToVoteOn) {
                setTimeout(outerThis.updateQuestionToScore, outerThis.SHOW_VOTE_COUNTDOWN * 1000, outerThis);
              } else {
                outerThis.restProxy.getRound(outerThis.gameId).subscribe(
                  resp => {
                    console.log(resp);
                    if (resp.body.round === 3) {
                      outerThis.restProxy.getFinalResults(outerThis.gameId).subscribe();
                    } else {
                      outerThis.restProxy.startRound(outerThis.gameId).subscribe();
                    }
                  },
                  errors => {
                    console.error(errors);
                  }
                );
              }
            },
            errors => {
              console.error(errors);
            });
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
    this.countdown = null;
    clearInterval(this.countdownInterval);
    clearInterval(this.votesSubmittedInterval);
  }

}
