package com.yodigi.quiplash.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
public class Contender {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String name;

    private Long score;

    @JsonIgnore
    @OneToMany(mappedBy = "contender",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<QuestionAnswer> questionAndAnswers;

    @ManyToOne
    @JoinColumn
    private Game game;

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Set<QuestionAnswer> getQuestionAndAnswers() {
        return questionAndAnswers;
    }

    public void setQuestionAndAnswers(Set<QuestionAnswer> questionAndAnswers) {
        this.questionAndAnswers = questionAndAnswers;
    }

    public Contender(Game game, String name) {
        this.game = game;
        this.name = name;
        this.score = 0L;
    }

    public Contender() {
    }
}
