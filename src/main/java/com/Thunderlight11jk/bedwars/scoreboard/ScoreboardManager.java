package com.Thunderlight11jk.bedwars.scoreboard;

import com.Thunderlight11jk.bedwars.BedWarsPlugin;
import com.Thunderlight11jk.bedwars.arena.Arena;
import com.Thunderlight11jk.bedwars.arena.TeamData;
import com.Thunderlight11jk.bedwars.game.GameState;
import com.Thunderlight11jk.bedwars.game.Team;
import com.Thunderlight11jk.bedwars.stats.PlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardManager {

    private final BedWarsPlugin plugin;
    private final Map<UUID, Scoreboard> scoreboards = new HashMap<>();
    private final Map<UUID, Boolean> lobbyScoreboards = new HashMap<>();

    // Unique entries for lines 1–15 (flicker-free)
    private static final String[] LINE_ENTRIES = new String[16];

    static {
        for (int i = 0; i < 16; i++) {
            LINE_ENTRIES[i] = ChatColor.values()[i].toString() + ChatColor.RESET;
        }
    }

    public ScoreboardManager(BedWarsPlugin plugin) {
        this.plugin = plugin;
    }

    /* ---------------------------------------------------
       SCOREBOARD CREATION
     --------------------------------------------------- */

    public void createScoreboard(Player player, Arena arena) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective("bedwars", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(
                plugin.getConfig().getString("scoreboard.title").replace("&", "§")
        );

        for (int i = 1; i <= 15; i++) {
            org.bukkit.scoreboard.Team team = board.registerNewTeam("line_" + i);
            team.addEntry(LINE_ENTRIES[i]);
        }

        player.setScoreboard(board);
        scoreboards.put(player.getUniqueId(), board);
        lobbyScoreboards.put(player.getUniqueId(), false);

        updateScoreboard(player, arena);
    }

    public void removeScoreboard(Player player) {
        scoreboards.remove(player.getUniqueId());
        lobbyScoreboards.remove(player.getUniqueId());
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    /* ---------------------------------------------------
       LOBBY SCOREBOARD
     --------------------------------------------------- */

    public boolean hasLobbyScoreboard(Player player) {
        return lobbyScoreboards.getOrDefault(player.getUniqueId(), false);
    }

    public void createLobbyScoreboard(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective("lobby", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§e§lBEDWARS");

        for (int i = 1; i <= 15; i++) {
            org.bukkit.scoreboard.Team team = board.registerNewTeam("line_" + i);
            team.addEntry(LINE_ENTRIES[i]);
        }

        player.setScoreboard(board);
        scoreboards.put(player.getUniqueId(), board);
        lobbyScoreboards.put(player.getUniqueId(), true);

        updateLobbyScoreboard(player);
    }

    public void updateLobbyScoreboard(Player player) {
        Scoreboard board = scoreboards.get(player.getUniqueId());
        if (board == null) return;

        PlayerStats stats = plugin.getStatsManager().getStats(player.getUniqueId());
        String serverIP = getServerIP();

        int line = 15;

        updateLine(board, line--, "§7" + formatDateTime());
        updateLine(board, line--, " ");

        updateLine(board, line--, "§fLevel: §a" + stats.getLevel());

        double ratio = stats.getLosses() == 0
                ? stats.getWins()
                : (double) stats.getWins() / stats.getLosses();

        int rounded = (int) Math.round(ratio);
        String arrow = ratio > rounded ? "↑" : ratio < rounded ? "↓" : "→";

        updateLine(board, line--,
                "§fW/L Ratio: §b" + String.format("%.1f", ratio) + " " + arrow
        );

        updateLine(board, line--, " ");
        updateLine(board, line--, "§eWinstreak: " + stats.getWinStreak());
        updateLine(board, line--, "§fCoins: §7TODO");
        updateLine(board, line--, " ");

        updateLine(board, line--, "§fTotal Kills: §a" + stats.getKills());
        updateLine(board, line--, "§fTotal Wins: §a" + stats.getWins());
        updateLine(board, line--, " ");

        updateLine(board, line--, "§e");

        while (line > 0) clearLine(board, line--);
    }

    /* ---------------------------------------------------
       IN-GAME SCOREBOARD
     --------------------------------------------------- */

    public void updateScoreboard(Player player, Arena arena) {
        Scoreboard board = scoreboards.get(player.getUniqueId());
        if (board == null) return;

        int line = 15;

        // WAITING / STARTING
        if (arena.getState() == GameState.WAITING ||
                arena.getState() == GameState.STARTING) {

            int maxPlayers = getMaxPlayersForMode(arena);

            updateLine(board, line--, "§7" + formatDate());
            updateLine(board, line--, " ");

            updateLine(board, line--, "§fMap: §a" + arena.getDisplayName());
            updateLine(board, line--,
                    "§fPlayers: §a" + arena.getPlayerCount() + "/" + maxPlayers
            );

            updateLine(board, line--, " ");

            if (arena.getState() == GameState.STARTING) {
                int time = plugin.getGameManager()
                        .getSession(arena)
                        .getCountdown();

                updateLine(board, line--, "§fStarting in §a" + time + "s");
            } else {
                updateLine(board, line--, "§fWaiting...");
            }

        }
        // ACTIVE (stub – we’ll improve next)
        else if (arena.getState() == GameState.ACTIVE) {

            // Title spacing
            updateLine(board, line--, "§7" + formatDate());
            updateLine(board, line--, " ");

            // Generator status (stub – hook later)
            updateLine(board, line--, "§fDiamond II in §a0:14");
            updateLine(board, line--, " ");

            Team playerTeam = plugin.getGameManager().getPlayerTeam(player);

            // Teams in fixed Hypixel order
            Team[] order = {
                    Team.RED,
                    Team.BLUE,
                    Team.GREEN,
                    Team.YELLOW,
                    Team.AQUA,
                    Team.WHITE,
                    Team.PINK,
                    Team.GRAY
            };

            for (Team team : order) {
                TeamData data = arena.getTeamData(team);
                if (data == null) continue;

                boolean isPlayerTeam = team == playerTeam;

                String status;
                if (data.isBedAlive()) {
                    status = isPlayerTeam ? "§a✓ §fYOU" : "§a✓";
                } else if (data.hasPlayers()) {
                    status = "§c" + data.getPlayerCount();
                } else {
                    status = "§c✗";
                }

                String lineText =
                        team.getColor()
                                + team.getDisplayName().substring(0, 1)
                                + " §f"
                                + team.getDisplayName()
                                + ": "
                                + status;

                updateLine(board, line--, lineText);
            }

            updateLine(board, line--, " ");
            updateLine(board, line--, "§ewww.example.net");
        }

        // GAME OVER
        else {
            updateLine(board, line--, "§7" + formatDate());
            updateLine(board, line--, " ");
            updateLine(board, line--, "§eGame Over");
        }

        updateLine(board, line--, " ");
        updateLine(board, line--, "§e---Bedwars-001---");

        while (line > 0) clearLine(board, line--);
    }

    public void updateAllScoreboards(Arena arena) {
        for (UUID uuid : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && scoreboards.containsKey(uuid)) {
                updateScoreboard(player, arena);
            }
        }
    }

    /* ---------------------------------------------------
       HELPERS
     --------------------------------------------------- */

    private int getMaxPlayersForMode(Arena arena) {
        if (arena.getGameType() == null) return arena.getMaxPlayers();

        switch (arena.getGameType()) {
            case SOLO:
                return 8;
            case DOUBLES:
                return 16;
            case TRIOS:
                return 12;
            case QUADS:
                return 16;
            case FOUR_V_FOUR:
                return 8;
            default:
                return arena.getMaxPlayers();
        }
    }

    private void updateLine(Scoreboard board, int line, String text) {
        if (line < 1 || line > 15) return;

        org.bukkit.scoreboard.Team team = board.getTeam("line_" + line);
        if (team == null) return;

        String prefix = text.length() <= 16 ? text : text.substring(0, 16);
        String suffix = text.length() > 16
                ? ChatColor.getLastColors(prefix) + text.substring(16)
                : "";

        if (suffix.length() > 16) suffix = suffix.substring(0, 16);

        team.setPrefix(prefix);
        team.setSuffix(suffix);

        Objective obj = board.getObjective(DisplaySlot.SIDEBAR);
        if (obj != null) obj.getScore(LINE_ENTRIES[line]).setScore(line);
    }

    private void clearLine(Scoreboard board, int line) {
        org.bukkit.scoreboard.Team team = board.getTeam("line_" + line);
        if (team != null) {
            team.setPrefix("");
            team.setSuffix("");
        }
    }

    private String formatDate() {
        return new java.text.SimpleDateFormat("MM/dd/yy")
                .format(new java.util.Date());
    }

    private String formatDateTime() {
        return new java.text.SimpleDateFormat("dd/MM/yy hh:mma")
                .format(new java.util.Date()).toLowerCase();
    }

    private String getServerIP() {
        String ip = Bukkit.getIp();
        if (ip == null || ip.isEmpty() || ip.equals("0.0.0.0")) {
            return "Bedwars-001";
        }

        String full = ip + ":" + Bukkit.getPort();
        return full.length() > 15 ? "Bedwars-001" : full;
    }
}
