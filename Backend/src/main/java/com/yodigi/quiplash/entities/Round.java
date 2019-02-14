package com.yodigi.quiplash.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Round {

    @Id
    @GeneratedValue
    private Long id;

    private Integer roundNumber;

    @ManyToOne
    @JoinColumn
    private Game game;

    @JsonIgnore
    @OneToMany(mappedBy = "round",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<QuestionAnswer> questionAnswers;

    public Round(Integer roundNumber) {
        this.roundNumber = roundNumber;
        this.questionAnswers = new HashSet<>();
    }

    public Integer getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Set<QuestionAnswer> getQuestionAnswers() {
        return questionAnswers;
    }

    public void setQuestionAnswers(Collection<QuestionAnswer> questionAnswers) {
        this.questionAnswers.clear();
        this.questionAnswers.addAll(questionAnswers);
    }

    public Round() {
        this.questionAnswers = new HashSet<>();
    }
}
