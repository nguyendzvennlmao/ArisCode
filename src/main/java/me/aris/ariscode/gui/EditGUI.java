package me.aris.ariscode.gui;

import me.aris.ariscode.ArisCode;
import me.aris.ariscode.managers.CodeManager;
import me.aris.ariscode.models.GiftCode;
import me.aris.ariscode.models.CodeType;
import me.aris.ariscode.utils.ItemBuilder;
import me.aris.ariscode.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.*;

public class EditGUI implements Listener {
    
    private final ArisCode plugin;
    private final CodeManager manager;
    public Map<String, String> editing;
    public int[] itemSlots;
    
    public EditGUI(ArisCode plugin) {
        this.plugin = plugin;
        this.manager = plugin.getCodeManager();
        this.editing = new HashMap<>();
        this.itemSlots = new int[]{10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34,37,38,39,40,41,42,43};
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    public void open(Player player, String code) {
        GiftCode giftCode = manager.getGiftCode(code);
        if (giftCode == null) {
            MessageUtils.sendMessage(player, "CodeNotExist");
            return;
        }
        
        String title = MessageUtils.color(plugin.getConfig().getString("Settings.Gui.Title", "&8ArisCode | Chinh sua code"));
        Inventory inv = Bukkit.createInventory(null, 54, title);
        
        ItemStack blank = ItemBuilder.create(Material.BLACK_STAINED_GLASS_PANE, " ");
        ItemStack cancel = ItemBuilder.create(Material.RED_STAINED_GLASS_PANE, "&c&lXoa code");
        ItemStack save = ItemBuilder.create(Material.LIME_STAINED_GLASS_PANE, "&a&lLuu & thoat");
        
        for (int i = 0; i <= 9; i++) inv.setItem(i, blank);
        inv.setItem(17, blank);
        inv.setItem(18, blank);
        inv.setItem(26, blank);
        inv.setItem(27, blank);
        inv.setItem(35, blank);
        inv.setItem(36, blank);
        inv.setItem(44, blank);
        inv.setItem(46, blank);
        inv.setItem(48, blank);
        inv.setItem(50, blank);
        inv.setItem(52, blank);
        
        String moneyName = plugin.getConfig().getString("Settings.Gui.Item.Money.Name", "&f&lMoney: &a&l<money>");
        String pointsName = plugin.getConfig().getString("Settings.Gui.Item.Points.Name", "&f&lPoints: &6&l<points>");
        String expName = plugin.getConfig().getString("Settings.Gui.Item.Exp.Name", "&f&lExp: &d&l<exp>");
        String limitName = plugin.getConfig().getString("Settings.Gui.Item.Limit.Name", "&f&lLimit: &c&l<limit>");
        String onlineName = plugin.getConfig().getString("Settings.Gui.Item.RequireOnline.Name", "&f&lRequire Online: &b&l<online>h");
        
        List<String> moneyLore = plugin.getConfig().getStringList("Settings.Gui.Item.Money.Lore");
        List<String> pointsLore = plugin.getConfig().getStringList("Settings.Gui.Item.Points.Lore");
        List<String> expLore = plugin.getConfig().getStringList("Settings.Gui.Item.Exp.Lore");
        List<String> limitLore = plugin.getConfig().getStringList("Settings.Gui.Item.Limit.Lore");
        List<String> onlineLore = plugin.getConfig().getStringList("Settings.Gui.Item.RequireOnline.Lore");
        
        Material moneyMat = Material.getMaterial(plugin.getConfig().getString("Settings.Gui.Item.Money.ID", "GOLD_INGOT"));
        Material pointsMat = Material.getMaterial(plugin.getConfig().getString("Settings.Gui.Item.Points.ID", "EMERALD"));
        Material expMat = Material.getMaterial(plugin.getConfig().getString("Settings.Gui.Item.Exp.ID", "EXPERIENCE_BOTTLE"));
        Material limitMat = Material.getMaterial(plugin.getConfig().getString("Settings.Gui.Item.Limit.ID", "PAPER"));
        Material onlineMat = Material.getMaterial(plugin.getConfig().getString("Settings.Gui.Item.RequireOnline.ID", "CLOCK"));
        
        if (moneyMat == null) moneyMat = Material.GOLD_INGOT;
        if (pointsMat == null) pointsMat = Material.EMERALD;
        if (expMat == null) expMat = Material.EXPERIENCE_BOTTLE;
        if (limitMat == null) limitMat = Material.PAPER;
        if (onlineMat == null) onlineMat = Material.CLOCK;
        
        ItemStack money = ItemBuilder.create(moneyMat, moneyName.replace("<money>", String.valueOf((int)giftCode.getMoney())), moneyLore);
        ItemStack points = ItemBuilder.create(pointsMat, pointsName.replace("<points>", String.valueOf(giftCode.getPoints())), pointsLore);
        ItemStack exp = ItemBuilder.create(expMat, expName.replace("<exp>", String.valueOf(giftCode.getExp())), expLore);
        ItemStack requireOnline = ItemBuilder.create(onlineMat, onlineName.replace("<online>", String.valueOf(giftCode.getRequireOnline())), onlineLore);
        
        inv.setItem(47, money);
        inv.setItem(49, points);
        inv.setItem(51, exp);
        inv.setItem(46, requireOnline);
        inv.setItem(45, cancel);
        inv.setItem(53, save);
        
        if (giftCode.getType() == CodeType.NORMAL || giftCode.getType() == CodeType.LIMIT) {
            ItemStack limit = ItemBuilder.create(limitMat, limitName.replace("<limit>", String.valueOf(giftCode.getLimit())), limitLore);
            inv.setItem(52, limit);
        } else if (giftCode.getType() == CodeType.RANDOM) {
            String randomName = plugin.getConfig().getString("Settings.Gui.Item.Random.Name", "&f&lRandom: &6&l<random>");
            List<String> randomLore = plugin.getConfig().getStringList("Settings.Gui.Item.Random.Lore");
            ItemStack random = ItemBuilder.create(limitMat, randomName.replace("<random>", String.valueOf(giftCode.getRandomCodes().size())), randomLore);
            inv.setItem(52, random);
        }
        
        for (ItemStack item : giftCode.getItems()) {
            if (item != null && item.getType() != Material.AIR) {
                inv.addItem(item.clone());
            }
        }
        
        editing.put(player.getName(), code);
        player.openInventory(inv);
    }
    
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        String title = MessageUtils.color(plugin.getConfig().getString("Settings.Gui.Title", "&8ArisCode | Chinh sua code"));
        
        if (!event.getView().getTitle().equals(title)) return;
        if (!editing.containsKey(player.getName())) return;
        
        String data = editing.get(player.getName());
        String code = data.contains(":") ? data.split(":")[0] : data;
        GiftCode giftCode = manager.getGiftCode(code);
        if (giftCode == null) return;
        
        if (event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }
        
        event.setCancelled(true);
        
        int[] lockSlots = {0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,44,48,50};
        for (int slot : lockSlots) {
            if (event.getSlot() == slot) return;
        }
        
        if (event.getSlot() == 45) {
            manager.deleteGiftCode(code);
            editing.remove(player.getName());
            player.closeInventory();
            MessageUtils.sendMessage(player, "DeleteCode");
            return;
        }
        
        if (event.getSlot() == 46) {
            editing.put(player.getName(), code + ":online");
            player.closeInventory();
            MessageUtils.sendMessage(player, "TypingValue");
            return;
        }
        
        if (event.getSlot() == 47) {
            editing.put(player.getName(), code + ":money");
            player.closeInventory();
            MessageUtils.sendMessage(player, "TypingValue");
            return;
        }
        
        if (event.getSlot() == 49) {
            editing.put(player.getName(), code + ":points");
            player.closeInventory();
            MessageUtils.sendMessage(player, "TypingValue");
            return;
        }
        
        if (event.getSlot() == 51) {
            editing.put(player.getName(), code + ":exp");
            player.closeInventory();
            MessageUtils.sendMessage(player, "TypingValue");
            return;
        }
        
        if (event.getSlot() == 52) {
            if (giftCode.getType() == CodeType.NORMAL || giftCode.getType() == CodeType.LIMIT) {
                editing.put(player.getName(), code + ":limit");
                player.closeInventory();
                MessageUtils.sendMessage(player, "TypingValue");
            }
            return;
        }
        
        if (event.getSlot() == 53) {
            List<ItemStack> items = new ArrayList<>();
            for (int slot : itemSlots) {
                ItemStack item = event.getInventory().getItem(slot);
                if (item != null && item.getType() != Material.AIR) {
                    items.add(item.clone());
                }
            }
            giftCode.setItems(items);
            manager.saveGiftCode(giftCode);
            editing.remove(player.getName());
            player.closeInventory();
            MessageUtils.sendMessage(player, "SaveCode");
            return;
        }
    }
    
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        String title = MessageUtils.color(plugin.getConfig().getString("Settings.Gui.Title", "&8ArisCode | Chinh sua code"));
        
        if (!event.getView().getTitle().equals(title)) return;
        if (!editing.containsKey(player.getName())) return;
        
        String data = editing.get(player.getName());
        if (data != null && !data.contains(":")) {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                open(player, data);
            });
        }
    }
            }
