package vennlmao.ariscode;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {
    public static ItemStack create(Material mat, int amount, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            List<String> lores = new ArrayList<>();
            for (String l : lore) lores.add(ChatColor.translateAlternateColorCodes('&', l));
            meta.setLore(lores);
            item.setItemMeta(meta);
        }
        return item;
    }
}
