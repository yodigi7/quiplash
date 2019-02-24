import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment'
import { JoinGameDto } from '../dto/JoinGameDto';
import { AnswerDto } from '../dto/AnswerDto';
import { VoteDto } from '../dto/VoteDto';

@Injectable({
  providedIn: 'root'
})
export class RestProxyService {
  readonly endpoint = environment.baseRestUrl;
  readonly restHeader = new HttpHeaders({
      'Content-Type':  'application/json'
    });

  constructor(private http: HttpClient) { }

  joinGame(gameId: number, name: string) {
    let url = `${this.endpoint}/game/${gameId}/join`;
    var request = new JoinGameDto(name);
    request.name = name;
    console.log('Sending request: ' + JSON.stringify(request) + ' to url: ' + url);
    return this.http.post(url, JSON.stringify(request), { "headers": this.restHeader, observe: "response"});
  }

  getPhase(gameId: number): any {
    let url = this.endpoint + `/game/${gameId}/phase`;
    console.log('Sending request to url: ' + url);
    return this.http.get(url, { "headers": this.restHeader, observe: "response"});
  }

  getQuestionToScore(gameId: number): any {
    let url = this.endpoint + `/game/${gameId}/question-to-score`;
    console.log('Sending request to url: ' + url);
    return this.http.get(url, { "headers": this.restHeader, observe: "response"});
  }

  getQuestions(gameId: number, name: string): any {
    let url = this.endpoint + `/game/${gameId}/name/${name}/questions`;
    console.log('Sending request to url: ' + url);
    return this.http.get(url, { "headers": this.restHeader, observe: "response"});
  }

  submitAnswer(gameId: number, name: string, answer: string, questionAnswerId: number) {
    let url = this.endpoint + `/game/${gameId}/name/${name}/answer`;
    let requestBody = new AnswerDto(questionAnswerId, answer);
    console.log('Sending request to url: ' + url);
    return this.http.post(url, JSON.stringify(requestBody), { "headers": this.restHeader, observe: "response"});
  }

  submitVote(gameId: number, name: string, questionAnswerId: number) {
    let url = this.endpoint + `/game/${gameId}/name/${name}/vote`;
    let requestBody = new VoteDto(questionAnswerId);
    console.log('Sending request to url: ' + url);
    return this.http.post(url, JSON.stringify(requestBody), { "headers": this.restHeader, observe: "response"});
  }

  startGame(gameId: number) {
    let url = this.endpoint + `/game/${gameId}/start-game`;
    console.log('Sending request to url: ' + url);
    return this.http.post(url, null, { "headers": this.restHeader });
  }
}
