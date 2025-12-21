package com.Thunderlight11jk.bedwars.arena;

import com.Thunderlight11jk.bedwars.BedWarsPlugin;
import com.Thunderlight11jk.bedwars.game.GameType;
import com.Thunderlight11jk.bedwars.game.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ArenaManager {

    private final BedWarsPlugin plugin;
    private final Map<String, Arena> arenas = new HashMap<>();
    private final File arenasDir;

    public ArenaManager(BedWarsPlugin plugin) {
        this.plugin = plugin;
        this.arenasDir = new File(plugin.getDataFolder(), "arenas");
        if (!arenasDir.exists()) {
            arenasDir.mkdirs();
        }
    }

    public void loadArenas() {
        arenas.clear();

        File[] files = arenasDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            try {
                FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection section = cfg.getConfigurationSection("arenas");
                if (section == null) continue;

                for (String key : section.getKeys(false)) {
                    try {
                        Arena arena = loadArena(section.getConfigurationSection(key));
                        if (arena != null) {
                            arenas.put(arena.getName(), arena);
                            plugin.getLogger().info("Loaded arena: " + arena.getName() + " from " + file.getName());
                        }
                    } catch (Exception e) {
                        plugin.getLogger().warning("Failed to load arena " + key + " from " + file.getName() + ": " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load file " + file.getName() + ": " + e.getMessage());
            }
        }
    }

    private Arena loadArena(ConfigurationSection section) {
        if (section == null) return null;

        String name = section.getName();
        Arena arena = new Arena(name);

        arena.setDisplayName(section.getString("display-name", name));

        String type = section.getString("game-type", "solos");
        GameType gameType;
        switch (type.toLowerCase()) {
            case "solos": gameType = GameType.SOLO; break;
            case "duos": gameType = GameType.DOUBLES; break;
            case "trios": gameType = GameType.TRIOS; break;
            case "quads": gameType = GameType.QUADS; break;
            case "4v4": gameType = GameType.FOUR_V_FOUR; break;
            default:
                plugin.getLogger().warning("Invalid game type in arena " + name + "! Available: solos, duos, trios, quads, 4v4");
                return null;
        }
        arena.setGameType(gameType);

        arena.setMinPlayers(section.getInt("min-players", 2));
        arena.setMaxPlayers(section.getInt("max-players", 16));

        String worldName = section.getString("world");
        if (worldName != null) {
            World world = Bukkit.getWorld(worldName);
            if (world != null) arena.setWorld(world);
        }

        arena.setLobbySpawn(deserializeLocation(section.getConfigurationSection("lobby-spawn")));
        arena.setSpectatorSpawn(deserializeLocation(section.getConfigurationSection("spectator-spawn")));
        arena.setWaitingPos1(deserializeLocation(section.getConfigurationSection("waiting-pos1")));
        arena.setWaitingPos2(deserializeLocation(section.getConfigurationSection("waiting-pos2")));

        ConfigurationSection teamsSection = section.getConfigurationSection("teams");
        if (teamsSection != null) {
            for (String teamKey : teamsSection.getKeys(false)) {
                try {
                    Team team = Team.valueOf(teamKey.toUpperCase());
                    TeamData data = loadTeamData(teamsSection.getConfigurationSection(teamKey));
                    arena.addTeam(team, data);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid team: " + teamKey);
                }
            }
        }

        ConfigurationSection diamondSection = section.getConfigurationSection("diamond-generators");
        if (diamondSection != null) {
            for (String key : diamondSection.getKeys(false)) {
                Location loc = deserializeLocation(diamondSection.getConfigurationSection(key + ".location"));
                int tier = diamondSection.getInt(key + ".tier", 1);
                if (loc != null) arena.getDiamondGenerators().add(new GeneratorLocation(loc, tier));
            }
        }

        ConfigurationSection emeraldSection = section.getConfigurationSection("emerald-generators");
        if (emeraldSection != null) {
            for (String key : emeraldSection.getKeys(false)) {
                Location loc = deserializeLocation(emeraldSection.getConfigurationSection(key + ".location"));
                int tier = emeraldSection.getInt(key + ".tier", 1);
                if (loc != null) arena.getEmeraldGenerators().add(new GeneratorLocation(loc, tier));
            }
        }

        return arena;
    }

    public void saveArenas() {
        for (Arena arena : arenas.values()) {
            File arenaFile = new File(arenasDir, arena.getName() + ".yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(arenaFile);

            ConfigurationSection arenaSection = cfg.createSection("arenas." + arena.getName());
            arenaSection.set("display-name", arena.getDisplayName());
            arenaSection.set("game-type", arena.getGameType().name().toLowerCase());
            arenaSection.set("min-players", arena.getMinPlayers());
            arenaSection.set("max-players", arena.getMaxPlayers());

            if (arena.getWorld() != null) arenaSection.set("world", arena.getWorld().getName());

            if (arena.getLobbySpawn() != null) serializeLocation(arenaSection, "lobby-spawn", arena.getLobbySpawn());
            if (arena.getSpectatorSpawn() != null) serializeLocation(arenaSection, "spectator-spawn", arena.getSpectatorSpawn());
            if (arena.getWaitingPos1() != null) serializeLocation(arenaSection, "waiting-pos1", arena.getWaitingPos1());
            if (arena.getWaitingPos2() != null) serializeLocation(arenaSection, "waiting-pos2", arena.getWaitingPos2());

            // Save teams
            for (Map.Entry<Team, TeamData> entry : arena.getTeams().entrySet()) {
                Team team = entry.getKey();
                TeamData data = entry.getValue();
                ConfigurationSection teamSection = arenaSection.createSection("teams." + team.name());

                if (data.getSpawnLocation() != null) serializeLocation(teamSection, "spawn", data.getSpawnLocation());
                if (data.getBedLocation() != null) serializeLocation(teamSection, "bed", data.getBedLocation());
                if (data.getShopLocation() != null) serializeLocation(teamSection, "shop", data.getShopLocation());
                if (data.getUpgradeShopLocation() != null) serializeLocation(teamSection, "upgrade-shop", data.getUpgradeShopLocation());

                // Iron generators
                ConfigurationSection ironSection = teamSection.createSection("iron-generators");
                int i = 0;
                for (Location loc : data.getIronGenerators()) {
                    serializeLocation(ironSection, String.valueOf(i), loc);
                    i++;
                }

                // Gold generators
                ConfigurationSection goldSection = teamSection.createSection("gold-generators");
                i = 0;
                for (Location loc : data.getGoldGenerators()) {
                    serializeLocation(goldSection, String.valueOf(i), loc);
                    i++;
                }
            }

            // Diamond generators
            ConfigurationSection diamondSection = arenaSection.createSection("diamond-generators");
            int i = 0;
            for (GeneratorLocation gen : arena.getDiamondGenerators()) {
                ConfigurationSection genSection = diamondSection.createSection(String.valueOf(i));
                serializeLocation(genSection.createSection("location"), "location", gen.getLocation());
                genSection.set("tier", gen.getTier());
                i++;
            }

            // Emerald generators
            ConfigurationSection emeraldSection = arenaSection.createSection("emerald-generators");
            i = 0;
            for (GeneratorLocation gen : arena.getEmeraldGenerators()) {
                ConfigurationSection genSection = emeraldSection.createSection(String.valueOf(i));
                serializeLocation(genSection.createSection("location"), "location", gen.getLocation());
                genSection.set("tier", gen.getTier());
                i++;
            }

            try {
                cfg.save(arenaFile);
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to save arena " + arena.getName() + ": " + e.getMessage());
            }
        }
    }

    private TeamData loadTeamData(ConfigurationSection section) {
        TeamData data = new TeamData();

        data.setSpawnLocation(deserializeLocation(section.getConfigurationSection("spawn")));
        data.setBedLocation(deserializeLocation(section.getConfigurationSection("bed")));
        data.setShopLocation(deserializeLocation(section.getConfigurationSection("shop")));
        data.setUpgradeShopLocation(deserializeLocation(section.getConfigurationSection("upgrade-shop")));

        ConfigurationSection ironSection = section.getConfigurationSection("iron-generators");
        if (ironSection != null) {
            for (String key : ironSection.getKeys(false)) {
                Location loc = deserializeLocation(ironSection.getConfigurationSection(key));
                if (loc != null) data.getIronGenerators().add(loc);
            }
        }

        ConfigurationSection goldSection = section.getConfigurationSection("gold-generators");
        if (goldSection != null) {
            for (String key : goldSection.getKeys(false)) {
                Location loc = deserializeLocation(goldSection.getConfigurationSection(key));
                if (loc != null) data.getGoldGenerators().add(loc);
            }
        }

        return data;
    }

    private Location deserializeLocation(ConfigurationSection section) {
        if (section == null) return null;
        String worldName = section.getString("world");
        if (worldName == null) return null;
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;

        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");
        float yaw = (float) section.getDouble("yaw");
        float pitch = (float) section.getDouble("pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }

    private void serializeLocation(ConfigurationSection config, String path, Location loc) {
        config.set(path + ".world", loc.getWorld().getName());
        config.set(path + ".x", loc.getX());
        config.set(path + ".y", loc.getY());
        config.set(path + ".z", loc.getZ());
        config.set(path + ".yaw", loc.getYaw());
        config.set(path + ".pitch", loc.getPitch());
    }


    public Arena getArena(String name) {
        return arenas.get(name);
    }

    public void addArena(Arena arena) {
        arenas.put(arena.getName(), arena);
        saveArenas();
    }

    public void removeArena(String name) {
        arenas.remove(name);
        saveArenas();
    }

    public Collection<Arena> getArenas() {
        return arenas.values();
    }

    public Arena getAvailableArena() {
        for (Arena arena : arenas.values()) {
            if (arena.isSetup() && !arena.isFull() && arena.getState().isJoinable()) {
                return arena;
            }
        }
        return null;
    }
}
