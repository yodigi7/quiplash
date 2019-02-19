package com.yodigi.quiplash.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Game {

    @Id
    @GeneratedValue
    private Long id;

    private Integer round;

    private String phase;

    @OneToMany(mappedBy = "game",
            cascade = CascadeType.ALL)
    private List<QuestionAnswer> currentQuestionAnswers;

    @JsonIgnore
    @OneToMany(mappedBy = "game",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<Contender> contenders;

    @JsonIgnore
    @OneToMany(mappedBy = "game",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<Round> rounds;

    public Set<Round> getRounds() {
        return rounds;
    }

    public void setRounds(Collection<Round> rounds) {
        this.rounds.clear();
        this.rounds.addAll(rounds);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public Set<Contender> getContenders() {
        return contenders;
    }

    public void setContenders(Collection<Contender> contenders) {
        this.contenders.clear();
        this.contenders.addAll(contenders);
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public List<QuestionAnswer> getCurrentQuestionAnswers() {
        return currentQuestionAnswers;
    }

    public void setCurrentQuestionAnswers(Collection<QuestionAnswer> currentQuestionAnswers) {
        clearCurrentQuestionAnswers();
        this.currentQuestionAnswers.addAll(currentQuestionAnswers);
    }

    public void clearCurrentQuestionAnswers() {
        currentQuestionAnswers.clear();
    }

    @Override
    public String toString() {
        return id.toString();
    }

    public Game() {
        this.contenders = new HashSet<>();
        this.rounds = new HashSet<>();
        this.round = 0;
    }
}
