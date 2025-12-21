package com.Thunderlight11jk.bedwars.listeners;

import com.Thunderlight11jk.bedwars.BedWarsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    
    private final BedWarsPlugin plugin;
    
    public PlayerQuitListener(BedWarsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getGameManager().leaveArena(event.getPlayer());
    }
}