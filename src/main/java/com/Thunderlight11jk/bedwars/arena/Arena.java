package com.Thunderlight11jk.bedwars.arena;

import com.Thunderlight11jk.bedwars.game.GameState;
import com.Thunderlight11jk.bedwars.game.Team;
import com.Thunderlight11jk.bedwars.game.GameType;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;

@Data
public class Arena {
    
    private String name;
    private String displayName;
    private World world;
    private Location lobbySpawn;
    private Location spectatorSpawn;
    private Location waitingPos1;
    private Location waitingPos2;
    private GameType mode;
    private int minPlayers;
    private int maxPlayers;
    
    private Map<Team, TeamData> teams = new HashMap<>();
    private List<GeneratorLocation> diamondGenerators = new ArrayList<>();
    private List<GeneratorLocation> emeraldGenerators = new ArrayList<>();
    
    private GameState state = GameState.WAITING;
    private Set<UUID> players = new HashSet<>();
    
    public Arena(String name) {
        this.name = name;
        this.displayName = name;
        this.minPlayers = 2;
        this.maxPlayers = 16;
    }
    
    public void addTeam(Team team, TeamData data) {
        teams.put(team, data);
    }
    
    public TeamData getTeamData(Team team) {
        return teams.get(team);
    }
    
    public boolean isSetup() {
        return world != null && lobbySpawn != null && spectatorSpawn != null 
            && !teams.isEmpty() && !diamondGenerators.isEmpty() && !emeraldGenerators.isEmpty();
    }
    
    public int getPlayerCount() {
        return players.size();
    }
    
    public boolean isFull() {
        return players.size() >= maxPlayers;
    }
    
    public boolean canStart() {
        return players.size() >= minPlayers;
    }
    private GameType gameType;

    public GameType getGameType() {
        return gameType;
    }

    public int getMaxPlayersByType() {
        if (gameType == null) return maxPlayers;

        switch (gameType) {
            case SOLO:
                return 8;
            case DOUBLES:
                return 16;
            case TRIOS:
                return 12;
            case QUADS:
                return 16;
            case FOUR_V_FOUR:
                return 8;
            default:
                return maxPlayers;
        }
    }

    public String getGameTypeDisplay() {
        if (gameType == null) return "Unknown";

        switch (gameType) {
            case SOLO:
                return "Solo";
            case DOUBLES:
                return "Doubles";
            case TRIOS:
                return "Trios";
            case QUADS:
                return "Quads";
            case FOUR_V_FOUR:
                return "4v4";
            default:
                return gameType.name();
        }
    }

}