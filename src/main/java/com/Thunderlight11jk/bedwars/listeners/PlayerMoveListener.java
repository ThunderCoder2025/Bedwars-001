package com.Thunderlight11jk.bedwars.listeners;

import com.Thunderlight11jk.bedwars.BedWarsPlugin;
import com.Thunderlight11jk.bedwars.arena.Arena;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {
    
    private final BedWarsPlugin plugin;
    
    public PlayerMoveListener(BedWarsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        // Check if player is in lobby world and fell into void
        if (plugin.isInLobbyWorld(player)) {
            if (player.getLocation().getY() < 0) {
                Location lobbyLoc = plugin.getLobbyLocation();
                if (lobbyLoc != null) {
                    player.teleport(lobbyLoc);
                }
            }
            return;
        }
        
        Arena arena = plugin.getGameManager().getPlayerArena(player);
        if (arena == null) return;
        
        if (player.getLocation().getY() < 0) {
            player.damage(player.getHealth());
        }
    }
}