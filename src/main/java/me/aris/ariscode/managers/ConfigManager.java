package me.aris.ariscode.managers;

import me.aris.ariscode.ArisCode;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    
    private final ArisCode plugin;
    private FileConfiguration config;
    
    public ConfigManager(ArisCode plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }
    
    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }
    
    public String getString(String path) {
        return config.getString(path, "");
    }
    
    public boolean getBoolean(String path) {
        return config.getBoolean(path, false);
    }
    
    public int getInt(String path) {
        return config.getInt(path, 0);
    }
    
    public double getDouble(String path) {
        return config.getDouble(path, 0);
    }
    
    public boolean isIpCheck() {
        return getBoolean("Settings.IP-Check");
    }
    
    public int getCodeRandom() {
        return getInt("Settings.CodeRandom");
    }
    
    public String getGiftcodeFormat() {
        return getString("Settings.GiftcodeFormat");
    }
    
    public int getRandomMaxUse() {
        return getInt("Settings.RandomMaxUse");
    }
    
    public boolean isChatEnabled() {
        return getBoolean("Settings.Message.chat");
    }
    
    public boolean isActionBarEnabled() {
        return getBoolean("Settings.Message.actionbar");
    }
}
