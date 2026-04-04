package me.aris.ariscode;

import me.aris.ariscode.commands.CodeCommand;
import me.aris.ariscode.managers.ConfigManager;
import me.aris.ariscode.managers.CodeManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ArisCode extends JavaPlugin {
    
    private static ArisCode instance;
    private ConfigManager configManager;
    private CodeManager codeManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        saveDefaultConfig();
        
        this.configManager = new ConfigManager(this);
        this.codeManager = new CodeManager(this);
        
        CodeCommand command = new CodeCommand(this);
        getCommand("ariscode").setExecutor(command);
        getCommand("ariscode").setTabCompleter(command);
        
        getLogger().info("§e╔════════════════════════════════╗");
        getLogger().info("§e║       §6ArisCode §fv1.0§e       §e║");
        getLogger().info("§e║     §fAuthor: §aVennLMAO§e      ║");
        getLogger().info("§e║   §fSupport: §b1.20.x - 1.21.x§e  ║");
        getLogger().info("§e╚════════════════════════════════╝");
        getLogger().info("§aPlugin da duoc kich hoat thanh cong!");
    }
    
    @Override
    public void onDisable() {
        if (codeManager != null) {
            codeManager.saveAll();
        }
        getLogger().info("§cArisCode da duoc tat!");
    }
    
    public static ArisCode getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public CodeManager getCodeManager() {
        return codeManager;
    }
          }
