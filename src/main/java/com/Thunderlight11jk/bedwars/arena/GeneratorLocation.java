package com.Thunderlight11jk.bedwars.arena;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;

@Data
@AllArgsConstructor
public class GeneratorLocation {
    private Location location;
    private int tier;
}