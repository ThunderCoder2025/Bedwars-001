package com.Thunderlight11jk.bedwars.commands;

import com.Thunderlight11jk.bedwars.BedWarsPlugin;
import com.Thunderlight11jk.bedwars.arena.Arena;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BedWarsCommand implements CommandExecutor {
    
    private final BedWarsPlugin plugin;
    
    public BedWarsCommand(BedWarsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix() + "§cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "join":
                if (args.length < 2) {
                    Arena arena = plugin.getArenaManager().getAvailableArena();
                    if (arena != null) {
                        plugin.getGameManager().joinArena(player, arena);
                    } else {
                        player.sendMessage(plugin.getPrefix() + "§cNo available arenas found!");
                    }
                } else {
                    Arena arena = plugin.getArenaManager().getArena(args[1]);
                    if (arena != null) {
                        plugin.getGameManager().joinArena(player, arena);
                    } else {
                        player.sendMessage(plugin.getPrefix() + "§cArena not found!");
                    }
                }
                break;
                
            case "leave":
                plugin.getGameManager().leaveArena(player);
                player.sendMessage(plugin.getPrefix() + "§aYou left the game!");
                break;
                
            case "list":
                player.sendMessage(plugin.getPrefix() + "§aAvailable arenas:");
                for (Arena arena : plugin.getArenaManager().getArenas()) {
                    player.sendMessage("§7- §e" + arena.getName() + " §8(§7" + 
                        arena.getPlayerCount() + "§8/§7" + arena.getMaxPlayers() + "§8)");
                }
                break;
                
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void sendHelp(Player player) {
        player.sendMessage("§8§m                                        ");
        player.sendMessage("§e§lBedWars Commands");
        player.sendMessage(" ");
        player.sendMessage("§e/bw join [arena] §7- Join a game");
        player.sendMessage("§e/bw leave §7- Leave the game");
        player.sendMessage("§e/bw list §7- List all arenas");
        player.sendMessage("§8§m                                        ");
    }
}