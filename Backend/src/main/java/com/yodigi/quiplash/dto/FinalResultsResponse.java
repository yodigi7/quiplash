package com.yodigi.quiplash.dto;

import com.yodigi.quiplash.entities.Contender;

import java.io.Serializable;
import java.util.Set;

public class FinalResultsResponse implements Serializable {
    private Set<Contender> contenders;

    public FinalResultsResponse(Set<Contender> contenders) {
        this.contenders = contenders;
    }

    public Set<Contender> getContenders() {
        return contenders;
    }

    public void setContenders(Set<Contender> contenders) {
        this.contenders = contenders;
    }
}
