package com.Thunderlight11jk.bedwars.listeners;

import com.Thunderlight11jk.bedwars.BedWarsPlugin;
import com.Thunderlight11jk.bedwars.arena.Arena;
import com.Thunderlight11jk.bedwars.arena.TeamData;
import com.Thunderlight11jk.bedwars.game.GameState;
import com.Thunderlight11jk.bedwars.game.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {
    
    private final BedWarsPlugin plugin;
    
    public EntityDamageListener(BedWarsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        
        Player player = (Player) event.getEntity();
        
        // Prevent all damage in lobby world
        if (plugin.isInLobbyWorld(player)) {
            event.setCancelled(true);
            return;
        }
        
        Arena arena = plugin.getGameManager().getPlayerArena(player);
        if (arena == null) return;
        
        if (arena.getState() != GameState.ACTIVE) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Player)) return;
        
        Player victim = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();
        
        Arena arena = plugin.getGameManager().getPlayerArena(victim);
        if (arena == null) return;
        
        Team victimTeam = plugin.getGameManager().getPlayerTeam(victim);
        Team damagerTeam = plugin.getGameManager().getPlayerTeam(damager);
        
        if (victimTeam == damagerTeam) {
            event.setCancelled(true);
            return;
        }
        
        TeamData damagerData = arena.getTeamData(damagerTeam);
        if (damagerData != null) {
            int sharpness = damagerData.getSharpness();
            if (sharpness > 0) {
                event.setDamage(event.getDamage() + sharpness * 0.5);
            }
        }
        
        TeamData victimData = arena.getTeamData(victimTeam);
        if (victimData != null) {
            int protection = victimData.getProtection();
            if (protection > 0) {
                event.setDamage(event.getDamage() - protection * 0.5);
            }
        }
    }
}