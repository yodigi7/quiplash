package com.yodigi.quiplash.dto;

import java.io.Serializable;

public class PhaseResponse implements Serializable {
    private String phase;

    public PhaseResponse(String phase) {
        this.phase = phase;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }
}
