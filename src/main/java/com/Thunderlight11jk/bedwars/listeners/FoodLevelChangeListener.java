package com.Thunderlight11jk.bedwars.listeners;

import com.Thunderlight11jk.bedwars.BedWarsPlugin;
import com.Thunderlight11jk.bedwars.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodLevelChangeListener implements Listener {
    
    private final BedWarsPlugin plugin;
    
    public FoodLevelChangeListener(BedWarsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        
        Player player = (Player) event.getEntity();
        
        // Prevent hunger loss in lobby world
        if (plugin.isInLobbyWorld(player)) {
            event.setCancelled(true);
            event.setFoodLevel(20);
            return;
        }
        
        Arena arena = plugin.getGameManager().getPlayerArena(player);
        if (arena != null) {
            event.setCancelled(true);
        }
    }
}