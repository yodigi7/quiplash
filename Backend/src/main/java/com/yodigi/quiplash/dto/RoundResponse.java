package com.yodigi.quiplash.dto;

import java.io.Serializable;

public class RoundResponse implements Serializable {
    private Integer round;

    public RoundResponse(Integer round) {
        this.round = round;
    }

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }
}
