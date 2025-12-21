package com.Thunderlight11jk.bedwars.listeners;

import com.Thunderlight11jk.bedwars.BedWarsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinListener implements Listener {
    
    private final BedWarsPlugin plugin;
    private static final String TRADEMARK_HEADER = "§e§lBedwars-001";
    private static final String TRADEMARK_FOOTER = "§7created by §cThunderlight11jk";
    
    public PlayerJoinListener(BedWarsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Teleport to global lobby location if they joined in the lobby world
        if (plugin.isInLobbyWorld(player)) {
            player.teleport(plugin.getLobbyLocation());
            
            // Set up lobby scoreboard
            plugin.getScoreboardManager().createLobbyScoreboard(player);
            
            // Set tab list trademark
            setTabListTrademark(player);
            
            // Schedule periodic update to keep trademark and scoreboard
            Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                if (player.isOnline() && plugin.isInLobbyWorld(player)) {
                    setTabListTrademark(player);
                    plugin.getScoreboardManager().updateLobbyScoreboard(player);
                }
            }, 20L, 20L);
        }
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        // Update tab list for remaining players
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                String lobbyWorld = plugin.getConfig().getString("lobby-world", "world");
                if (p.getWorld().getName().equalsIgnoreCase(lobbyWorld)) {
                    setTabListTrademark(p);
                }
            }
        }, 1L);
    }
    
    private void setTabListTrademark(Player player) {
        try {
            player.getClass().getMethod("setPlayerListHeaderFooter", String.class, String.class)
                .invoke(player, TRADEMARK_HEADER, TRADEMARK_FOOTER);
        } catch (Exception e) {
            plugin.getLogger().warning("Could not set tab list for player: " + player.getName() + " - " + e.getMessage());
        }
    }
}