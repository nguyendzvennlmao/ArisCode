package me.aris.ariscode.commands;

import me.aris.ariscode.ArisCode;
import me.aris.ariscode.managers.CodeManager;
import me.aris.ariscode.models.GiftCode;
import me.aris.ariscode.models.CodeType;
import me.aris.ariscode.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.*;

public class CodeCommand implements CommandExecutor, TabCompleter {
    
    private final ArisCode plugin;
    private final CodeManager manager;
    
    public CodeCommand(ArisCode plugin) {
        this.plugin = plugin;
        this.manager = plugin.getCodeManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "create":
                return handleCreate(sender, args);
            case "edit":
                return handleEdit(sender, args);
            case "list":
                return handleList(sender);
            case "reload":
                return handleReload(sender);
            case "help":
                sendHelp(sender);
                return true;
            default:
                return handleRedeem(sender, args);
        }
    }
    
    private boolean handleCreate(CommandSender sender, String[] args) {
        if (!sender.hasPermission("ariscode.create")) {
            MessageUtils.sendMessage((Player) sender, "NoPermissions");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(MessageUtils.color("&c/ariscode create <code> [random/limit]"));
            return true;
        }
        
        String code = args[1].toUpperCase();
        CodeType type = CodeType.NORMAL;
        
        if (args.length >= 3) {
            if (args[2].equalsIgnoreCase("random")) type = CodeType.RANDOM;
            if (args[2].equalsIgnoreCase("limit")) type = CodeType.LIMIT;
        }
        
        if (manager.exists(code)) {
            MessageUtils.sendMessage((Player) sender, "CodeExist");
            return true;
        }
        
        manager.createGiftCode(code, type);
        sender.sendMessage(MessageUtils.color("&a&l✓ Da tao ma code &e&l" + code + " &a&lthanh cong!"));
        sender.sendMessage(MessageUtils.color("&7Su dung &f/ariscode edit " + code + " &7de chinh sua phan thuong"));
        return true;
    }
    
    private boolean handleEdit(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.color("&cLenh chi danh cho nguoi choi!"));
            return true;
        }
        
        if (!sender.hasPermission("ariscode.edit")) {
            MessageUtils.sendMessage((Player) sender, "NoPermissions");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(MessageUtils.color("&c/ariscode edit <code>"));
            return true;
        }
        
        String code = args[1].toUpperCase();
        if (!manager.exists(code)) {
            MessageUtils.sendMessage((Player) sender, "CodeNotExist");
            return true;
        }
        
        sender.sendMessage(MessageUtils.color("&aDang mo giao dien chinh sua cho code &e" + code));
        return true;
    }
    
    private boolean handleList(CommandSender sender) {
        if (!sender.hasPermission("ariscode.list")) {
            MessageUtils.sendMessage((Player) sender, "NoPermissions");
            return true;
        }
        
        Map<String, GiftCode> codes = manager.getAllGiftCodes();
        if (codes.isEmpty()) {
            sender.sendMessage(MessageUtils.color("&cChua co ma code nao!"));
            return true;
        }
        
        sender.sendMessage(MessageUtils.color("&6&m----------------------------------------"));
        sender.sendMessage(MessageUtils.color("&e&lDanh sach ma code:"));
        
        for (GiftCode gc : codes.values()) {
            String type = "";
            switch (gc.getType()) {
                case NORMAL: type = "&aNormal"; break;
                case RANDOM: type = "&6Random"; break;
                case LIMIT: type = "&cLimit"; break;
            }
            sender.sendMessage(MessageUtils.color("&7- &f" + gc.getCode() + " &7[" + type + "&7] &7- &b" + gc.getCurrentUse() + "&7/" + (gc.getType() == CodeType.LIMIT ? gc.getMaxUse() : gc.getLimit()) + " luot"));
        }
        
        sender.sendMessage(MessageUtils.color("&6&m----------------------------------------"));
        sender.sendMessage(MessageUtils.color("&7Tong so: &e" + codes.size() + " &7ma code"));
        return true;
    }
    
    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("ariscode.reload")) {
            MessageUtils.sendMessage((Player) sender, "NoPermissions");
            return true;
        }
        
        long start = System.currentTimeMillis();
        plugin.getConfigManager().reload();
        manager.saveAll();
        long end = System.currentTimeMillis();
        
        sender.sendMessage(MessageUtils.color("&a&l✓ Da reload cau hinh thanh cong! &7(" + (end - start) + "ms)"));
        return true;
    }
    
    private boolean handleRedeem(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length < 2) return true;
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(MessageUtils.color("&cNguoi choi khong online!"));
                return true;
            }
            return redeemCode(target, args[0], sender);
        }
        
        Player player = (Player) sender;
        String code = args[0].toUpperCase();
        
        if (args.length >= 2 && player.hasPermission("ariscode.give")) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                MessageUtils.sendMessage(player, "NotOnline");
                return true;
            }
            return redeemCode(target, code, player);
        }
        
        return redeemCode(player, code, null);
    }
    
    private boolean redeemCode(Player player, String code, CommandSender giver) {
        if (!manager.exists(code)) {
            MessageUtils.sendMessage(player, "CodeNotFound");
            return true;
        }
        
        GiftCode giftCode = manager.getGiftCode(code);
        
        if (manager.isExpired(giftCode)) {
            MessageUtils.sendMessage(player, "CodeNotFound");
            return true;
        }
        
        int onlineHours = manager.getPlayerOnlineHours(player);
        if (onlineHours < giftCode.getRequireOnline()) {
            MessageUtils.sendMessage(player, "RequireOnline", "<require>", String.valueOf(giftCode.getRequireOnline()));
            return true;
        }
        
        String playerIp = player.getAddress().getAddress().getHostAddress();
        
        if (giftCode.getType() == CodeType.NORMAL) {
            if (manager.hasUsed(giftCode, player.getName())) {
                MessageUtils.sendMessage(player, "AlreadyUseCode");
                return true;
            }
            
            if (plugin.getConfigManager().isIpCheck() && manager.hasUsedIp(giftCode, playerIp)) {
                MessageUtils.sendMessage(player, "CodeIpUsed");
                return true;
            }
            
            giveRewards(player, giftCode);
            manager.addUsedPlayer(giftCode, player.getName(), playerIp);
            MessageUtils.sendMessage(player, "CodeReceived");
            
        } else if (giftCode.getType() == CodeType.LIMIT) {
            if (manager.hasUsed(giftCode, player.getName())) {
                MessageUtils.sendMessage(player, "AlreadyUseCode");
                return true;
            }
            
            if (giftCode.getCurrentUse() >= giftCode.getMaxUse() && giftCode.getMaxUse() > 0) {
                MessageUtils.sendMessage(player, "CodeMaxUse");
                return true;
            }
            
            if (plugin.getConfigManager().isIpCheck() && manager.hasUsedIp(giftCode, playerIp)) {
                MessageUtils.sendMessage(player, "CodeIpUsed");
                return true;
            }
            
            giveRewards(player, giftCode);
            manager.addUsedPlayer(giftCode, player.getName(), playerIp);
            MessageUtils.sendMessage(player, "CodeReceived");
            
        } else if (giftCode.getType() == CodeType.RANDOM) {
            if (giftCode.getRandomCodes().contains(code)) {
                if (manager.hasUsed(giftCode, player.getName())) {
                    MessageUtils.sendMessage(player, "AlreadyUseCodeRandom");
                    return true;
                }
                
                giveRewards(player, giftCode);
                manager.addUsedPlayer(giftCode, player.getName(), playerIp);
                MessageUtils.sendMessage(player, "CodeReceived");
            } else {
                MessageUtils.sendMessage(player, "CodeNotFound");
                return true;
            }
        }
        
        String broadcastMsg = plugin.getConfig().getString("Message.PlayerCodeReceived");
        if (broadcastMsg != null && !broadcastMsg.isEmpty()) {
            Bukkit.broadcastMessage(MessageUtils.color(broadcastMsg.replace("<player>", player.getName())));
        }
        
        if (giver != null && giver != player) {
            giver.sendMessage(MessageUtils.color("&aDa tang code &e" + code + " &acho &e" + player.getName()));
        }
        
        return true;
    }
    
    private void giveRewards(Player player, GiftCode giftCode) {
        if (giftCode.getMoney() > 0) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + player.getName() + " " + giftCode.getMoney());
        }
        
        if (giftCode.getPoints() > 0) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "points give " + player.getName() + " " + giftCode.getPoints());
        }
        
        if (giftCode.getExp() > 0) {
            player.giveExp(giftCode.getExp());
        }
        
        for (ItemStack item : giftCode.getItems()) {
            if (item != null && item.getType() != Material.AIR) {
                player.getInventory().addItem(item.clone());
            }
        }
        
        for (String command : giftCode.getCommands()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("<player>", player.getName()));
        }
    }
    
    private void sendHelp(CommandSender sender) {
        for (String line : plugin.getConfig().getStringList("Message.Help")) {
            sender.sendMessage(MessageUtils.color(line));
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            completions.add("create");
            completions.add("edit");
            completions.add("list");
            completions.add("reload");
            completions.add("help");
            completions.addAll(manager.getAllGiftCodes().keySet());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("create")) {
            completions.add("<CODE>");
        } else if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
            completions.add("random");
            completions.add("limit");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("edit")) {
            completions.addAll(manager.getAllGiftCodes().keySet());
        }
        
        return completions;
    }
    }
