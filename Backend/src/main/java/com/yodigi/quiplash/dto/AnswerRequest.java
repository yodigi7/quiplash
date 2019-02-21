package com.yodigi.quiplash.dto;

import java.io.Serializable;

public class AnswerRequest implements Serializable {
    private String answer;
    private Long questionAnswerId;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Long getQuestionAnswerId() {
        return questionAnswerId;
    }

    public void setQuestionAnswerId(Long questionAnswerId) {
        this.questionAnswerId = questionAnswerId;
    }
}
