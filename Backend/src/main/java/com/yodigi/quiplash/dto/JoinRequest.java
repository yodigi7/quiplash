package com.yodigi.quiplash.dto;

import java.io.Serializable;

public class JoinRequest implements Serializable {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
