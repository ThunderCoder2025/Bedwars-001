package com.Thunderlight11jk.bedwars.inventory.impl;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.Thunderlight11jk.bedwars.BedWarsPlugin;
import com.Thunderlight11jk.bedwars.arena.Arena;
import com.Thunderlight11jk.bedwars.arena.TeamData;
import com.Thunderlight11jk.bedwars.game.Team;
import com.Thunderlight11jk.bedwars.inventory.InventoryButton;
import com.Thunderlight11jk.bedwars.inventory.InventoryGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class UpgradeShopGUI extends InventoryGUI {
    
    private final BedWarsPlugin plugin;
    
    public UpgradeShopGUI(BedWarsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 54, plugin.getConfig().getString("shops.upgrade-shop-title").replace("&", "§"));
    }
    
    @Override
    public void decorate(Player player) {
        Arena arena = plugin.getGameManager().getPlayerArena(player);
        Team team = plugin.getGameManager().getPlayerTeam(player);
        
        if (arena == null || team == null) {
            return;
        }
        
        TeamData teamData = arena.getTeamData(team);
        if (teamData == null) {
            return;
        }
        
        addButton(11, new InventoryButton()
            .creator(p -> createUpgradeItem(XMaterial.IRON_SWORD, "§aSharpened Swords", 
                teamData.getSharpness(), 4, "§7Cost: §b" + getSharpnessCost(teamData.getSharpness()) + " Diamonds"))
            .consumer(event -> purchaseSharpness(player, teamData))
        );
        
        addButton(13, new InventoryButton()
            .creator(p -> createUpgradeItem(XMaterial.IRON_CHESTPLATE, "§aReinforced Armor", 
                teamData.getProtection(), 4, "§7Cost: §b" + getProtectionCost(teamData.getProtection()) + " Diamonds"))
            .consumer(event -> purchaseProtection(player, teamData))
        );
        
        addButton(15, new InventoryButton()
            .creator(p -> createUpgradeItem(XMaterial.GOLDEN_PICKAXE, "§aManiac Miner", 
                teamData.isHasteEnabled() ? 1 : 0, 1, "§7Cost: §b" + (teamData.isHasteEnabled() ? "PURCHASED" : "4 Diamonds")))
            .consumer(event -> purchaseHaste(player, teamData))
        );
        
        addButton(20, new InventoryButton()
            .creator(p -> createUpgradeItem(XMaterial.FURNACE, "§aIron Forge", 
                teamData.getForgeLevel(), 4, "§7Cost: §b" + getForgeCost(teamData.getForgeLevel()) + " Diamonds"))
            .consumer(event -> purchaseForge(player, teamData))
        );
        
        addButton(22, new InventoryButton()
            .creator(p -> createUpgradeItem(XMaterial.BEACON, "§aHeal Pool", 
                teamData.isHealPoolEnabled() ? 1 : 0, 1, "§7Cost: §b" + (teamData.isHealPoolEnabled() ? "PURCHASED" : "3 Diamonds")))
            .consumer(event -> purchaseHealPool(player, teamData))
        );
        
        addButton(29, new InventoryButton()
            .creator(p -> createItem(XMaterial.TRIPWIRE_HOOK, "§aIt's a trap!", 1, 
                "§7Cost: §b1 Diamond", "§7Traps: " + teamData.getTraps().size() + "/3"))
            .consumer(event -> purchaseTrap(player, teamData, "alarm"))
        );
        
        super.decorate(player);
    }
    
    private ItemStack createUpgradeItem(XMaterial material, String name, int level, int maxLevel, String cost) {
        ItemStack item = material.parseItem();
        if (item == null) return new ItemStack(Material.STONE);
        
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name.replace("&", "§"));
        
        String tierInfo = level >= maxLevel ? "§aMAX" : "§7Tier: §e" + level + "§7/§e" + maxLevel;
        meta.setLore(Arrays.asList(tierInfo, cost.replace("&", "§")));
        item.setItemMeta(meta);
        return item;
    }
    
    private ItemStack createItem(XMaterial material, String name, int amount, String... lore) {
        ItemStack item = material.parseItem();
        if (item == null) return new ItemStack(Material.STONE);
        
        item.setAmount(amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name.replace("&", "§"));
        
        String[] coloredLore = new String[lore.length];
        for (int i = 0; i < lore.length; i++) {
            coloredLore[i] = lore[i].replace("&", "§");
        }
        meta.setLore(Arrays.asList(coloredLore));
        item.setItemMeta(meta);
        return item;
    }
    
    private void purchaseSharpness(Player player, TeamData teamData) {
        if (teamData.getSharpness() >= 4) {
            player.sendMessage("§cThis upgrade is already maxed!");
            XSound.matchXSound("ENTITY_VILLAGER_NO").ifPresent(s -> s.play(player));
            return;
        }
        
        int cost = getSharpnessCost(teamData.getSharpness());
        if (!hasEnough(player, XMaterial.DIAMOND, cost)) {
            player.sendMessage("§cYou don't have enough diamonds!");
            XSound.matchXSound("ENTITY_VILLAGER_NO").ifPresent(s -> s.play(player));
            return;
        }
        
        removeCurrency(player, XMaterial.DIAMOND, cost);
        teamData.setSharpness(teamData.getSharpness() + 1);
        
        XSound.matchXSound("ENTITY_EXPERIENCE_ORB_PICKUP").ifPresent(s -> s.play(player));
        player.sendMessage("§aPurchased Sharpened Swords tier " + teamData.getSharpness() + "!");
        player.closeInventory();
    }
    
    private void purchaseProtection(Player player, TeamData teamData) {
        if (teamData.getProtection() >= 4) {
            player.sendMessage("§cThis upgrade is already maxed!");
            XSound.matchXSound("ENTITY_VILLAGER_NO").ifPresent(s -> s.play(player));
            return;
        }
        
        int cost = getProtectionCost(teamData.getProtection());
        if (!hasEnough(player, XMaterial.DIAMOND, cost)) {
            player.sendMessage("§cYou don't have enough diamonds!");
            XSound.matchXSound("ENTITY_VILLAGER_NO").ifPresent(s -> s.play(player));
            return;
        }
        
        removeCurrency(player, XMaterial.DIAMOND, cost);
        teamData.setProtection(teamData.getProtection() + 1);
        
        XSound.matchXSound("ENTITY_EXPERIENCE_ORB_PICKUP").ifPresent(s -> s.play(player));
        player.sendMessage("§aPurchased Reinforced Armor tier " + teamData.getProtection() + "!");
        player.closeInventory();
    }
    
    private void purchaseHaste(Player player, TeamData teamData) {
        if (teamData.isHasteEnabled()) {
            player.sendMessage("§cYou already have this upgrade!");
            XSound.matchXSound("ENTITY_VILLAGER_NO").ifPresent(s -> s.play(player));
            return;
        }
        
        if (!hasEnough(player, XMaterial.DIAMOND, 4)) {
            player.sendMessage("§cYou don't have enough diamonds!");
            XSound.matchXSound("ENTITY_VILLAGER_NO").ifPresent(s -> s.play(player));
            return;
        }
        
        removeCurrency(player, XMaterial.DIAMOND, 4);
        teamData.setHasteEnabled(true);
        
        XSound.matchXSound("ENTITY_EXPERIENCE_ORB_PICKUP").ifPresent(s -> s.play(player));
        player.sendMessage("§aPurchased Maniac Miner!");
        player.closeInventory();
    }
    
    private void purchaseForge(Player player, TeamData teamData) {
        if (teamData.getForgeLevel() >= 4) {
            player.sendMessage("§cThis upgrade is already maxed!");
            XSound.matchXSound("ENTITY_VILLAGER_NO").ifPresent(s -> s.play(player));
            return;
        }
        
        int cost = getForgeCost(teamData.getForgeLevel());
        if (!hasEnough(player, XMaterial.DIAMOND, cost)) {
            player.sendMessage("§cYou don't have enough diamonds!");
            XSound.matchXSound("ENTITY_VILLAGER_NO").ifPresent(s -> s.play(player));
            return;
        }
        
        removeCurrency(player, XMaterial.DIAMOND, cost);
        teamData.setForgeLevel(teamData.getForgeLevel() + 1);
        
        XSound.matchXSound("ENTITY_EXPERIENCE_ORB_PICKUP").ifPresent(s -> s.play(player));
        player.sendMessage("§aPurchased Iron Forge tier " + teamData.getForgeLevel() + "!");
        player.closeInventory();
    }
    
    private void purchaseHealPool(Player player, TeamData teamData) {
        if (teamData.isHealPoolEnabled()) {
            player.sendMessage("§cYou already have this upgrade!");
            XSound.matchXSound("ENTITY_VILLAGER_NO").ifPresent(s -> s.play(player));
            return;
        }
        
        if (!hasEnough(player, XMaterial.DIAMOND, 3)) {
            player.sendMessage("§cYou don't have enough diamonds!");
            XSound.matchXSound("ENTITY_VILLAGER_NO").ifPresent(s -> s.play(player));
            return;
        }
        
        removeCurrency(player, XMaterial.DIAMOND, 3);
        teamData.setHealPoolEnabled(true);
        
        XSound.matchXSound("ENTITY_EXPERIENCE_ORB_PICKUP").ifPresent(s -> s.play(player));
        player.sendMessage("§aPurchased Heal Pool!");
        player.closeInventory();
    }
    
    private void purchaseTrap(Player player, TeamData teamData, String trapType) {
        if (teamData.getTraps().size() >= 3) {
            player.sendMessage("§cYou can only have 3 traps!");
            XSound.matchXSound("ENTITY_VILLAGER_NO").ifPresent(s -> s.play(player));
            return;
        }
        
        if (!hasEnough(player, XMaterial.DIAMOND, 1)) {
            player.sendMessage("§cYou don't have enough diamonds!");
            XSound.matchXSound("ENTITY_VILLAGER_NO").ifPresent(s -> s.play(player));
            return;
        }
        
        removeCurrency(player, XMaterial.DIAMOND, 1);
        teamData.getTraps().add(trapType);
        
        XSound.matchXSound("ENTITY_EXPERIENCE_ORB_PICKUP").ifPresent(s -> s.play(player));
        player.sendMessage("§aPurchased trap!");
        player.closeInventory();
    }
    
    private int getSharpnessCost(int level) {
        return level == 0 ? 4 : level == 1 ? 8 : level == 2 ? 12 : 16;
    }
    
    private int getProtectionCost(int level) {
        return level == 0 ? 2 : level == 1 ? 4 : level == 2 ? 8 : 16;
    }
    
    private int getForgeCost(int level) {
        return level == 0 ? 4 : level == 1 ? 8 : level == 2 ? 12 : 16;
    }
    
    private boolean hasEnough(Player player, XMaterial material, int amount) {
        int count = 0;
        Material mat = material.parseMaterial();
        if (mat == null) return false;
        
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == mat) {
                count += item.getAmount();
            }
        }
        return count >= amount;
    }
    
    private void removeCurrency(Player player, XMaterial material, int amount) {
        int remaining = amount;
        Material mat = material.parseMaterial();
        if (mat == null) return;
        
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == mat) {
                int itemAmount = item.getAmount();
                if (itemAmount <= remaining) {
                    remaining -= itemAmount;
                    player.getInventory().remove(item);
                } else {
                    item.setAmount(itemAmount - remaining);
                    remaining = 0;
                }
                
                if (remaining == 0) break;
            }
        }
    }
}