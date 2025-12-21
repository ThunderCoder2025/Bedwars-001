package com.Thunderlight11jk.bedwars.game;

import lombok.Data;

import java.util.UUID;

@Data
public class PlayerData {
    
    private final UUID playerId;
    private boolean alive = true;
    private int respawnTime = 0;
    private int kills = 0;
    private int finalKills = 0;
    private int deaths = 0;
    private int bedsBroken = 0;
    
    public PlayerData(UUID playerId) {
        this.playerId = playerId;
    }
}