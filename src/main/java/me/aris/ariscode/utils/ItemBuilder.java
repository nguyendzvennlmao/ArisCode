package me.aris.ariscode.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {
    
    public static ItemStack create(Material material) {
        return new ItemStack(material);
    }
    
    public static ItemStack create(Material material, int amount) {
        return new ItemStack(material, amount);
    }
    
    public static ItemStack create(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageUtils.color(name));
        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemStack create(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageUtils.color(name));
        
        List<String> coloredLore = new ArrayList<>();
        for (String line : lore) {
            coloredLore.add(MessageUtils.color(line));
        }
        meta.setLore(coloredLore);
        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemStack create(Material material, int amount, int data, String name) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageUtils.color(name));
        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemStack create(Material material, int amount, int data, String name, List<String> lore) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageUtils.color(name));
        
        List<String> coloredLore = new ArrayList<>();
        for (String line : lore) {
            coloredLore.add(MessageUtils.color(line));
        }
        meta.setLore(coloredLore);
        item.setItemMeta(meta);
        return item;
    }
}
