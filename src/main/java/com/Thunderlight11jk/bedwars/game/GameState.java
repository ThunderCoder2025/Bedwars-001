package com.Thunderlight11jk.bedwars.game;

public enum GameState {
    WAITING,
    STARTING,
    ACTIVE,
    ENDING;
    
    public boolean isJoinable() {
        return this == WAITING || this == STARTING;
    }
    
    public boolean isActive() {
        return this == ACTIVE;
    }
}