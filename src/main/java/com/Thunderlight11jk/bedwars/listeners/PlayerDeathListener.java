package com.Thunderlight11jk.bedwars.listeners;

import com.Thunderlight11jk.bedwars.BedWarsPlugin;
import com.Thunderlight11jk.bedwars.arena.Arena;
import com.Thunderlight11jk.bedwars.game.GameSession;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
    
    private final BedWarsPlugin plugin;
    
    public PlayerDeathListener(BedWarsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Arena arena = plugin.getGameManager().getPlayerArena(player);
        
        if (arena == null) return;
        
        event.setDeathMessage(null);
        event.getDrops().clear();
        event.setDroppedExp(0);
        
        Player killer = player.getKiller();
        
        GameSession session = plugin.getGameManager().getSession(arena);
        if (session != null) {
            session.handlePlayerDeath(player, killer);
        }
    }
}