package com.Thunderlight11jk.bedwars.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.Thunderlight11jk.bedwars.BedWarsPlugin;
import com.Thunderlight11jk.bedwars.arena.Arena;
import com.Thunderlight11jk.bedwars.arena.TeamData;
import com.Thunderlight11jk.bedwars.game.GameSession;
import com.Thunderlight11jk.bedwars.game.Team;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    
    private final BedWarsPlugin plugin;
    
    public BlockBreakListener(BedWarsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        
        // Prevent block breaking in lobby world
        if (plugin.isInLobbyWorld(player)) {
            event.setCancelled(true);
            return;
        }
        
        Arena arena = plugin.getGameManager().getPlayerArena(player);
        if (arena == null) return;
        
        Block block = event.getBlock();
        XMaterial material = XMaterial.matchXMaterial(block.getType());
        
        if (material == XMaterial.RED_BED || material == XMaterial.WHITE_BED) {
            Team playerTeam = plugin.getGameManager().getPlayerTeam(player);
            
            for (Team team : arena.getTeams().keySet()) {
                TeamData data = arena.getTeamData(team);
                
                if (data.getBedLocation() != null && 
                    data.getBedLocation().distance(block.getLocation()) < 2) {
                    
                    if (team == playerTeam) {
                        event.setCancelled(true);
                        player.sendMessage("§cYou cannot break your own bed!");
                        return;
                    }
                    
                    if (!data.isBedAlive()) {
                        event.setCancelled(true);
                        return;
                    }
                    
                    GameSession session = plugin.getGameManager().getSession(arena);
                    if (session != null) {
                        session.handleBedBreak(team, player);
                        XSound.matchXSound("ENTITY_ENDER_DRAGON_GROWL").ifPresent(s -> s.play(player));
                    }
                    return;
                }
            }
        }
    }
}