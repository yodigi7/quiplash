import { Component, OnInit, Output, EventEmitter, Input, OnChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms'
import { RestProxyService } from '../../services/rest-proxy.service';

@Component({
  selector: 'app-join-game',
  templateUrl: './join-game.component.html',
  styleUrls: ['./join-game.component.css']
})
export class JoinGameComponent {

  @Output() emitter = new EventEmitter<any>();

  messageForm: FormGroup;
  submitted = false;
  errorMessage: string;

  constructor(private formBuilder: FormBuilder, 
              private restProxyService: RestProxyService) {
    this.messageForm = this.formBuilder.group({
      gameId: [null as number, Validators.required],
      name: [null as string, Validators.required]
    })
  }

  onSubmit() {
    this.submitted = true;

    if (this.messageForm.invalid) {
      return;
    }

    let gameId = this.messageForm.controls.gameId.value;
    let name = this.messageForm.controls.name.value;
    
    this.restProxyService.joinGame(gameId, name)
                         .subscribe(
                          // If has data
                          resp => {
                            if (resp.status == 200) {
                              console.log('Joined successfully');
                              this.errorMessage = '';
                              this.emitter.emit({
                                'gameId': gameId,
                                'name': name
                              });
                            }
                          },
                          // If has error messages
                          error => {
                            console.error("Failed to join");
                            this.errorMessage = "Failed to add, try again";
                          });
  }
}
