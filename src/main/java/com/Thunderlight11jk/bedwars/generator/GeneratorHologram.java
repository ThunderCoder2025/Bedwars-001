package com.Thunderlight11jk.bedwars.generator;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

@Getter
public class GeneratorHologram {

    private final ArmorStand title;
    private final ArmorStand timer;

    public GeneratorHologram(Location base) {
        Location titleLoc = base.clone().add(0, 2.3, 0);
        Location timerLoc = base.clone().add(0, 2.05, 0);

        title = spawnStand(titleLoc);
        timer = spawnStand(timerLoc);
    }

    private ArmorStand spawnStand(Location loc) {
        ArmorStand as = loc.getWorld().spawn(loc, ArmorStand.class);
        as.setVisible(false);
        as.setGravity(false);
        as.setMarker(true);
        as.setCustomNameVisible(true);
        return as;
    }

    public void update(String titleText, String timerText) {
        title.setCustomName(titleText);
        timer.setCustomName(timerText);
    }

    public void remove() {
        if (title != null && !title.isDead()) title.remove();
        if (timer != null && !timer.isDead()) timer.remove();
    }
}
