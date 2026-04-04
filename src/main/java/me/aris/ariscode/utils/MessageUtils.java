package me.aris.ariscode.utils;

import me.aris.ariscode.ArisCode;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {
    
    private static ArisCode plugin = ArisCode.getInstance();
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    
    public static String color(String message) {
        if (message == null) return "";
        
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();
        
        while (matcher.find()) {
            String hexCode = matcher.group(1);
            matcher.appendReplacement(buffer, ChatColor.COLOR_CHAR + "x" + 
                ChatColor.COLOR_CHAR + hexCode.charAt(0) + 
                ChatColor.COLOR_CHAR + hexCode.charAt(1) + 
                ChatColor.COLOR_CHAR + hexCode.charAt(2) + 
                ChatColor.COLOR_CHAR + hexCode.charAt(3) + 
                ChatColor.COLOR_CHAR + hexCode.charAt(4) + 
                ChatColor.COLOR_CHAR + hexCode.charAt(5));
        }
        matcher.appendTail(buffer);
        
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }
    
    public static void sendMessage(Player player, String path) {
        if (player == null) return;
        
        boolean chatEnabled = plugin.getConfig().getBoolean("Settings.Message.chat", true);
        boolean actionBarEnabled = plugin.getConfig().getBoolean("Settings.Message.actionbar", false);
        
        String message = plugin.getConfig().getString("Message." + path, "");
        if (message.isEmpty()) return;
        
        String coloredMessage = color(message);
        
        if (chatEnabled) {
            player.sendMessage(coloredMessage);
        }
        
        if (actionBarEnabled) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(coloredMessage));
        }
    }
    
    public static void sendMessage(Player player, String path, String placeholder, String replacement) {
        if (player == null) return;
        
        boolean chatEnabled = plugin.getConfig().getBoolean("Settings.Message.chat", true);
        boolean actionBarEnabled = plugin.getConfig().getBoolean("Settings.Message.actionbar", false);
        
        String message = plugin.getConfig().getString("Message." + path, "");
        if (message.isEmpty()) return;
        
        message = message.replace(placeholder, replacement);
        String coloredMessage = color(message);
        
        if (chatEnabled) {
            player.sendMessage(coloredMessage);
        }
        
        if (actionBarEnabled) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(coloredMessage));
        }
    }
    
    public static void broadcast(String path, String placeholder, String replacement) {
        boolean chatEnabled = plugin.getConfig().getBoolean("Settings.Message.chat", true);
        
        String message = plugin.getConfig().getString("Message." + path, "");
        if (message.isEmpty()) return;
        
        message = message.replace(placeholder, replacement);
        String coloredMessage = color(message);
        
        if (chatEnabled) {
            plugin.getServer().broadcastMessage(coloredMessage);
        }
    }
    
    public static void sendHelp(Player player) {
        for (String line : plugin.getConfig().getStringList("Message.Help")) {
            player.sendMessage(color(line));
        }
    }
          }
