package com.yodigi.quiplash.dto;

import java.io.Serializable;

public class VoteRequest implements Serializable {
    private Long questionAnswerId;

    public Long getQuestionAnswerId() {
        return questionAnswerId;
    }

    public void setQuestionAnswerId(Long questionAnswerId) {
        this.questionAnswerId = questionAnswerId;
    }
}
