package com.Thunderlight11jk.bedwars.arena;

import com.Thunderlight11jk.bedwars.generator.Generator;

import java.util.ArrayList;
import java.util.List;

public class ArenaGeneratorManager {

    private final Arena arena;
    private final List<Generator> generators = new ArrayList<>();

    public ArenaGeneratorManager(Arena arena) {
        this.arena = arena;
    }

    public void start() {
        for (Generator gen : generators) {
            gen.spawnHologram(); // ✅ HERE
        }
    }

    public void tick() {
        for (Generator gen : generators) {
            gen.tick();

            if (gen.shouldSpawn()) {
                gen.spawnItem();
                gen.resetTimer();
            }
        }
    }

    public void stop() {
        for (Generator gen : generators) {
            if (gen.getHologram() != null) {
                gen.getHologram().remove();
            }
        }
    }
}

