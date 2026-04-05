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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.*;

public class EditGUI implements Listener {
    
    private final ArisCode plugin;
    private final CodeManager manager;
    public Map<String, String> editing;
    public Map<String, Inventory> tempInventory;
    public Map<String, String> waitingInput;
    public int[] itemSlots;
    
    public EditGUI(ArisCode plugin) {
        this.plugin = plugin;
        this.manager = plugin.getCodeManager();
        this.editing = new HashMap<>();
        this.tempInventory = new HashMap<>();
        this.waitingInput = new HashMap<>();
        this.itemSlots = new int[]{10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34,37,38,39,40,41,42,43};
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    public void open(Player player, String code) {
        GiftCode giftCode = manager.getGiftCode(code);
        if (giftCode == null) {
            MessageUtils.sendMessage(player, "CodeNotExist");
            return;
        }
        
        String title = MessageUtils.color(plugin.getConfig().getString("Settings.Gui.Title", "&8ArisCode &7| &fChinh sua code &e" + code));
        Inventory inv = Bukkit.createInventory(null, 54, title);
        
        ItemStack border = ItemBuilder.create(Material.GRAY_STAINED_GLASS_PANE, " ");
        ItemStack cancel = ItemBuilder.create(Material.BARRIER, "&c&lXOA CODE", Arrays.asList("&7Xoa ma code nay", "&cKhong the khoi phuc!"));
        ItemStack save = ItemBuilder.create(Material.EMERALD_BLOCK, "&a&lLUU & THOAT", Arrays.asList("&7Luu tat ca thay doi", "&7va dong cua so"));
        
        for (int i = 0; i <= 9; i++) inv.setItem(i, border);
        inv.setItem(17, border);
        inv.setItem(18, border);
        inv.setItem(26, border);
        inv.setItem(27, border);
        inv.setItem(35, border);
        inv.setItem(36, border);
        inv.setItem(44, border);
        inv.setItem(46, border);
        inv.setItem(48, border);
        inv.setItem(50, border);
        inv.setItem(52, border);
        
        String moneyName = plugin.getConfig().getString("Settings.Gui.Item.Money.Name", "&6&lMoney &7&l> &e&l<money> &6⛁");
        String pointsName = plugin.getConfig().getString("Settings.Gui.Item.Points.Name", "&a&lPoints &7&l> &e&l<points> &a⛁");
        String expName = plugin.getConfig().getString("Settings.Gui.Item.Exp.Name", "&b&lExp &7&l> &e&l<exp> &b⚡");
        String limitName = plugin.getConfig().getString("Settings.Gui.Item.Limit.Name", "&c&lLimit &7&l> &e&l<limit> &c✖");
        String onlineName = plugin.getConfig().getString("Settings.Gui.Item.RequireOnline.Name", "&3&lOnline &7&l> &e&l<online> &3⏣");
        
        List<String> moneyLore = Arrays.asList("&7So tien Vault", "&eClick de chinh sua", "&7&m--------------------------------", "&e&lCurrent: &f" + (int)giftCode.getMoney());
        List<String> pointsLore = Arrays.asList("&7So diem PlayerPoints", "&eClick de chinh sua", "&7&m--------------------------------", "&e&lCurrent: &f" + giftCode.getPoints());
        List<String> expLore = Arrays.asList("&7Diem kinh nghiem", "&eClick de chinh sua", "&7&m--------------------------------", "&e&lCurrent: &f" + giftCode.getExp());
        List<String> onlineLore = Arrays.asList("&7So gio online yeu cau", "&eClick de chinh sua", "&7&m--------------------------------", "&e&lCurrent: &f" + giftCode.getRequireOnline() + "h");
        
        ItemStack money = ItemBuilder.create(Material.GOLD_INGOT, moneyName.replace("<money>", String.valueOf((int)giftCode.getMoney())), moneyLore);
        ItemStack points = ItemBuilder.create(Material.EMERALD, pointsName.replace("<points>", String.valueOf(giftCode.getPoints())), pointsLore);
        ItemStack exp = ItemBuilder.create(Material.EXPERIENCE_BOTTLE, expName.replace("<exp>", String.valueOf(giftCode.getExp())), expLore);
        ItemStack requireOnline = ItemBuilder.create(Material.CLOCK, onlineName.replace("<online>", String.valueOf(giftCode.getRequireOnline())), onlineLore);
        
        inv.setItem(47, money);
        inv.setItem(49, points);
        inv.setItem(51, exp);
        inv.setItem(46, requireOnline);
        inv.setItem(45, cancel);
        inv.setItem(53, save);
        
        if (giftCode.getType() == CodeType.NORMAL) {
            List<String> limitLore = Arrays.asList("&7Gioi han luot dung", "&eClick de chinh sua", "&7&m--------------------------------", "&e&lCurrent: &f" + giftCode.getLimit());
            ItemStack limit = ItemBuilder.create(Material.PAPER, limitName.replace("<limit>", String.valueOf(giftCode.getLimit())), limitLore);
            inv.setItem(52, limit);
        } else if (giftCode.getType() == CodeType.LIMIT) {
            List<String> limitLore = Arrays.asList("&7Gioi han tong luot dung", "&eClick de chinh sua", "&7&m--------------------------------", "&e&lCurrent: &f" + giftCode.getMaxUse());
            ItemStack limit = ItemBuilder.create(Material.NAME_TAG, limitName.replace("<limit>", String.valueOf(giftCode.getMaxUse())), limitLore);
            inv.setItem(52, limit);
        } else if (giftCode.getType() == CodeType.RANDOM) {
            List<String> randomLore = Arrays.asList("&7So luong code con", "&7Tu dong sinh khi tao", "&7&m--------------------------------", "&e&lCurrent: &f" + giftCode.getRandomCodes().size());
            ItemStack random = ItemBuilder.create(Material.CHEST, "&6&lRandom &7&l> &e&l" + giftCode.getRandomCodes().size() + " &6code", randomLore);
            inv.setItem(52, random);
        }
        
        for (ItemStack item : giftCode.getItems()) {
            if (item != null && item.getType() != Material.AIR) {
                ItemStack clone = item.clone();
                ItemMeta meta = clone.getItemMeta();
                if (meta != null) {
                    List<String> lore = meta.getLore();
                    if (lore == null) lore = new ArrayList<>();
                    lore.add(0, "&7&m--------------------------------");
                    lore.add(0, "&a&lVAT PHAM THUONG");
                    lore.add(0, "&7&m--------------------------------");
                    meta.setLore(lore);
                    clone.setItemMeta(meta);
                }
                inv.addItem(clone);
            }
        }
        
        editing.put(player.getName(), code);
        tempInventory.put(player.getName(), inv);
        player.openInventory(inv);
    }
    
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        
        if (!waitingInput.containsKey(player.getName())) return;
        
        event.setCancelled(true);
        
        String[] data = waitingInput.get(player.getName()).split(":");
        String code = data[0];
        String type = data[1];
        String message = event.getMessage().trim();
        
        if (message.equalsIgnoreCase("huy")) {
            waitingInput.remove(player.getName());
            MessageUtils.sendMessage(player, "FormatError");
            return;
        }
        
        GiftCode giftCode = manager.getGiftCode(code);
        if (giftCode == null) {
            waitingInput.remove(player.getName());
            return;
        }
        
        try {
            switch (type) {
                case "money":
                    double money = Double.parseDouble(message);
                    giftCode.setMoney(money);
                    break;
                case "points":
                    int points = Integer.parseInt(message);
                    giftCode.setPoints(points);
                    break;
                case "exp":
                    int exp = Integer.parseInt(message);
                    giftCode.setExp(exp);
                    break;
                case "limit":
                    int limit = Integer.parseInt(message);
                    if (giftCode.getType() == CodeType.NORMAL) {
                        giftCode.setLimit(limit);
                    } else if (giftCode.getType() == CodeType.LIMIT) {
                        giftCode.setMaxUse(limit);
                    }
                    break;
                case "online":
                    int online = Integer.parseInt(message);
                    giftCode.setRequireOnline(online);
                    break;
            }
            manager.saveGiftCode(giftCode);
            MessageUtils.sendMessage(player, "SaveCode");
        } catch (NumberFormatException e) {
            MessageUtils.sendMessage(player, "FormatError");
            waitingInput.remove(player.getName());
            return;
        }
        
        waitingInput.remove(player.getName());
        
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (editing.containsKey(player.getName()) && editing.get(player.getName()).equals(code)) {
                Inventory inv = tempInventory.get(player.getName());
                if (inv != null) {
                    updateInventory(player, code, inv);
                } else {
                    open(player, code);
                }
            }
        });
    }
    
    private void updateInventory(Player player, String code, Inventory oldInv) {
        GiftCode giftCode = manager.getGiftCode(code);
        if (giftCode == null) return;
        
        Inventory newInv = Bukkit.createInventory(null, 54, oldInv.getTitle());
        
        for (int i = 0; i < oldInv.getSize(); i++) {
            ItemStack item = oldInv.getItem(i);
            if (item != null) {
                newInv.setItem(i, item);
            }
        }
        
        String moneyName = plugin.getConfig().getString("Settings.Gui.Item.Money.Name", "&6&lMoney &7&l> &e&l<money> &6⛁");
        String pointsName = plugin.getConfig().getString("Settings.Gui.Item.Points.Name", "&a&lPoints &7&l> &e&l<points> &a⛁");
        String expName = plugin.getConfig().getString("Settings.Gui.Item.Exp.Name", "&b&lExp &7&l> &e&l<exp> &b⚡");
        String limitName = plugin.getConfig().getString("Settings.Gui.Item.Limit.Name", "&c&lLimit &7&l> &e&l<limit> &c✖");
        String onlineName = plugin.getConfig().getString("Settings.Gui.Item.RequireOnline.Name", "&3&lOnline &7&l> &e&l<online> &3⏣");
        
        List<String> moneyLore = Arrays.asList("&7So tien Vault", "&eClick de chinh sua", "&7&m--------------------------------", "&e&lCurrent: &f" + (int)giftCode.getMoney());
        List<String> pointsLore = Arrays.asList("&7So diem PlayerPoints", "&eClick de chinh sua", "&7&m--------------------------------", "&e&lCurrent: &f" + giftCode.getPoints());
        List<String> expLore = Arrays.asList("&7Diem kinh nghiem", "&eClick de chinh sua", "&7&m--------------------------------", "&e&lCurrent: &f" + giftCode.getExp());
        List<String> onlineLore = Arrays.asList("&7So gio online yeu cau", "&eClick de chinh sua", "&7&m--------------------------------", "&e&lCurrent: &f" + giftCode.getRequireOnline() + "h");
        
        ItemStack money = ItemBuilder.create(Material.GOLD_INGOT, moneyName.replace("<money>", String.valueOf((int)giftCode.getMoney())), moneyLore);
        ItemStack points = ItemBuilder.create(Material.EMERALD, pointsName.replace("<points>", String.valueOf(giftCode.getPoints())), pointsLore);
        ItemStack exp = ItemBuilder.create(Material.EXPERIENCE_BOTTLE, expName.replace("<exp>", String.valueOf(giftCode.getExp())), expLore);
        ItemStack requireOnline = ItemBuilder.create(Material.CLOCK, onlineName.replace("<online>", String.valueOf(giftCode.getRequireOnline())), onlineLore);
        
        newInv.setItem(47, money);
        newInv.setItem(49, points);
        newInv.setItem(51, exp);
        newInv.setItem(46, requireOnline);
        
        if (giftCode.getType() == CodeType.NORMAL) {
            List<String> limitLore = Arrays.asList("&7Gioi han luot dung", "&eClick de chinh sua", "&7&m--------------------------------", "&e&lCurrent: &f" + giftCode.getLimit());
            ItemStack limit = ItemBuilder.create(Material.PAPER, limitName.replace("<limit>", String.valueOf(giftCode.getLimit())), limitLore);
            newInv.setItem(52, limit);
        } else if (giftCode.getType() == CodeType.LIMIT) {
            List<String> limitLore = Arrays.asList("&7Gioi han tong luot dung", "&eClick de chinh sua", "&7&m--------------------------------", "&e&lCurrent: &f" + giftCode.getMaxUse());
            ItemStack limit = ItemBuilder.create(Material.NAME_TAG, limitName.replace("<limit>", String.valueOf(giftCode.getMaxUse())), limitLore);
            newInv.setItem(52, limit);
        }
        
        tempInventory.put(player.getName(), newInv);
        player.openInventory(newInv);
    }
    
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        
        if (!editing.containsKey(player.getName())) return;
        String code = editing.get(player.getName());
        
        String title = MessageUtils.color(plugin.getConfig().getString("Settings.Gui.Title", "&8ArisCode &7| &fChinh sua code &e" + code));
        if (!event.getView().getTitle().equals(title)) return;
        
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
            tempInventory.remove(player.getName());
            player.closeInventory();
            MessageUtils.sendMessage(player, "DeleteCode");
            return;
        }
        
        if (event.getSlot() == 46) {
            waitingInput.put(player.getName(), code + ":online");
            player.closeInventory();
            MessageUtils.sendMessage(player, "TypingValue");
            return;
        }
        
        if (event.getSlot() == 47) {
            waitingInput.put(player.getName(), code + ":money");
            player.closeInventory();
            MessageUtils.sendMessage(player, "TypingValue");
            return;
        }
        
        if (event.getSlot() == 49) {
            waitingInput.put(player.getName(), code + ":points");
            player.closeInventory();
            MessageUtils.sendMessage(player, "TypingValue");
            return;
        }
        
        if (event.getSlot() == 51) {
            waitingInput.put(player.getName(), code + ":exp");
            player.closeInventory();
            MessageUtils.sendMessage(player, "TypingValue");
            return;
        }
        
        if (event.getSlot() == 52) {
            if (giftCode.getType() == CodeType.NORMAL || giftCode.getType() == CodeType.LIMIT) {
                waitingInput.put(player.getName(), code + ":limit");
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
            tempInventory.remove(player.getName());
            player.closeInventory();
            MessageUtils.sendMessage(player, "SaveCode");
            return;
        }
    }
    
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        
        if (!editing.containsKey(player.getName())) return;
        String code = editing.get(player.getName());
        
        String title = MessageUtils.color(plugin.getConfig().getString("Settings.Gui.Title", "&8ArisCode &7| &fChinh sua code &e" + code));
        if (!event.getView().getTitle().equals(title)) return;
        
        if (!waitingInput.containsKey(player.getName())) {
            Inventory inv = event.getInventory();
            tempInventory.put(player.getName(), inv);
        }
    }
            }
