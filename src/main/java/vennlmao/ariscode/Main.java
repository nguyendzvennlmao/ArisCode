package vennlmao.ariscode;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

public class Main extends JavaPlugin {
    private File codeFile;
    private FileConfiguration codeConfig;

    @Override
    public void onEnable() {
        getServer().getConsoleSender().sendMessage("§e[ArisCode] §fPlugin đang hoạt động...");
        getServer().getConsoleSender().sendMessage("§e[ArisCode] §bArisCode by VennLMAO");
        
        saveDefaultConfig();
        createCodeConfig();
        
        getCommand("code").setExecutor(new CodeCommand(this));
        getCommand("ariscode").setExecutor(new AdminCommand(this));
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
    }

    private void createCodeConfig() {
        codeFile = new File(getDataFolder(), "ariscode.yml");
        if (!codeFile.exists()) saveResource("ariscode.yml", false);
        codeConfig = YamlConfiguration.loadConfiguration(codeFile);
    }

    public FileConfiguration getCodeConfig() { return codeConfig; }
    public void saveCodeConfig() {
        try { codeConfig.save(codeFile); } catch (Exception e) { e.printStackTrace(); }
    }
          }
