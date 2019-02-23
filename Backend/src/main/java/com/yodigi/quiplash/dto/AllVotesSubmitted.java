package com.yodigi.quiplash.dto;

import java.io.Serializable;

public class AllVotesSubmitted implements Serializable {
    private boolean allVotesSubmitted;

    public AllVotesSubmitted(boolean allVotesSubmitted) {
        this.allVotesSubmitted = allVotesSubmitted;
    }

    public boolean isAllVotesSubmitted() {
        return allVotesSubmitted;
    }

    public void setAllVotesSubmitted(boolean allVotesSubmitted) {
        this.allVotesSubmitted = allVotesSubmitted;
    }
}
