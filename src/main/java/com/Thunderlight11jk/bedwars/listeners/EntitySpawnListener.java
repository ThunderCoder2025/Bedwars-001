package com.Thunderlight11jk.bedwars.listeners;

import com.Thunderlight11jk.bedwars.BedWarsPlugin;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class EntitySpawnListener implements Listener {
    
    private final BedWarsPlugin plugin;
    
    public EntitySpawnListener(BedWarsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        // Prevent all mob spawning in lobby world
        String lobbyWorld = plugin.getConfig().getString("lobby-world", "world");
        if (event.getLocation().getWorld().getName().equalsIgnoreCase(lobbyWorld)) {
            if (event.getEntityType() != EntityType.VILLAGER) {
                event.setCancelled(true);
            }
            return;
        }
        
        if (event.getEntityType() == EntityType.VILLAGER) {
            return;
        }
        
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL ||
            event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) {
            event.setCancelled(true);
        }
    }
}