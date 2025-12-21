package com.Thunderlight11jk.bedwars.game;

import com.Thunderlight11jk.bedwars.BedWarsPlugin;
import com.Thunderlight11jk.bedwars.arena.Arena;
import com.Thunderlight11jk.bedwars.arena.TeamData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class GameManager {
    
    private final BedWarsPlugin plugin;
    private final Map<String, GameSession> activeSessions = new HashMap<>();
    private final Map<UUID, Arena> playerArenas = new HashMap<>();
    private final Map<UUID, Team> playerTeams = new HashMap<>();
    
    public GameManager(BedWarsPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void joinArena(Player player, Arena arena) {
        if (playerArenas.containsKey(player.getUniqueId())) {
            player.sendMessage(plugin.getPrefix() + "§cYou are already in a game!");
            return;
        }
        
        if (arena.isFull()) {
            player.sendMessage(plugin.getPrefix() + "§cThis arena is full!");
            return;
        }
        
        if (!arena.getState().isJoinable()) {
            player.sendMessage(plugin.getPrefix() + "§cThis game has already started!");
            return;
        }
        
        GameSession session = getOrCreateSession(arena);
        session.addPlayer(player);
        
        arena.getPlayers().add(player.getUniqueId());
        playerArenas.put(player.getUniqueId(), arena);
        
        // Teleport to arena lobby spawn, or fall back appropriately
        org.bukkit.Location target = arena.getLobbySpawn();
        
        // Try to resolve a world for this arena if needed
        org.bukkit.World arenaWorld = arena.getWorld();
        if (arenaWorld == null) {
            // Common setup: world name matches arena name
            arenaWorld = Bukkit.getWorld(arena.getName());
            if (arenaWorld != null) {
                arena.setWorld(arenaWorld);
            }
        }
        
        if (target == null) {
            if (arenaWorld != null) {
                target = arenaWorld.getSpawnLocation();
            } else {
                target = plugin.getLobbyLocation();
            }
        } else if (target.getWorld() == null && arenaWorld != null) {
            // Clone with resolved world if YAML was loaded before world existed
            target = new org.bukkit.Location(
                arenaWorld,
                target.getX(),
                target.getY(),
                target.getZ(),
                target.getYaw(),
                target.getPitch()
            );
        }
        
        player.teleport(target);
        
        broadcast(arena, "§e" + player.getName() + " §7has joined! §8(§a" + arena.getPlayerCount() + "§7/§a" + arena.getMaxPlayers() + "§8)");
        
        if (arena.getPlayerCount() >= arena.getMinPlayers() && arena.getState() == GameState.WAITING) {
            session.startCountdown();
        }
    }
    
    public void leaveArena(Player player) {
        UUID playerId = player.getUniqueId();
        Arena arena = playerArenas.remove(playerId);
        
        if (arena == null) return;
        
        GameSession session = activeSessions.get(arena.getName());
        if (session != null) {
            session.removePlayer(player);
        }
        
        arena.getPlayers().remove(playerId);
        Team team = playerTeams.remove(playerId);
        
        if (team != null) {
            TeamData teamData = arena.getTeamData(team);
            if (teamData != null) {
                teamData.removePlayer(playerId);
            }
        }
        
        // Teleport back to global lobby location
        player.teleport(plugin.getLobbyLocation());
        
        if (arena.getState() != GameState.WAITING && session != null) {
            session.checkWinCondition();
        }
    }
    
    public void selectTeam(Player player, Team team) {
        UUID playerId = player.getUniqueId();
        Arena arena = playerArenas.get(playerId);
        
        if (arena == null) {
            player.sendMessage(plugin.getPrefix() + "§cYou are not in a game!");
            return;
        }
        
        if (arena.getState() != GameState.WAITING && arena.getState() != GameState.STARTING) {
            player.sendMessage(plugin.getPrefix() + "§cYou cannot change teams during the game!");
            return;
        }
        
        TeamData teamData = arena.getTeamData(team);
        if (teamData == null) {
            player.sendMessage(plugin.getPrefix() + "§cThis team is not available!");
            return;
        }
        
        Team oldTeam = playerTeams.get(playerId);
        if (oldTeam != null) {
            TeamData oldTeamData = arena.getTeamData(oldTeam);
            if (oldTeamData != null) {
                oldTeamData.removePlayer(playerId);
            }
        }
        
        playerTeams.put(playerId, team);
        teamData.addPlayer(playerId);
        
        player.sendMessage(plugin.getPrefix() + "§aYou have joined team " + team.getColoredName() + "§a!");
    }
    
    private GameSession getOrCreateSession(Arena arena) {
        return activeSessions.computeIfAbsent(arena.getName(), k -> new GameSession(plugin, arena));
    }
    
    public void tick() {
        for (GameSession session : new ArrayList<>(activeSessions.values())) {
            session.tick();
        }
    }
    
    public void shutdown() {
        for (GameSession session : new ArrayList<>(activeSessions.values())) {
            session.endGame(null);
        }
    }
    
    public Arena getPlayerArena(Player player) {
        return playerArenas.get(player.getUniqueId());
    }
    
    public Team getPlayerTeam(Player player) {
        return playerTeams.get(player.getUniqueId());
    }
    
    public GameSession getSession(Arena arena) {
        return activeSessions.get(arena.getName());
    }
    
    public void removeSession(Arena arena) {
        activeSessions.remove(arena.getName());
    }
    
    private void broadcast(Arena arena, String message) {
        for (UUID playerId : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }
}