package com.yodigi.quiplash.dto;

public class InitResponse {
    private Long gameId;

    public InitResponse(Long gameId) {
        this.gameId = gameId;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }
}
