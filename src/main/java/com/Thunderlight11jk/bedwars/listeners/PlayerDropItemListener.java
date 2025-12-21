package com.Thunderlight11jk.bedwars.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.Thunderlight11jk.bedwars.BedWarsPlugin;
import com.Thunderlight11jk.bedwars.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropItemListener implements Listener {
    
    private final BedWarsPlugin plugin;
    
    public PlayerDropItemListener(BedWarsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Arena arena = plugin.getGameManager().getPlayerArena(player);
        
        if (arena == null) return;
        
        XMaterial material = XMaterial.matchXMaterial(event.getItemDrop().getItemStack());
        
        if (material == XMaterial.LEATHER_HELMET || material == XMaterial.LEATHER_CHESTPLATE ||
            material == XMaterial.LEATHER_LEGGINGS || material == XMaterial.LEATHER_BOOTS ||
            material == XMaterial.WOODEN_SWORD) {
            event.setCancelled(true);
        }
    }
}