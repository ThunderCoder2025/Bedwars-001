package com.Thunderlight11jk.bedwars.generator;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Data
public class Generator {

    private final GeneratorType type;
    private final Location location;
    private int tier;
    private int timer = 0;

    // Hologram above generator (for diamond/emerald)
    private GeneratorHologram hologram;

    // Track items spawned by this generator
    private final List<Item> spawnedItems = new ArrayList<>();

    public Generator(GeneratorType type, Location location, int tier) {
        this.type = type;
        this.location = location;
        this.tier = tier;
    }

    public void spawnHologram() {
        if (type == GeneratorType.IRON || type == GeneratorType.GOLD) return;
        this.hologram = new GeneratorHologram(location);
    }

    public void tick() {
        timer++;

        // Update hologram display
        if (hologram != null) {
            updateHologram();
        }
    }

    public boolean shouldSpawn() {
        return timer >= calculateDelay();
    }

    public void resetTimer() {
        timer = 0;
    }

    public void spawnItem() {
        // Clean up invalid/dead items
        spawnedItems.removeIf(item -> item == null || !item.isValid() || item.isDead());

        // Check limit
        if (spawnedItems.size() >= type.getMaxItems()) return;

        // Spawn new item
        Item item = location.getWorld().dropItemNaturally(
                location,
                new ItemStack(type.getMaterial().parseMaterial())
        );
        spawnedItems.add(item);
    }

    public void upgradeTier() {
        if (tier < 3) {
            tier++;
            if (hologram != null) updateHologram();
        }
    }

    public void remove() {
        if (hologram != null) {
            hologram.remove();
            hologram = null;
        }

        // Remove all spawned items
        for (Item item : spawnedItems) {
            if (item != null && item.isValid()) item.remove();
        }
        spawnedItems.clear();
    }

    private int calculateDelay() {
        int delay = type.getBaseDelay();

        if (type == GeneratorType.DIAMOND || type == GeneratorType.EMERALD) {
            if (tier == 2) delay /= 2;
            if (tier == 3) delay /= 3;
        }

        return delay;
    }

    private void updateHologram() {
        int remaining = calculateDelay() - timer;
        if (remaining < 0) remaining = 0;

        String title =
                (type == GeneratorType.DIAMOND ? "§bDiamond" : "§aEmerald")
                        + " §7" + roman(tier);

        String time = String.format(
                "§eSpawns in §c%d:%02d",
                remaining / 60,
                remaining % 60
        );

        hologram.update(title, time);
    }

    private String roman(int i) {
        switch (i) {
            case 2: return "II";
            case 3: return "III";
            default: return "I";
        }
    }
}
