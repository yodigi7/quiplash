import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { JoinGameComponent } from './components/join-game/join-game.component';
import { VoteComponent } from './components/vote/vote.component';
import { WaitComponent } from './components/wait/wait.component';
import { AnswerComponent } from './components/answer/answer.component';
import { HeaderComponent } from './components/header/header.component';
import { MainGameComponent } from './components/main-game/main-game.component';

@NgModule({
  declarations: [
    AppComponent,
    JoinGameComponent,
    VoteComponent,
    WaitComponent,
    AnswerComponent,
    HeaderComponent,
    MainGameComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
