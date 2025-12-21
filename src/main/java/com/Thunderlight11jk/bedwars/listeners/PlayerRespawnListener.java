package com.Thunderlight11jk.bedwars.listeners;

import com.Thunderlight11jk.bedwars.BedWarsPlugin;
import com.Thunderlight11jk.bedwars.arena.Arena;
import com.Thunderlight11jk.bedwars.arena.TeamData;
import com.Thunderlight11jk.bedwars.game.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {
    
    private final BedWarsPlugin plugin;
    
    public PlayerRespawnListener(BedWarsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Arena arena = plugin.getGameManager().getPlayerArena(player);
        
        if (arena == null) return;
        
        Team team = plugin.getGameManager().getPlayerTeam(player);
        if (team == null) return;
        
        TeamData teamData = arena.getTeamData(team);
        if (teamData == null) return;
        
        event.setRespawnLocation(teamData.getSpawnLocation());
    }
}