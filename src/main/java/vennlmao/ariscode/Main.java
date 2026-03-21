package vennlmao.ariscode;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin implements Listener {
    public File configf, ariscodef;
    public FileConfiguration config, ariscode;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadFiles();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    public void loadFiles() {
        configf = new File(getDataFolder(), "config.yml");
        ariscodef = new File(getDataFolder(), "ariscode.yml");
        if (!ariscodef.exists()) {
            try { 
                getDataFolder().mkdirs();
                ariscodef.createNewFile(); 
            } catch (Exception e) {}
        }
        config = YamlConfiguration.loadConfiguration(configf);
        ariscode = YamlConfiguration.loadConfiguration(ariscodef);
    }

    public void sendMsg(Player p, String path) {
        if (!config.contains("Message." + path)) return;
        String msg = config.getString("Message." + path + ".text");
        boolean canChat = config.getBoolean("Message." + path + ".chat", true);
        boolean canBar = config.getBoolean("Message." + path + ".actionbar", false);

        if (msg == null) return;
        String f = ChatColor.translateAlternateColorCodes('&', msg);

        if (canChat) p.sendMessage(f);
        if (canBar) p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(f));
    }

    public void saveArisData() {
        Bukkit.getAsyncScheduler().runNow(this, (task) -> {
            try { ariscode.save(ariscodef); } catch (Exception e) {}
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;

        if (args.length == 0) {
            config.getStringList("Help").forEach(s -> p.sendMessage(ChatColor.translateAlternateColorCodes('&', s)));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload") && p.hasPermission("ariscode.admin")) {
            loadFiles();
            p.sendMessage("§a[ArisCode] Reloaded!");
            return true;
        }

        if (args[0].equalsIgnoreCase("create") && p.hasPermission("ariscode.admin")) {
            if (args.length < 2) return true;
            p.getScheduler().run(this, (t) -> {
                p.openInventory(Bukkit.createInventory(null, 54, "§0Cài đặt: " + args[1]));
            }, null);
            return true;
        }

        handleClaim(p, args[0]);
        return true;
    }

    private void handleClaim(Player p, String code) {
        if (!ariscode.contains(code)) {
            sendMsg(p, "CodeNotExist");
            return;
        }

        if (config.getBoolean("Settings.IP-Check")) {
            String ip = p.getAddress().getAddress().getHostAddress().replace(".", "_");
            List<String> ips = ariscode.getStringList(code + ".used-ips");
            if (ips.contains(ip)) {
                sendMsg(p, "IPLimit");
                return;
            }
            ips.add(ip);
            ariscode.set(code + ".used-ips", ips);
            saveArisData();
        }

        p.getScheduler().run(this, (t) -> {
            sendMsg(p, "CodeReceived");
        }, null);
    }
  }
