package com.Thunderlight11jk.bedwars.inventory.impl;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.Thunderlight11jk.bedwars.BedWarsPlugin;
import com.Thunderlight11jk.bedwars.inventory.InventoryButton;
import com.Thunderlight11jk.bedwars.inventory.InventoryGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ItemShopGUI extends InventoryGUI {
    
    private final BedWarsPlugin plugin;
    
    public ItemShopGUI(BedWarsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 54, plugin.getConfig().getString("shops.item-shop-title").replace("&", "§"));
    }
    
    @Override
    public void decorate(Player player) {
        addButton(10, new InventoryButton()
            .creator(p -> createItem(XMaterial.WHITE_WOOL, "§aWool", 4, "§7Cost: §f4 Iron"))
            .consumer(event -> purchaseItem(player, XMaterial.IRON_INGOT, 4, XMaterial.WHITE_WOOL, 4))
        );
        
        addButton(11, new InventoryButton()
            .creator(p -> createItem(XMaterial.TERRACOTTA, "§aHardened Clay", 16, "§7Cost: §f12 Iron"))
            .consumer(event -> purchaseItem(player, XMaterial.IRON_INGOT, 12, XMaterial.TERRACOTTA, 16))
        );
        
        addButton(12, new InventoryButton()
            .creator(p -> createItem(XMaterial.END_STONE, "§aEnd Stone", 12, "§7Cost: §f24 Iron"))
            .consumer(event -> purchaseItem(player, XMaterial.IRON_INGOT, 24, XMaterial.END_STONE, 12))
        );
        
        addButton(13, new InventoryButton()
            .creator(p -> createItem(XMaterial.LADDER, "§aLadder", 8, "§7Cost: §f4 Iron"))
            .consumer(event -> purchaseItem(player, XMaterial.IRON_INGOT, 4, XMaterial.LADDER, 8))
        );
        
        addButton(14, new InventoryButton()
            .creator(p -> createItem(XMaterial.OAK_PLANKS, "§aOak Planks", 16, "§7Cost: §64 Gold"))
            .consumer(event -> purchaseItem(player, XMaterial.GOLD_INGOT, 4, XMaterial.OAK_PLANKS, 16))
        );
        
        addButton(15, new InventoryButton()
            .creator(p -> createItem(XMaterial.OBSIDIAN, "§aObsidian", 4, "§7Cost: §b4 Emerald"))
            .consumer(event -> purchaseItem(player, XMaterial.EMERALD, 4, XMaterial.OBSIDIAN, 4))
        );
        
        addButton(19, new InventoryButton()
            .creator(p -> createItem(XMaterial.STONE_SWORD, "§aStone Sword", 1, "§7Cost: §f10 Iron"))
            .consumer(event -> purchaseItem(player, XMaterial.IRON_INGOT, 10, XMaterial.STONE_SWORD, 1))
        );
        
        addButton(20, new InventoryButton()
            .creator(p -> createItem(XMaterial.IRON_SWORD, "§aIron Sword", 1, "§7Cost: §67 Gold"))
            .consumer(event -> purchaseItem(player, XMaterial.GOLD_INGOT, 7, XMaterial.IRON_SWORD, 1))
        );
        
        addButton(21, new InventoryButton()
            .creator(p -> createItem(XMaterial.DIAMOND_SWORD, "§aDiamond Sword", 1, "§7Cost: §b4 Emerald"))
            .consumer(event -> purchaseItem(player, XMaterial.EMERALD, 4, XMaterial.DIAMOND_SWORD, 1))
        );
        
        addButton(22, new InventoryButton()
            .creator(p -> createItem(XMaterial.STICK, "§aKnockback Stick", 1, "§7Cost: §65 Gold"))
            .consumer(event -> purchaseKnockbackStick(player))
        );
        
        addButton(28, new InventoryButton()
            .creator(p -> createItem(XMaterial.CHAINMAIL_BOOTS, "§aChainmail Armor", 1, "§7Cost: §f40 Iron"))
            .consumer(event -> purchaseArmor(player, "chainmail"))
        );
        
        addButton(29, new InventoryButton()
            .creator(p -> createItem(XMaterial.IRON_BOOTS, "§aIron Armor", 1, "§7Cost: §612 Gold"))
            .consumer(event -> purchaseArmor(player, "iron"))
        );
        
        addButton(30, new InventoryButton()
            .creator(p -> createItem(XMaterial.DIAMOND_BOOTS, "§aDiamond Armor", 1, "§7Cost: §b6 Emerald"))
            .consumer(event -> purchaseArmor(player, "diamond"))
        );
        
        addButton(37, new InventoryButton()
            .creator(p -> createItem(XMaterial.BOW, "§aBow", 1, "§7Cost: §612 Gold"))
            .consumer(event -> purchaseItem(player, XMaterial.GOLD_INGOT, 12, XMaterial.BOW, 1))
        );
        
        addButton(38, new InventoryButton()
            .creator(p -> createItem(XMaterial.ARROW, "§aArrows", 8, "§7Cost: §62 Gold"))
            .consumer(event -> purchaseItem(player, XMaterial.GOLD_INGOT, 2, XMaterial.ARROW, 8))
        );
        
        addButton(39, new InventoryButton()
            .creator(p -> createItem(XMaterial.TNT, "§aTNT", 1, "§7Cost: §64 Gold"))
            .consumer(event -> purchaseItem(player, XMaterial.GOLD_INGOT, 4, XMaterial.TNT, 1))
        );
        
        addButton(40, new InventoryButton()
            .creator(p -> createItem(XMaterial.GOLDEN_APPLE, "§aGolden Apple", 1, "§7Cost: §63 Gold"))
            .consumer(event -> purchaseItem(player, XMaterial.GOLD_INGOT, 3, XMaterial.GOLDEN_APPLE, 1))
        );
        
        super.decorate(player);
    }
    
    private ItemStack createItem(XMaterial material, String name, int amount, String lore) {
        ItemStack item = material.parseItem();
        if (item == null) return new ItemStack(Material.STONE);
        
        item.setAmount(amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name.replace("&", "§"));
        meta.setLore(Arrays.asList(lore.replace("&", "§")));
        item.setItemMeta(meta);
        return item;
    }
    
    private void purchaseItem(Player player, XMaterial currency, int cost, XMaterial item, int amount) {
        if (!hasEnough(player, currency, cost)) {
            player.sendMessage("§cYou don't have enough " + currency.name().replace("_", " ").toLowerCase() + "!");
            XSound.matchXSound("ENTITY_VILLAGER_NO").ifPresent(s -> s.play(player));
            return;
        }
        
        removeCurrency(player, currency, cost);
        ItemStack reward = item.parseItem();
        if (reward != null) {
            reward.setAmount(amount);
            player.getInventory().addItem(reward);
        }
        
        XSound.matchXSound("ENTITY_EXPERIENCE_ORB_PICKUP").ifPresent(s -> s.play(player));
        player.sendMessage("§aPurchased " + amount + "x " + item.name().replace("_", " ") + "!");
    }
    
    private void purchaseKnockbackStick(Player player) {
        if (!hasEnough(player, XMaterial.GOLD_INGOT, 5)) {
            player.sendMessage("§cYou don't have enough gold!");
            XSound.matchXSound("ENTITY_VILLAGER_NO").ifPresent(s -> s.play(player));
            return;
        }
        
        removeCurrency(player, XMaterial.GOLD_INGOT, 5);
        
        ItemStack stick = XMaterial.STICK.parseItem();
        ItemMeta meta = stick.getItemMeta();
        meta.setDisplayName("§aKnockback Stick");
        stick.setItemMeta(meta);
        stick.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.KNOCKBACK, 1);
        
        player.getInventory().addItem(stick);
        XSound.matchXSound("ENTITY_EXPERIENCE_ORB_PICKUP").ifPresent(s -> s.play(player));
        player.sendMessage("§aPurchased Knockback Stick!");
    }
    
    private void purchaseArmor(Player player, String type) {
        XMaterial currency = type.equals("chainmail") ? XMaterial.IRON_INGOT : 
                            type.equals("iron") ? XMaterial.GOLD_INGOT : XMaterial.EMERALD;
        int cost = type.equals("chainmail") ? 40 : type.equals("iron") ? 12 : 6;
        
        if (!hasEnough(player, currency, cost)) {
            player.sendMessage("§cYou don't have enough " + currency.name().replace("_", " ").toLowerCase() + "!");
            XSound.matchXSound("ENTITY_VILLAGER_NO").ifPresent(s -> s.play(player));
            return;
        }
        
        removeCurrency(player, currency, cost);
        
        ItemStack[] armor = player.getInventory().getArmorContents();
        
        if (type.equals("chainmail")) {
            armor[2] = XMaterial.CHAINMAIL_CHESTPLATE.parseItem();
            armor[1] = XMaterial.CHAINMAIL_LEGGINGS.parseItem();
        } else if (type.equals("iron")) {
            armor[2] = XMaterial.IRON_CHESTPLATE.parseItem();
            armor[1] = XMaterial.IRON_LEGGINGS.parseItem();
        } else {
            armor[2] = XMaterial.DIAMOND_CHESTPLATE.parseItem();
            armor[1] = XMaterial.DIAMOND_LEGGINGS.parseItem();
        }
        
        player.getInventory().setArmorContents(armor);
        XSound.matchXSound("ENTITY_EXPERIENCE_ORB_PICKUP").ifPresent(s -> s.play(player));
        player.sendMessage("§aPurchased " + type + " armor!");
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