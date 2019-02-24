package com.yodigi.quiplash.dto;

import java.io.Serializable;

public class MoreToVoteOn implements Serializable {
    private boolean moreToVoteOn;

    public MoreToVoteOn(boolean moreToVoteOn) {
        this.moreToVoteOn = moreToVoteOn;
    }

    public boolean isMoreToVoteOn() {
        return moreToVoteOn;
    }

    public void setMoreToVoteOn(boolean moreToVoteOn) {
        this.moreToVoteOn = moreToVoteOn;
    }
}
