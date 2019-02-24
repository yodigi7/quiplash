import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class RestProxyService {
  readonly endpoint = environment.baseUrl;
  readonly restHeader = new HttpHeaders({
      'Content-Type':  'application/json'
    });

  constructor(private http: HttpClient) { }

  initGame(): any {
    let url = `${this.endpoint}/init`;
    console.log('Sending request to url: ' + url);
    return this.http.post(url, null, { "headers": this.restHeader, observe: "response"});
  }

  endGame(gameId: number) {
    let url = `${this.endpoint}/game/${gameId}/end`;
    console.log('Sending request to url: ' + url);
    return this.http.post(url, null, { "headers": this.restHeader });
  }

  startRound(gameId: number) {
    let url = `${this.endpoint}/game/${gameId}/start-round`;
    console.log('Sending request to url: ' + url);
    return this.http.post(url, null, { "headers": this.restHeader });
  }

  startVoting(gameId: number) {
    let url = this.endpoint + `/game/${gameId}/start-voting`;
    console.log('Sending request to url: ' + url);
    return this.http.post(url, null, { "headers": this.restHeader });
  }

  setNextToScore(gameId: number) {
    let url = this.endpoint + `/game/${gameId}/set-next-to-score`;
    console.log('Sending request to url: ' + url);
    return this.http.post(url, null, { "headers": this.restHeader });
  }

  getWhetherAllVotesSubmitted(gameId: number): any {
    let url = this.endpoint + `/game/${gameId}/all-votes-submitted`;
    console.log('Sending request to url: ' + url);
    return this.http.get(url, { "headers": this.restHeader });  
  }

  getWhetherMoreToVoteOn(gameId: number): any {
    let url = this.endpoint + `/game/${gameId}/more-to-vote-on`;
    console.log('Sending request to url: ' + url);
    return this.http.get(url, { "headers": this.restHeader });
  }

  getQuestionVotes(gameId: number): any {
    let url = this.endpoint + `/game/${gameId}/question-votes`;
    console.log('Sending request to url: ' + url);
    return this.http.get(`${this.endpoint}/game/${gameId}/question-votes`, { "headers": this.restHeader, observe: "response"});
  }

  getQuestionToScore(gameId: number): any {
    let url = this.endpoint + `/game/${gameId}/question-to-score`;
    console.log('Sending request to url: ' + url);
    return this.http.get(url, { "headers": this.restHeader, observe: "response"});
  }

  getFinalResults(gameId: number): any {
    let url = this.endpoint + `/game/${gameId}/final-results`;
    console.log('Sending request to url: ' + url);
    return this.http.get(url, { "headers": this.restHeader, observe: "response"});
  }

  getContenderNames(gameId: number): any {
    let url = this.endpoint + `/game/${gameId}/contender-names`;
    console.log('Sending request to url: ' + url);
    return this.http.get(url, { "headers": this.restHeader, observe: "response"});
  }

  getPhase(gameId: number): any {
    let url = this.endpoint + `/game/${gameId}/phase`;
    console.log('Sending request to url: ' + url);
    return this.http.get(url, { "headers": this.restHeader, observe: "response"});
  }

  getRound(gameId: number): any {
    let url = this.endpoint + `/game/${gameId}/round-number`;
    console.log('Sending request to url: ' + url);
    return this.http.get(url, { "headers": this.restHeader, observe: "response"});
  }
}
