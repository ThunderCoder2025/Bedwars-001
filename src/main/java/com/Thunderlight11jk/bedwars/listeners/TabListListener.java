package com.Thunderlight11jk.bedwars.listeners;

import com.Thunderlight11jk.bedwars.BedWarsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class TabListListener implements Listener {
    
    private final BedWarsPlugin plugin;
    private static final String TRADEMARK_HEADER = "§e§lBedwars-001";
    private static final String TRADEMARK_FOOTER = "§7created by §cThunderlight11jk";
    
    public TabListListener(BedWarsPlugin plugin) {
        this.plugin = plugin;
        
        // Periodically check and restore trademark for all players in lobby
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            String lobbyWorld = plugin.getConfig().getString("lobby-world", "world");
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getWorld().getName().equalsIgnoreCase(lobbyWorld)) {
                    checkAndRestoreTrademark(player);
                }
            }
        }, 20L, 20L);
    }
    
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        String lobbyWorld = plugin.getConfig().getString("lobby-world", "world");
        
        if (player.getWorld().getName().equalsIgnoreCase(lobbyWorld)) {
            // Player entered lobby world
            setTabListTrademark(player);
            plugin.getScoreboardManager().createLobbyScoreboard(player);
        } else {
            // Player left lobby world, remove lobby scoreboard
            plugin.getScoreboardManager().removeScoreboard(player);
        }
    }
    
    private void checkAndRestoreTrademark(Player player) {
        // Always set the trademark - if it was removed, it will show "TRADEMARK MISSING"
        // We use a simple approach: always set it, and if someone tries to remove it,
        // the next tick will restore it
        setTabListTrademark(player);
    }
    
    private void setTabListTrademark(Player player) {
        // Use reflection to call setPlayerListHeaderFooter
        try {
            player.getClass().getMethod("setPlayerListHeaderFooter", String.class, String.class)
                .invoke(player, TRADEMARK_HEADER, TRADEMARK_FOOTER);
        } catch (Exception e) {
            plugin.getLogger().warning("Could not set tab list for player: " + player.getName() + " - " + e.getMessage());
        }
    }
}

