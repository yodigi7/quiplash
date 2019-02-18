import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { WaitComponent } from './components/wait/wait.component';
import { JoinComponent } from './components/join/join.component';
import { VoteComponent } from './components/vote/vote.component';
import { FinalResultsComponent } from './components/final-results/final-results.component';
import { MainGameComponent } from './components/main-game/main-game.component';
import { HeaderComponent } from './components/header/header.component';

@NgModule({
  declarations: [
    AppComponent,
    WaitComponent,
    JoinComponent,
    VoteComponent,
    FinalResultsComponent,
    MainGameComponent,
    HeaderComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [ AppComponent ]
})
export class AppModule { }

export const routingComponents = [ MainGameComponent ]