package com.yodigi.quiplash.dto;

import com.yodigi.quiplash.entities.QuestionAnswer;

import java.util.Set;

public class QuestionsResponse {
    private Set<QuestionAnswer> questions;

    public QuestionsResponse(Set<QuestionAnswer> questions) {
        this.questions = questions;
    }

    public Set<QuestionAnswer> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<QuestionAnswer> questions) {
        this.questions = questions;
    }
}
