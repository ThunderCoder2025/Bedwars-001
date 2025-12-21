package com.Thunderlight11jk.bedwars;

import com.Thunderlight11jk.bedwars.arena.ArenaManager;
import com.Thunderlight11jk.bedwars.commands.BedWarsCommand;
import com.Thunderlight11jk.bedwars.commands.BedWarsAdminCommand;
import com.Thunderlight11jk.bedwars.game.GameManager;
import com.Thunderlight11jk.bedwars.inventory.gui.GUIListener;
import com.Thunderlight11jk.bedwars.inventory.gui.GUIManager;
import com.Thunderlight11jk.bedwars.listeners.*;
import com.Thunderlight11jk.bedwars.scoreboard.ScoreboardManager;
import com.Thunderlight11jk.bedwars.shop.ShopManager;
import com.Thunderlight11jk.bedwars.stats.StatsManager;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class BedWarsPlugin extends JavaPlugin {
    
    private static BedWarsPlugin instance;
    
    private ArenaManager arenaManager;
    private GameManager gameManager;
    private GUIManager guiManager;
    private ScoreboardManager scoreboardManager;
    private ShopManager shopManager;
    private StatsManager statsManager;
    private Economy economy;
    
    @Override
    public void onEnable() {
        instance = this;
        
        saveDefaultConfig();
        
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            setupEconomy();
        }
        
        this.arenaManager = new ArenaManager(this);
        this.gameManager = new GameManager(this);
        this.guiManager = new GUIManager();
        this.scoreboardManager = new ScoreboardManager(this);
        this.shopManager = new ShopManager(this);
        this.statsManager = new StatsManager(this);
        
        registerCommands();
        registerListeners();
        
        arenaManager.loadArenas();
        
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            gameManager.tick();
        }, 20L, 20L);
        
        // Update lobby scoreboards periodically
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            String lobbyWorld = getConfig().getString("lobby-world", "world");
            Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getWorld().getName().equalsIgnoreCase(lobbyWorld))
                .filter(scoreboardManager::hasLobbyScoreboard)
                .forEach(scoreboardManager::updateLobbyScoreboard);
        }, 20L, 20L);
        
        getLogger().info("Bedwars-001 has been enabled!");
    }
    
    @Override
    public void onDisable() {
        if (gameManager != null) {
            gameManager.shutdown();
        }
        if (arenaManager != null) {
            arenaManager.saveArenas();
        }
        if (statsManager != null) {
            statsManager.saveStats();
        }
        getLogger().info("Bedwars-001 has been disabled!");
    }
    
    private void registerCommands() {
        getCommand("bw").setExecutor(new BedWarsCommand(this));
        getCommand("bwadmin").setExecutor(new BedWarsAdminCommand(this));
    }
    
    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new GUIListener(guiManager), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerRespawnListener(this), this);
        Bukkit.getPluginManager().registerEvents(new EntitySpawnListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDropItemListener(this), this);
        Bukkit.getPluginManager().registerEvents(new FoodLevelChangeListener(this), this);
        Bukkit.getPluginManager().registerEvents(new TabListListener(this), this);
    }
    
    private void setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            economy = rsp.getProvider();
        }
    }
    
    public static BedWarsPlugin getInstance() {
        return instance;
    }
    
    public String getPrefix() {
        return getConfig().getString("prefix", "&8[&c&lBEDWARS&8] &r").replace("&", "§");
    }
    
    public Location getLobbyLocation() {
        FileConfiguration config = getConfig();
        String worldName = config.getString("lobby-location.world");
        if (worldName == null) {
            worldName = config.getString("lobby-world", "world");
        }
        
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            world = Bukkit.getWorlds().get(0);
        }
        
        double x = config.getDouble("lobby-location.x", 0.0);
        double y = config.getDouble("lobby-location.y", 64.0);
        double z = config.getDouble("lobby-location.z", 0.0);
        float yaw = (float) config.getDouble("lobby-location.yaw", 0.0);
        float pitch = (float) config.getDouble("lobby-location.pitch", 0.0);
        
        return new Location(world, x, y, z, yaw, pitch);
    }
    
    public void setLobbyLocation(Location location) {
        FileConfiguration config = getConfig();
        config.set("lobby-location.world", location.getWorld().getName());
        config.set("lobby-location.x", location.getX());
        config.set("lobby-location.y", location.getY());
        config.set("lobby-location.z", location.getZ());
        config.set("lobby-location.yaw", location.getYaw());
        config.set("lobby-location.pitch", location.getPitch());
        saveConfig();
    }
    
    public boolean isInLobbyWorld(org.bukkit.entity.Player player) {
        FileConfiguration config = getConfig();
        String worldName = config.getString("lobby-location.world");
        if (worldName == null || worldName.isEmpty()) {
            worldName = config.getString("lobby-world", "world");
        }
        return player.getWorld().getName().equalsIgnoreCase(worldName);
    }
}