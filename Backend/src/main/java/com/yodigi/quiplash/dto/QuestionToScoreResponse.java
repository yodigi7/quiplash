package com.yodigi.quiplash.dto;

import com.yodigi.quiplash.entities.QuestionAnswer;

import java.io.Serializable;
import java.util.List;

public class QuestionsToScoreResponse implements Serializable {
    private List<QuestionAnswer> questionAnswers;

    public QuestionsToScoreResponse(List<QuestionAnswer> questionAnswers) {
        this.questionAnswers = questionAnswers;
    }

    public List<QuestionAnswer> getQuestionAnswers() {
        return questionAnswers;
    }

    public void setQuestionAnswers(List<QuestionAnswer> questionAnswers) {
        this.questionAnswers = questionAnswers;
    }
}
