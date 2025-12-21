package com.Thunderlight11jk.bedwars.commands;

import com.Thunderlight11jk.bedwars.BedWarsPlugin;
import com.Thunderlight11jk.bedwars.arena.Arena;
import com.Thunderlight11jk.bedwars.arena.GeneratorLocation;
import com.Thunderlight11jk.bedwars.arena.TeamData;
import com.Thunderlight11jk.bedwars.game.GameType;
import com.Thunderlight11jk.bedwars.game.Team;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BedWarsAdminCommand implements CommandExecutor {
    
    private final BedWarsPlugin plugin;
    
    public BedWarsAdminCommand(BedWarsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("bedwars.admin")) {
            sender.sendMessage(plugin.getPrefix() + "§cYou don't have permission!");
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix() + "§cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        
        String cmd = args[0];
        String commandLower = cmd.toLowerCase();
        
        // Handle setLobby (global lobby) separately before switch
        if (cmd.equals("setLobby") && args.length == 1) {
            plugin.setLobbyLocation(player.getLocation());
            player.sendMessage(plugin.getPrefix() + "§aSet global lobby location at " + 
                String.format("§7(%.1f, %.1f, %.1f)", player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
            return true;
        }
        
        switch (commandLower) {
            case "create":
                if (args.length < 2) {
                    player.sendMessage(plugin.getPrefix() + "§cUsage: /bwadmin create <name>");
                    return true;
                }
                
                Arena newArena = new Arena(args[1]);
                newArena.setWorld(player.getWorld());
                plugin.getArenaManager().addArena(newArena);
                player.sendMessage(plugin.getPrefix() + "§aCreated arena: " + args[1]);
                break;
                
            case "delete":
                if (args.length < 2) {
                    player.sendMessage(plugin.getPrefix() + "§cUsage: /bwadmin delete <name>");
                    return true;
                }
                
                plugin.getArenaManager().removeArena(args[1]);
                player.sendMessage(plugin.getPrefix() + "§aDeleted arena: " + args[1]);
                break;

            case "setteamspawn": {
                if (args.length < 3) {
                    player.sendMessage(plugin.getPrefix() + "§cUsage: /bwadmin setteamspawn <arena> <team>");
                    return true;
                }
                
                Arena arena = plugin.getArenaManager().getArena(args[1]);
                if (arena != null) {
                    try {
                        Team team = Team.valueOf(args[2].toUpperCase());
                        TeamData data = arena.getTeamData(team);
                        if (data != null) {
                            data.setSpawnLocation(player.getLocation());
                            plugin.getArenaManager().saveArenas();
                            player.sendMessage(plugin.getPrefix() + "§aSet team spawn for " + team.getColoredName() + " §aat " + 
                                String.format("§7(%.1f, %.1f, %.1f)", player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
                        } else {
                            player.sendMessage(plugin.getPrefix() + "§cTeam not found in this arena!");
                        }
                    } catch (IllegalArgumentException e) {
                        player.sendMessage(plugin.getPrefix() + "§cInvalid team! Available: RED, BLUE, GREEN, YELLOW, AQUA, WHITE, PINK, GRAY");
                    }
                } else {
                    player.sendMessage(plugin.getPrefix() + "§cArena not found!");
                }
                break;
                }

            case "setlobby":
                if (args.length < 2) {
                    player.sendMessage(plugin.getPrefix() + "§cUsage: /bwadmin setlobby <arena>");
                    return true;
                }
                
                Arena arena = plugin.getArenaManager().getArena(args[1]);
                if (arena != null) {
                    arena.setLobbySpawn(player.getLocation());
                    plugin.getArenaManager().saveArenas();
                    player.sendMessage(plugin.getPrefix() + "§aSet lobby spawn for " + args[1]);
                } else {
                    player.sendMessage(plugin.getPrefix() + "§cArena not found!");
                }
                break;
                
            case "setspectator":
                if (args.length < 2) {
                    player.sendMessage(plugin.getPrefix() + "§cUsage: /bwadmin setspectator <arena>");
                    return true;
                }
                
                arena = plugin.getArenaManager().getArena(args[1]);
                if (arena != null) {
                    arena.setSpectatorSpawn(player.getLocation());
                    plugin.getArenaManager().saveArenas();
                    player.sendMessage(plugin.getPrefix() + "§aSet spectator spawn for " + args[1]);
                } else {
                    player.sendMessage(plugin.getPrefix() + "§cArena not found!");
                }
                break;
                
            case "addteam":
                if (args.length < 3) {
                    player.sendMessage(plugin.getPrefix() + "§cUsage: /bwadmin addteam <arena> <team>");
                    return true;
                }
                
                arena = plugin.getArenaManager().getArena(args[1]);
                if (arena != null) {
                    try {
                        Team team = Team.valueOf(args[2].toUpperCase());
                        TeamData data = new TeamData();
                        data.setSpawnLocation(player.getLocation());
                        arena.addTeam(team, data);
                        plugin.getArenaManager().saveArenas();
                        player.sendMessage(plugin.getPrefix() + "§aAdded team " + team.getColoredName() + " §ato " + args[1]);
                    } catch (IllegalArgumentException e) {
                        player.sendMessage(plugin.getPrefix() + "§cInvalid team! Available: RED, BLUE, GREEN, YELLOW, AQUA, WHITE, PINK, GRAY");
                    }
                } else {
                    player.sendMessage(plugin.getPrefix() + "§cArena not found!");
                }
                break;
                
            case "setbed":
                if (args.length < 3) {
                    player.sendMessage(plugin.getPrefix() + "§cUsage: /bwadmin setbed <arena> <team>");
                    return true;
                }
                
                arena = plugin.getArenaManager().getArena(args[1]);
                if (arena != null) {
                    try {
                        Team team = Team.valueOf(args[2].toUpperCase());
                        TeamData data = arena.getTeamData(team);
                        if (data != null) {
                            data.setBedLocation(player.getLocation());
                            plugin.getArenaManager().saveArenas();
                            player.sendMessage(plugin.getPrefix() + "§aSet bed location for " + team.getColoredName());
                        } else {
                            player.sendMessage(plugin.getPrefix() + "§cTeam not found in this arena!");
                        }
                    } catch (IllegalArgumentException e) {
                        player.sendMessage(plugin.getPrefix() + "§cInvalid team!");
                    }
                } else {
                    player.sendMessage(plugin.getPrefix() + "§cArena not found!");
                }
                break;
                
            case "setshop":
                if (args.length < 3) {
                    player.sendMessage(plugin.getPrefix() + "§cUsage: /bwadmin setshop <arena> <team>");
                    return true;
                }
                
                arena = plugin.getArenaManager().getArena(args[1]);
                if (arena != null) {
                    try {
                        Team team = Team.valueOf(args[2].toUpperCase());
                        TeamData data = arena.getTeamData(team);
                        if (data != null) {
                            data.setShopLocation(player.getLocation());
                            plugin.getArenaManager().saveArenas();
                            player.sendMessage(plugin.getPrefix() + "§aSet shop location for " + team.getColoredName());
                        } else {
                            player.sendMessage(plugin.getPrefix() + "§cTeam not found in this arena!");
                        }
                    } catch (IllegalArgumentException e) {
                        player.sendMessage(plugin.getPrefix() + "§cInvalid team!");
                    }
                } else {
                    player.sendMessage(plugin.getPrefix() + "§cArena not found!");
                }
                break;
                
            case "setupgradeshop":
                if (args.length < 3) {
                    player.sendMessage(plugin.getPrefix() + "§cUsage: /bwadmin setupgradeshop <arena> <team>");
                    return true;
                }
                
                arena = plugin.getArenaManager().getArena(args[1]);
                if (arena != null) {
                    try {
                        Team team = Team.valueOf(args[2].toUpperCase());
                        TeamData data = arena.getTeamData(team);
                        if (data != null) {
                            data.setUpgradeShopLocation(player.getLocation());
                            plugin.getArenaManager().saveArenas();
                            player.sendMessage(plugin.getPrefix() + "§aSet upgrade shop location for " + team.getColoredName());
                        } else {
                            player.sendMessage(plugin.getPrefix() + "§cTeam not found in this arena!");
                        }
                    } catch (IllegalArgumentException e) {
                        player.sendMessage(plugin.getPrefix() + "§cInvalid team!");
                    }
                } else {
                    player.sendMessage(plugin.getPrefix() + "§cArena not found!");
                }
                break;
                
            case "addiron":
                if (args.length < 3) {
                    player.sendMessage(plugin.getPrefix() + "§cUsage: /bwadmin addiron <arena> <team>");
                    return true;
                }
                
                arena = plugin.getArenaManager().getArena(args[1]);
                if (arena != null) {
                    try {
                        Team team = Team.valueOf(args[2].toUpperCase());
                        TeamData data = arena.getTeamData(team);
                        if (data != null) {
                            data.getIronGenerators().add(player.getLocation());
                            plugin.getArenaManager().saveArenas();
                            player.sendMessage(plugin.getPrefix() + "§aAdded iron generator for " + team.getColoredName());
                        } else {
                            player.sendMessage(plugin.getPrefix() + "§cTeam not found in this arena!");
                        }
                    } catch (IllegalArgumentException e) {
                        player.sendMessage(plugin.getPrefix() + "§cInvalid team!");
                    }
                } else {
                    player.sendMessage(plugin.getPrefix() + "§cArena not found!");
                }
                break;
                
            case "addgold":
                if (args.length < 3) {
                    player.sendMessage(plugin.getPrefix() + "§cUsage: /bwadmin addgold <arena> <team>");
                    return true;
                }
                
                arena = plugin.getArenaManager().getArena(args[1]);
                if (arena != null) {
                    try {
                        Team team = Team.valueOf(args[2].toUpperCase());
                        TeamData data = arena.getTeamData(team);
                        if (data != null) {
                            data.getGoldGenerators().add(player.getLocation());
                            plugin.getArenaManager().saveArenas();
                            player.sendMessage(plugin.getPrefix() + "§aAdded gold generator for " + team.getColoredName());
                        } else {
                            player.sendMessage(plugin.getPrefix() + "§cTeam not found in this arena!");
                        }
                    } catch (IllegalArgumentException e) {
                        player.sendMessage(plugin.getPrefix() + "§cInvalid team!");
                    }
                } else {
                    player.sendMessage(plugin.getPrefix() + "§cArena not found!");
                }
                break;
                
            case "adddiamond":
                if (args.length < 2) {
                    player.sendMessage(plugin.getPrefix() + "§cUsage: /bwadmin adddiamond <arena> [tier]");
                    return true;
                }
                
                arena = plugin.getArenaManager().getArena(args[1]);
                if (arena != null) {
                    int tier = args.length >= 3 ? Integer.parseInt(args[2]) : 1;
                    arena.getDiamondGenerators().add(new GeneratorLocation(player.getLocation(), tier));
                    plugin.getArenaManager().saveArenas();
                    player.sendMessage(plugin.getPrefix() + "§aAdded diamond generator tier " + tier);
                } else {
                    player.sendMessage(plugin.getPrefix() + "§cArena not found!");
                }
                break;
                
            case "addemerald":
                if (args.length < 2) {
                    player.sendMessage(plugin.getPrefix() + "§cUsage: /bwadmin addemerald <arena> [tier]");
                    return true;
                }
                
                arena = plugin.getArenaManager().getArena(args[1]);
                if (arena != null) {
                    int tier = args.length >= 3 ? Integer.parseInt(args[2]) : 1;
                    arena.getEmeraldGenerators().add(new GeneratorLocation(player.getLocation(), tier));
                    plugin.getArenaManager().saveArenas();
                    player.sendMessage(plugin.getPrefix() + "§aAdded emerald generator tier " + tier);
                } else {
                    player.sendMessage(plugin.getPrefix() + "§cArena not found!");
                }
                break;
                
            case "setwaitingpos1": {
                if (args.length < 2) {
                    player.sendMessage(plugin.getPrefix() + "§cUsage: /bwadmin setwaitingpos1 <arena>");
                    return true;
                }
                
                arena = plugin.getArenaManager().getArena(args[1]);
                if (arena != null) {
                    arena.setWaitingPos1(player.getLocation());
                    plugin.getArenaManager().saveArenas();
                    player.sendMessage(plugin.getPrefix() + "§aSet waiting position 1 for " + args[1] + " §aat " + 
                        String.format("§7(%.1f, %.1f, %.1f)", player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
                } else {
                    player.sendMessage(plugin.getPrefix() + "§cArena not found!");
                }
                break;
            }
                
            case "setwaitingpos2": {
                if (args.length < 2) {
                    player.sendMessage(plugin.getPrefix() + "§cUsage: /bwadmin setwaitingpos2 <arena>");
                    return true;
                }
                
                arena = plugin.getArenaManager().getArena(args[1]);
                if (arena != null) {
                    arena.setWaitingPos2(player.getLocation());
                    plugin.getArenaManager().saveArenas();
                    player.sendMessage(plugin.getPrefix() + "§aSet waiting position 2 for " + args[1] + " §aat " + 
                        String.format("§7(%.1f, %.1f, %.1f)", player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
                    player.sendMessage(plugin.getPrefix() + "§7Waiting region will be cleared when the game starts.");
                } else {
                    player.sendMessage(plugin.getPrefix() + "§cArena not found!");
                }
                break;
            }
                
            case "settype": {
                if (args.length < 3) {
                    player.sendMessage(plugin.getPrefix() + "§cUsage: /bwadmin settype <arena> <solos/duos/trios/quads/4v4>");
                    return true;
                }
                
                arena = plugin.getArenaManager().getArena(args[1]);
                if (arena != null) {
                    String type = args[2].toLowerCase();
                    if (setGameType(arena, type)) {
                        arena.setGameType(GameType.valueOf(type));
                        plugin.getArenaManager().saveArenas();
                        player.sendMessage(plugin.getPrefix() + "§aSet game type to §e" + type.toUpperCase() + " §afor " + args[1]);
                        player.sendMessage(plugin.getPrefix() + "§7Min players to start: §e" + arena.getMinPlayers());
                        player.sendMessage(plugin.getPrefix() + "§7Max players: §e" + arena.getMaxPlayers());
                    } else {
                        player.sendMessage(plugin.getPrefix() + "§cInvalid game type! Available: solos, duos, trios, quads, 4v4");
                    }
                } else {
                    player.sendMessage(plugin.getPrefix() + "§cArena not found!");
                }
                break;
            }
                
            case "save":
                plugin.getArenaManager().saveArenas();
                player.sendMessage(plugin.getPrefix() + "§aSaved all arenas and maps!");
                break;
                
            case "reload":
                plugin.reloadConfig();
                plugin.getArenaManager().loadArenas();
                player.sendMessage(plugin.getPrefix() + "§aReloaded configuration!");
                break;
                
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private boolean setGameType(Arena arena, String type) {
        switch (type.toLowerCase()) {
            case "solos":
                // Solos: 1 player per team, typically 8 teams = 8 players max
                arena.setMinPlayers(2); // Minimum 2 players to start
                arena.setMaxPlayers(8); // Maximum 8 players
                return true;
                
            case "duos":
                // Duos: 2 players per team, typically 4 teams = 8 players max
                arena.setMinPlayers(4); // Minimum 4 players to start (2 teams)
                arena.setMaxPlayers(8); // Maximum 8 players (4 teams)
                return true;
                
            case "trios":
                // Trios: 3 players per team, typically 3 teams = 9 players max
                arena.setMinPlayers(6); // Minimum 6 players to start (2 teams)
                arena.setMaxPlayers(9); // Maximum 9 players (3 teams)
                return true;
                
            case "quads":
                // Quads: 4 players per team, typically 2 teams = 8 players max
                arena.setMinPlayers(6); // Minimum 6 players to start
                arena.setMaxPlayers(8); // Maximum 8 players (2 teams)
                return true;
                
            case "4v4":
                // 4v4: 4 teams of 4 players each = 16 players max
                arena.setMinPlayers(12); // Minimum 12 players to start (3 teams)
                arena.setMaxPlayers(16); // Maximum 16 players (4 teams)
                return true;
                
            default:
                return false;
        }
    }
    
    private void sendHelp(Player player) {
        player.sendMessage("§8§m                                        ");
        player.sendMessage("§c§lBedWars Admin Commands");
        player.sendMessage(" ");
        player.sendMessage("§c/bwadmin create <name> §7- Create arena");
        player.sendMessage("§c/bwadmin delete <name> §7- Delete arena");
        player.sendMessage("§c/bwadmin settype <arena> <type> §7- Set game type");
        player.sendMessage("§c/bwadmin setLobby §7- Set global lobby location");
        player.sendMessage("§c/bwadmin setlobby <arena> §7- Set arena lobby spawn");
        player.sendMessage("§c/bwadmin setspectator <arena> §7- Set spectator spawn");
        player.sendMessage("§c/bwadmin setteamspawn <arena> <team> §7- Set team spawn");
        player.sendMessage("§c/bwadmin setwaitingpos1 <arena> §7- Set waiting lobby pos 1");
        player.sendMessage("§c/bwadmin setwaitingpos2 <arena> §7- Set waiting lobby pos 2");
        player.sendMessage("§c/bwadmin addteam <arena> <team> §7- Add team");
        player.sendMessage("§c/bwadmin setbed <arena> <team> §7- Set bed location");
        player.sendMessage("§c/bwadmin setshop <arena> <team> §7- Set item shop");
        player.sendMessage("§c/bwadmin setupgradeshop <arena> <team> §7- Set upgrade shop");
        player.sendMessage("§c/bwadmin addiron <arena> <team> §7- Add iron generator");
        player.sendMessage("§c/bwadmin addgold <arena> <team> §7- Add gold generator");
        player.sendMessage("§c/bwadmin adddiamond <arena> [tier] §7- Add diamond gen");
        player.sendMessage("§c/bwadmin addemerald <arena> [tier] §7- Add emerald gen");
        player.sendMessage("§c/bwadmin save §7- Save all arenas to map files");
        player.sendMessage("§c/bwadmin reload §7- Reload config");
        player.sendMessage("§8§m                                        ");
    }
}