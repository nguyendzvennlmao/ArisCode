package me.aris.ariscode.gui;

import me.aris.ariscode.ArisCode;
import me.aris.ariscode.managers.CodeManager;
import me.aris.ariscode.models.GiftCode;
import me.aris.ariscode.utils.ItemBuilder;
import me.aris.ariscode.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class EditGUI implements Listener {
    
    private final ArisCode plugin;
    private final CodeManager manager;
    
    public EditGUI(ArisCode plugin) {
        this.plugin = plugin;
        this.manager = plugin.getCodeManager();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    public void open(Player player, String code) {
        GiftCode giftCode = manager.getGiftCode(code);
        if (giftCode == null) return;
        
        Inventory inv = Bukkit.createInventory(null, 54, MessageUtils.color("&8Chinh sua code &e" + code));
        
        ItemBuilder.create(Material.BLACK_STAINED_GLASS_PANE, 1, 15, " ");
        
        player.openInventory(inv);
    }
    
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().contains("Chinh sua code")) return;
        event.setCancelled(true);
    }
    }
