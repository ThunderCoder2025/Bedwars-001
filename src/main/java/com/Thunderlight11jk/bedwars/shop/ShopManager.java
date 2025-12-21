package com.Thunderlight11jk.bedwars.shop;

import com.Thunderlight11jk.bedwars.BedWarsPlugin;
import com.Thunderlight11jk.bedwars.inventory.impl.ItemShopGUI;
import com.Thunderlight11jk.bedwars.inventory.impl.UpgradeShopGUI;
import org.bukkit.entity.Player;

public class ShopManager {
    
    private final BedWarsPlugin plugin;
    
    public ShopManager(BedWarsPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void openItemShop(Player player) {
        ItemShopGUI gui = new ItemShopGUI(plugin);
        plugin.getGuiManager().openGUI(gui, player);
    }
    
    public void openUpgradeShop(Player player) {
        UpgradeShopGUI gui = new UpgradeShopGUI(plugin);
        plugin.getGuiManager().openGUI(gui, player);
    }
}