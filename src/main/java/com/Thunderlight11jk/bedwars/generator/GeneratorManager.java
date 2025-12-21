package com.Thunderlight11jk.bedwars.generator;

import com.Thunderlight11jk.bedwars.arena.Arena;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class GeneratorManager {

    private final Arena arena;
    private final JavaPlugin plugin;
    private final List<Generator> generators = new ArrayList<>();
    private BukkitRunnable task;
    private int gameTime = 0;

    public GeneratorManager(JavaPlugin plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
    }

    public void addGenerator(Generator generator) {
        generators.add(generator);
        generator.spawnHologram();
    }

    public Generator getGenerator(Location location) {
        for (Generator g : generators) {
            if (g.getLocation().equals(location)) return g;
        }
        return null;
    }

    public void removeGenerator(Generator generator) {
        generators.remove(generator);
        generator.remove(); // remove hologram if exists
    }

    public void start() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        };
        task.runTaskTimer(plugin, 20L, 20L);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
        }

        for (Generator generator : generators) {
            generator.remove();
        }
        generators.clear();
    }

    public void tick() {
        gameTime++;

        for (Generator generator : generators) {
            generator.tick();                 // update timer & hologram
            handleUpgrades(generator);        // upgrade tiers at the right time

            // Spawn item if ready
            if (generator.shouldSpawn()) {
                generator.spawnItem();        // respects maxItems automatically
                generator.resetTimer();       // reset timer for next spawn
            }
        }
    }

    private void handleUpgrades(Generator generator) {
        if (generator.getType() == GeneratorType.DIAMOND) {
            if (gameTime == 360) generator.upgradeTier(); // 6 min
            if (gameTime == 720) generator.upgradeTier(); // 12 min
        }

        if (generator.getType() == GeneratorType.EMERALD) {
            if (gameTime == 720) generator.upgradeTier(); // 12 min
            if (gameTime == 1080) generator.upgradeTier(); // 18 min
        }
    }
}
