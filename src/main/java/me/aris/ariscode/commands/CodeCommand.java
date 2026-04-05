package me.aris.ariscode.commands;

import me.aris.ariscode.ArisCode;
import me.aris.ariscode.managers.CodeManager;
import me.aris.ariscode.models.GiftCode;
import me.aris.ariscode.models.CodeType;
import me.aris.ariscode.gui.EditGUI;
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
    private EditGUI editGUI;
    
    public CodeCommand(ArisCode plugin) {
        this.plugin = plugin;
        this.manager = plugin.getCodeManager();
        this.editGUI = new EditGUI(plugin);
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
        
        if (type == CodeType.RANDOM) {
            List<String> randomCodes = manager.generateRandomCodes(
                plugin.getConfigManager().getCodeRandom(),
                plugin.getConfigManager().getGiftcodeFormat()
            );
            GiftCode gc = new GiftCode(code, type);
            gc.setRandomCodes(randomCodes);
            manager.createGiftCode(code, type);
            manager.saveGiftCode(gc);
        } else {
            manager.createGiftCode(code, type);
        }
        
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
        
        editGUI.open((Player) sender, code);
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
        
        for (String line : plugin.getConfig().getStringList("Message.ListHeader")) {
            sender.sendMessage(MessageUtils.color(line));
        }
        
        for (GiftCode gc : codes.values()) {
            String type = "";
            switch (gc.getType()) {
                case NORMAL: type = plugin.getConfig().getString("Message.TypeNormal", "&aNormal"); break;
                case RANDOM: type = plugin.getConfig().getString("Message.TypeRandom", "&6Random"); break;
                case LIMIT: type = plugin.getConfig().getString("Message.TypeLimit", "&cLimit"); break;
            }
            String line = plugin.getConfig().getString("Message.ListFormat", "&7- &f<code> &7[<type>&7] &7- &b<used>&7/<max> luot");
            line = line.replace("<code>", gc.getCode());
            line = line.replace("<type>", type);
            line = line.replace("<used>", String.valueOf(gc.getCurrentUse()));
            line = line.replace("<max>", String.valueOf(gc.getType() == CodeType.LIMIT ? gc.getMaxUse() : gc.getLimit()));
            sender.sendMessage(MessageUtils.color(line));
        }
        
        for (String line : plugin.getConfig().getStringList("Message.ListFooter")) {
            sender.sendMessage(MessageUtils.color(line));
        }
        
        String totalLine = plugin.getConfig().getString("Message.ListTotal", "&7Tong so: &e<total> &7ma code");
        sender.sendMessage(MessageUtils.color(totalLine.replace("<total>", String.valueOf(codes.size()))));
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
        GiftCode giftCode = null;
        String mainCode = null;
        
        for (GiftCode gc : manager.getAllGiftCodes().values()) {
            if (gc.getType() == CodeType.RANDOM && gc.getRandomCodes().contains(code)) {
                giftCode = gc;
                mainCode = gc.getCode();
                break;
            } else if (gc.getCode().equals(code)) {
                giftCode = gc;
                mainCode = code;
                break;
            }
        }
        
        if (giftCode == null) {
            MessageUtils.sendMessage(player, "CodeNotFound");
            return true;
        }
        
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
            if (manager.hasUsed(giftCode, player.getName())) {
                int maxUse = plugin.getConfigManager().getRandomMaxUse();
                if (maxUse > 0 && manager.countElement(giftCode.getUsedPlayers(), player.getName()) >= maxUse) {
                    MessageUtils.sendMessage(player, "AlreadyUseCodeRandom");
                    return true;
                }
            }
            
            if (plugin.getConfigManager().isIpCheck() && manager.hasUsedIp(giftCode, playerIp)) {
                MessageUtils.sendMessage(player, "CodeIpUsed");
                return true;
            }
            
            giveRewards(player, giftCode);
            manager.addUsedPlayer(giftCode, player.getName(), playerIp);
            
            List<String> randomCodes = giftCode.getRandomCodes();
            randomCodes.remove(code);
            giftCode.setRandomCodes(randomCodes);
            manager.saveGiftCode(giftCode);
            MessageUtils.sendMessage(player, "CodeReceived");
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
