package com.yodigi.quiplash.dto;

import java.io.Serializable;
import java.util.Set;

public class ContenderNamesResponse implements Serializable {
    private Set<String> names;

    public ContenderNamesResponse(Set<String> names) {
        this.names = names;
    }

    public Set<String> getNames() {
        return names;
    }

    public void setNames(Set<String> names) {
        this.names = names;
    }
}
