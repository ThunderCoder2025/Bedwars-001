package com.Thunderlight11jk.bedwars.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.Thunderlight11jk.bedwars.BedWarsPlugin;
import com.Thunderlight11jk.bedwars.arena.Arena;
import com.Thunderlight11jk.bedwars.arena.TeamData;
import com.Thunderlight11jk.bedwars.game.Team;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {
    
    private final BedWarsPlugin plugin;
    
    public PlayerInteractListener(BedWarsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Villager)) return;
        
        Player player = event.getPlayer();
        Arena arena = plugin.getGameManager().getPlayerArena(player);
        
        if (arena == null) return;
        
        event.setCancelled(true);
        
        Team team = plugin.getGameManager().getPlayerTeam(player);
        if (team == null) return;
        
        TeamData teamData = arena.getTeamData(team);
        if (teamData == null) return;
        
        Villager villager = (Villager) event.getRightClicked();
        
        if (villager.getLocation().distance(teamData.getShopLocation()) < 2) {
            plugin.getShopManager().openItemShop(player);
        } else if (villager.getLocation().distance(teamData.getUpgradeShopLocation()) < 2) {
            plugin.getShopManager().openUpgradeShop(player);
        }
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Arena arena = plugin.getGameManager().getPlayerArena(player);
        
        if (arena == null) return;
        
        if (event.getClickedBlock() == null) return;
        
        Block block = event.getClickedBlock();
        XMaterial material = XMaterial.matchXMaterial(block.getType());
        
        if (material == XMaterial.RED_BED || material == XMaterial.WHITE_BED) {
            Team playerTeam = plugin.getGameManager().getPlayerTeam(player);
            
            for (Team team : arena.getTeams().keySet()) {
                TeamData data = arena.getTeamData(team);
                if (data.getBedLocation() != null && 
                    data.getBedLocation().getBlock().equals(block)) {
                    
                    if (team == playerTeam) {
                        event.setCancelled(true);
                        player.sendMessage("§cYou cannot break your own bed!");
                    }
                    break;
                }
            }
        }
    }
}