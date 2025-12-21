package com.Thunderlight11jk.bedwars.game;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
public enum Team {
    RED(ChatColor.RED, "Red", XMaterial.RED_WOOL, XMaterial.RED_STAINED_GLASS),
    BLUE(ChatColor.BLUE, "Blue", XMaterial.BLUE_WOOL, XMaterial.BLUE_STAINED_GLASS),
    GREEN(ChatColor.GREEN, "Green", XMaterial.GREEN_WOOL, XMaterial.GREEN_STAINED_GLASS),
    YELLOW(ChatColor.YELLOW, "Yellow", XMaterial.YELLOW_WOOL, XMaterial.YELLOW_STAINED_GLASS),
    AQUA(ChatColor.AQUA, "Aqua", XMaterial.CYAN_WOOL, XMaterial.CYAN_STAINED_GLASS),
    WHITE(ChatColor.WHITE, "White", XMaterial.WHITE_WOOL, XMaterial.WHITE_STAINED_GLASS),
    PINK(ChatColor.LIGHT_PURPLE, "Pink", XMaterial.PINK_WOOL, XMaterial.PINK_STAINED_GLASS),
    GRAY(ChatColor.GRAY, "Gray", XMaterial.GRAY_WOOL, XMaterial.GRAY_STAINED_GLASS);
    
    private final ChatColor color;
    private final String displayName;
    private final XMaterial wool;
    private final XMaterial glass;
    
    Team(ChatColor color, String displayName, XMaterial wool, XMaterial glass) {
        this.color = color;
        this.displayName = displayName;
        this.wool = wool;
        this.glass = glass;
    }
    
    public String getColoredName() {
        return color + displayName;
    }
}