package com.yodigi.quiplash.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class QuestionAnswer {

    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn
    private Contender contender;

    private String question;

    private String answer;

    @JsonIgnore
    @ManyToOne
    @JoinColumn
    private Game game;

    @JsonIgnore
    @ManyToOne
    @JoinColumn
    private Round round;

    private Integer score;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Round getRound() {
        return round;
    }

    public void setRound(Round round) {
        this.round = round;
    }

    public Contender getContender() {
        return contender;
    }

    public void setContender(Contender contender) {
        this.contender = contender;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public void incrementScore() {
        score += 1;
    }

    public Long getId() {
        return id;
    }
}
