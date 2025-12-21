package com.Thunderlight11jk.bedwars.commands;

import com.Thunderlight11jk.bedwars.BedWarsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveCommand implements CommandExecutor {

    private final BedWarsPlugin plugin;

    public LeaveCommand(BedWarsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix() + "§cThis command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;
        plugin.getGameManager().leaveArena(player);
        player.sendMessage(plugin.getPrefix() + "§aYou left the game!");
        return true;
    }
}
