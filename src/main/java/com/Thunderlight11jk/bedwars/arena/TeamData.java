package com.Thunderlight11jk.bedwars.arena;

import lombok.Data;
import org.bukkit.Location;

import java.util.*;

@Data
public class TeamData {
    
    private Location spawnLocation;
    private Location bedLocation;
    private Location shopLocation;
    private Location upgradeShopLocation;
    private List<Location> ironGenerators = new ArrayList<>();
    private List<Location> goldGenerators = new ArrayList<>();
    
    private boolean bedAlive = true;
    private Set<UUID> players = new HashSet<>();
    
    private int sharpness = 0;
    private int protection = 0;
    private boolean hasteEnabled = false;
    private int forgeLevel = 0;
    private boolean healPoolEnabled = false;
    private Queue<String> traps = new LinkedList<>();
    
    public TeamData() {
    }
    
    public void reset() {
        bedAlive = true;
        players.clear();
        sharpness = 0;
        protection = 0;
        hasteEnabled = false;
        forgeLevel = 0;
        healPoolEnabled = false;
        traps.clear();
    }
    
    public void addPlayer(UUID playerId) {
        players.add(playerId);
    }
    
    public void removePlayer(UUID playerId) {
        players.remove(playerId);
    }
    
    public boolean hasPlayers() {
        return !players.isEmpty();
    }
    
    public int getPlayerCount() {
        return players.size();
    }
}