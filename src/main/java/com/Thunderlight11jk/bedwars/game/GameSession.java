package com.Thunderlight11jk.bedwars.game;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import com.cryptomorin.xseries.XSound;
import com.Thunderlight11jk.bedwars.BedWarsPlugin;
import com.Thunderlight11jk.bedwars.arena.Arena;
import com.Thunderlight11jk.bedwars.arena.GeneratorLocation;
import com.Thunderlight11jk.bedwars.arena.TeamData;
import com.Thunderlight11jk.bedwars.generator.Generator;
import com.Thunderlight11jk.bedwars.generator.GeneratorType;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class GameSession {

    private final BedWarsPlugin plugin;
    private final Arena arena;
    private final List<Generator> generators = new ArrayList<>();
    private final Map<UUID, PlayerData> playerData = new HashMap<>();

    private int countdown = -1;
    private int gameTime = 0;
    private int diamondTier = 1;
    private int emeraldTier = 1;

    public GameSession(BedWarsPlugin plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
    }

    public void addPlayer(Player player) {
        playerData.put(player.getUniqueId(), new PlayerData(player.getUniqueId()));
        plugin.getScoreboardManager().createScoreboard(player, arena);

        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
    }

    public void removePlayer(Player player) {
        playerData.remove(player.getUniqueId());
        plugin.getScoreboardManager().removeScoreboard(player);

        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
    }

    public void startCountdown() {
        if (arena.getState() != GameState.WAITING) return;

        arena.setState(GameState.STARTING);
        countdown = plugin.getConfig().getInt("countdown-time", 20);

        broadcast("Game starting in " + countdown + " seconds!");
    }

    public void startGame() {
        arena.setState(GameState.ACTIVE);
        gameTime = 0;

        clearWaitingRegion();

        for (Map.Entry<Team, TeamData> entry : arena.getTeams().entrySet()) {
            entry.getValue().reset();
        }

        assignTeams();
        setupGenerators();
        teleportPlayers();
        giveStartingItems();

        broadcast(plugin.getConfig().getString("messages.game-start"));

        for (UUID playerId : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                XSound.matchXSound("ENTITY_ENDER_DRAGON_GROWL").ifPresent(s -> s.play(player));
            }
        }
    }

    private void assignTeams() {
        List<Player> unassigned = new ArrayList<>();

        for (UUID playerId : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player == null) continue;

            Team team = plugin.getGameManager().getPlayerTeam(player);
            if (team == null) {
                unassigned.add(player);
            }
        }

        List<Team> availableTeams = new ArrayList<>(arena.getTeams().keySet());
        Collections.shuffle(availableTeams);

        int teamIndex = 0;
        for (Player player : unassigned) {
            Team team = availableTeams.get(teamIndex % availableTeams.size());
            plugin.getGameManager().selectTeam(player, team);
            teamIndex++;
        }
    }

    private void setupGenerators() {
        generators.clear();

        for (Map.Entry<Team, TeamData> entry : arena.getTeams().entrySet()) {
            TeamData data = entry.getValue();

            for (Location loc : data.getIronGenerators()) {
                generators.add(new Generator(GeneratorType.IRON, loc, 1));
            }

            for (Location loc : data.getGoldGenerators()) {
                generators.add(new Generator(GeneratorType.GOLD, loc, 1));
            }
        }

        for (GeneratorLocation gen : arena.getDiamondGenerators()) {
            generators.add(new Generator(GeneratorType.DIAMOND, gen.getLocation(), gen.getTier()));
        }

        for (GeneratorLocation gen : arena.getEmeraldGenerators()) {
            generators.add(new Generator(GeneratorType.EMERALD, gen.getLocation(), gen.getTier()));
        }
    }

    private void teleportPlayers() {
        for (UUID playerId : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player == null) continue;

            Team team = plugin.getGameManager().getPlayerTeam(player);
            if (team == null) continue;

            TeamData data = arena.getTeamData(team);
            if (data == null) continue;

            player.teleport(data.getSpawnLocation());
        }
    }

    private void giveStartingItems() {
        for (UUID playerId : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player == null) continue;

            Team team = plugin.getGameManager().getPlayerTeam(player);
            if (team == null) continue;

            player.getInventory().clear();

            ItemStack sword = XMaterial.WOODEN_SWORD.parseItem();
            player.getInventory().addItem(sword);

            ItemStack[] armor = new ItemStack[4];
            armor[3] = createColoredArmor(XMaterial.LEATHER_HELMET, team);
            armor[2] = createColoredArmor(XMaterial.LEATHER_CHESTPLATE, team);
            armor[1] = createColoredArmor(XMaterial.LEATHER_LEGGINGS, team);
            armor[0] = createColoredArmor(XMaterial.LEATHER_BOOTS, team);

            player.getInventory().setArmorContents(armor);
        }
    }

    private ItemStack createColoredArmor(XMaterial material, Team team) {
        ItemStack item = material.parseItem();
        if (item != null && item.getItemMeta() instanceof org.bukkit.inventory.meta.LeatherArmorMeta) {
            org.bukkit.inventory.meta.LeatherArmorMeta meta = (org.bukkit.inventory.meta.LeatherArmorMeta) item.getItemMeta();

            Color color = Color.WHITE;
            switch (team) {
                case RED: color = Color.RED; break;
                case BLUE: color = Color.BLUE; break;
                case GREEN: color = Color.GREEN; break;
                case YELLOW: color = Color.YELLOW; break;
                case AQUA: color = Color.AQUA; break;
                case WHITE: color = Color.WHITE; break;
                case PINK: color = Color.FUCHSIA; break;
                case GRAY: color = Color.GRAY; break;
            }

            meta.setColor(color);
            item.setItemMeta(meta);
        }
        return item;
    }

    public void tick() {
        if (arena.getState() == GameState.STARTING) {
            handleCountdown();
        } else if (arena.getState() == GameState.ACTIVE) {
            handleGame();
        }
    }

    private void handleCountdown() {
        if (countdown <= 0) {
            startGame();
            return;
        }

        if (arena.getPlayerCount() < arena.getMinPlayers()) {
            arena.setState(GameState.WAITING);
            countdown = -1;
            broadcast("Not enough players! Countdown cancelled.");
            return;
        }

        if (countdown == 40 || countdown == 20 || countdown == 10 ||
                countdown == 5 || countdown == 4 || countdown == 3 ||
                countdown == 2 || countdown == 1) {

            String color;
            if (countdown <= 5) {
                color = "§c§l";
            } else if (countdown <= 10) {
                color = "§e§l";
            } else {
                color = "§a§l";
            }

            String title = color + countdown;

            for (UUID playerId : arena.getPlayers()) {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null) {
                    player.sendTitle(title, "");
                    XSound.matchXSound("BLOCK_NOTE_BLOCK_PLING").ifPresent(s -> s.play(player));
                }
            }
        }

        if ((countdown > 10 && countdown % 10 == 0) || (countdown <= 10 && countdown > 5)) {
            broadcast("Game starting in " + countdown + " seconds!");
        }

        countdown--;
    }

    private void handleGame() {
        gameTime++;

        for (Generator generator : generators) {
            generator.tick();

            if (generator.shouldSpawn()) {
                spawnItem(generator.getLocation(), generator.getType().getMaterial());
                generator.resetTimer();
            }
        }

        for (UUID playerId : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player == null) continue;

            Team team = plugin.getGameManager().getPlayerTeam(player);
            if (team == null) continue;

            TeamData teamData = arena.getTeamData(team);
            if (teamData == null) continue;

            if (teamData.isHasteEnabled() && isNearBase(player, teamData)) {
                XPotion.matchXPotion("HASTE").map(xp -> xp.buildPotionEffect(40, 0)).ifPresent(player::addPotionEffect);
            }

            if (teamData.isHealPoolEnabled() && isNearBase(player, teamData)) {
                XPotion.matchXPotion("REGENERATION").map(xp -> xp.buildPotionEffect(40, 0)).ifPresent(player::addPotionEffect);
            }
        }

        plugin.getScoreboardManager().updateAllScoreboards(arena);
    }

    private boolean isNearBase(Player player, TeamData teamData) {
        if (teamData.getSpawnLocation() == null) return false;
        return player.getLocation().distance(teamData.getSpawnLocation()) < 15;
    }

    private void clearWaitingRegion() {
        Location pos1 = arena.getWaitingPos1();
        Location pos2 = arena.getWaitingPos2();

        if (pos1 == null || pos2 == null) {
            return;
        }

        if (!pos1.getWorld().equals(pos2.getWorld())) {
            return;
        }

        World world = pos1.getWorld();

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        broadcast(plugin.getPrefix() + "Clearing waiting region...");

        final int[] cleared = {0};
        final int[] currentX = {minX};
        final int[] currentY = {minY};
        final int[] currentZ = {minZ};
        final BukkitTask[] taskRef = new BukkitTask[1];

        taskRef[0] = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                int blocksPerTick = 100;
                int processed = 0;

                while (processed < blocksPerTick && currentX[0] <= maxX) {
                    world.getBlockAt(currentX[0], currentY[0], currentZ[0]).setType(Material.AIR);
                    cleared[0]++;
                    processed++;

                    currentZ[0]++;
                    if (currentZ[0] > maxZ) {
                        currentZ[0] = minZ;
                        currentY[0]++;
                        if (currentY[0] > maxY) {
                            currentY[0] = minY;
                            currentX[0]++;
                        }
                    }
                }

                if (currentX[0] > maxX) {
                    taskRef[0].cancel();
                    broadcast(plugin.getPrefix() + "Cleared " + cleared[0] + " blocks in the waiting region!");
                }
            }
        }, 0L, 1L);
    }

    private void spawnItem(Location location, XMaterial material) {
        ItemStack item = material.parseItem();
        if (item == null) return;

        Collection<Item> nearbyItems = location.getWorld().getNearbyEntities(location, 1, 1, 1).stream()
                .filter(e -> e instanceof Item)
                .map(e -> (Item) e)
                .filter(i -> i.getItemStack().getType() == item.getType())
                .collect(java.util.stream.Collectors.toList());

        int totalAmount = nearbyItems.stream().mapToInt(i -> i.getItemStack().getAmount()).sum();

        int maxStack = material == XMaterial.IRON_INGOT ? 48 :
                material == XMaterial.GOLD_INGOT ? 12 :
                        material == XMaterial.DIAMOND ? 8 : 4;

        if (totalAmount >= maxStack) {
            return;
        }

        Item dropped = location.getWorld().dropItem(location.add(0, 0.5, 0), item);
        dropped.setVelocity(dropped.getVelocity().zero());
        dropped.setPickupDelay(0);
    }

    public void handlePlayerDeath(Player player, Player killer) {
        PlayerData data = playerData.get(player.getUniqueId());
        Team team = plugin.getGameManager().getPlayerTeam(player);

        if (team == null) return;

        TeamData teamData = arena.getTeamData(team);

        boolean isFinalKill = !teamData.isBedAlive();

        if (isFinalKill) {
            data.setAlive(false);
            player.setGameMode(GameMode.SPECTATOR);

            String message = plugin.getConfig().getString("messages.final-kill")
                    .replace("{victim}", player.getName())
                    .replace("{killer}", killer != null ? killer.getName() : "unknown");
            broadcast(message);

            checkWinCondition();
        } else {
            data.setRespawnTime(5);
            player.setGameMode(GameMode.SPECTATOR);


            String message = plugin.getConfig().getString("messages.player-eliminated")
                    .replace("{player}", player.getName())
                    .replace("{killer}", killer != null ? killer.getName() : "unknown");
            broadcast(message);
        }

        player.getInventory().clear();
    }

    public void handleRespawn(Player player) {
        PlayerData data = playerData.get(player.getUniqueId());
        if (data == null) return;

        if (data.getRespawnTime() > 0) {
            data.setRespawnTime(data.getRespawnTime() - 1);

            player.sendTitle(
                    plugin.getConfig().getString("messages.respawn-title"),
                    plugin.getConfig().getString("messages.respawn-subtitle")
                            .replace("{time}", String.valueOf(data.getRespawnTime()))
            );

            if (data.getRespawnTime() == 0) {
                respawnPlayer(player);
            }
        }
    }

    private void respawnPlayer(Player player) {
        Team team = plugin.getGameManager().getPlayerTeam(player);
        if (team == null) return;

        TeamData teamData = arena.getTeamData(team);
        if (teamData == null) return;

        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(teamData.getSpawnLocation());
        player.setHealth(20);
        player.setFoodLevel(20);

        giveStartingItems();
    }

    public void handleBedBreak(Team team, Player breaker) {
        TeamData teamData = arena.getTeamData(team);
        if (teamData == null) return;

        teamData.setBedAlive(false);

        String message = plugin.getConfig().getString("messages.bed-destroyed")
                .replace("{team}", team.getColoredName())
                .replace("{player}", breaker.getName());
        broadcast(message);

        for (UUID playerId : teamData.getPlayers()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                player.sendTitle(
                        "BED DESTROYED",
                        "You cannot respawn again!"
                );
                XSound.matchXSound("ENTITY_WITHER_DEATH").ifPresent(s -> s.play(player));
            }
        }

        checkWinCondition();
    }

    public void checkWinCondition() {
        List<Team> aliveTeams = new ArrayList<>();

        for (Map.Entry<Team, TeamData> entry : arena.getTeams().entrySet()) {
            TeamData data = entry.getValue();

            if (data.isBedAlive() || hasAlivePlayers(data)) {
                aliveTeams.add(entry.getKey());
            }
        }

        if (aliveTeams.size() == 1) {
            endGame(aliveTeams.get(0));
        } else if (aliveTeams.size() == 0) {
            endGame(null);
        }
    }

    private boolean hasAlivePlayers(TeamData data) {
        for (UUID playerId : data.getPlayers()) {
            PlayerData pData = playerData.get(playerId);
            if (pData != null && pData.isAlive()) {
                return true;
            }
        }
        return false;
    }

    public void endGame(Team winner) {
        arena.setState(GameState.ENDING);

        if (winner != null) {
            String message = plugin.getConfig().getString("messages.victory")
                    .replace("{team}", winner.getColoredName());
            broadcast(message);

            TeamData winnerData = arena.getTeamData(winner);
            if (winnerData != null) {
                for (UUID playerId : winnerData.getPlayers()) {
                    plugin.getStatsManager().addWin(playerId);
                }
            }

            for (Map.Entry<Team, TeamData> entry : arena.getTeams().entrySet()) {
                if (entry.getKey() != winner) {
                    TeamData teamData = entry.getValue();
                    if (teamData != null) {
                        for (UUID playerId : teamData.getPlayers()) {
                            plugin.getStatsManager().addLoss(playerId);
                        }
                    }
                }
            }
        } else {
            broadcast("DRAW!");

            for (UUID playerId : arena.getPlayers()) {
                plugin.getStatsManager().addLoss(playerId);
            }
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (UUID playerId : new ArrayList<>(arena.getPlayers())) {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null) {
                    plugin.getGameManager().leaveArena(player);

                    String lobbyWorld = plugin.getConfig().getString("lobby-world", "world");
                    if (player.getWorld().getName().equalsIgnoreCase(lobbyWorld)) {
                        plugin.getScoreboardManager().createLobbyScoreboard(player);
                    }
                }
            }

            arena.setState(GameState.WAITING);
            arena.getPlayers().clear();
            playerData.clear();
            generators.clear();

            plugin.getGameManager().removeSession(arena);
        }, 200L);
    }

    private void broadcast(String message) {
        for (UUID playerId : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                player.sendMessage(message.replace("&", "§"));
            }
        }
    }

    public Arena getArena() {
        return arena;
    }

    public PlayerData getPlayerData(UUID playerId) {
        return playerData.get(playerId);
    }

    public int getCountdown() {
        return countdown;
    }

    public int getGameTime() {
        return gameTime;
    }
}