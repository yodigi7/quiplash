import { Component, OnInit, AfterViewChecked, AfterContentInit, Input, Output, EventEmitter, AfterContentChecked, OnChanges } from '@angular/core';
import { RestProxyService } from 'src/app/services/rest-proxy.service';
import { FormsModule, FormGroup, FormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-answer',
  templateUrl: './answer.component.html',
  styleUrls: ['./answer.component.css']
})
export class AnswerComponent implements OnChanges {

  messageForm: FormGroup;
  @Input() gameId: number;
  @Input() name: string;
  @Input() visible: boolean;

  @Output() emmiter = new EventEmitter<boolean>();

  questionsToAnswer: Array<string> = [];
  private questionIds: Array<number> = [];
  currentQuestion: string;
  error = false;

  constructor(private restProxy: RestProxyService,
              private formBuilder: FormBuilder) {
    this.messageForm = formBuilder.group({
      answer: ['', Validators.required]
    })
  }

  ngOnChanges() {
    if (this.visible) {
      this.restProxy.getQuestions(this.gameId, this.name)
        .subscribe(
          resp => {
            resp.body.questions.forEach(question => {
              this.questionsToAnswer.push(question.question);
              this.questionIds.push(question.id);
              console.log(question.question);
            });
            this.currentQuestion = this.questionsToAnswer[0];
            console.log('Successful retrieval of questions');
            console.log(resp);
            // TODO: should probably remove this eventually since this should never happen
            if (this.questionsToAnswer.length <= 0) {
              this.emmiter.emit(true);
            }
          },
          errors => {
            console.error(errors);
          });
    }
  }

  submitAnswer() {
    console.log(this.messageForm.controls.answer.value);
    this.restProxy.submitAnswer(this.gameId, this.name, this.messageForm.controls.answer.value, this.questionIds[0])
      .subscribe(
        resp => {
          this.messageForm.controls["answer"].patchValue("");
          this.questionIds.shift();
          this.questionsToAnswer.shift();
          this.currentQuestion = this.questionsToAnswer[0];
          this.error = false;
          if (this.questionsToAnswer.length <= 0) {
            this.emmiter.emit(true);
          }
        },
        errors => {
          this.error = true;
          console.log(errors);
        });
  }

}
