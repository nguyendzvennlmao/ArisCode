package vennlmao.ariscode;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import java.util.List;

public class CodeCommand implements CommandExecutor {
    private final Main plugin;
    public CodeCommand(Main plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;

        if (args.length == 0) return true;

        String name = args[0];
        if (!plugin.getCodeConfig().contains("codes." + name)) return true;

        String ip = p.getAddress().getAddress().getHostAddress().replace(".", "_");
        List<String> claimed = plugin.getConfig().getStringList("claimed-ips." + name);

        if (claimed.contains(ip)) {
            sendNotify(p, name, "already-claimed");
            return true;
        }

        double money = plugin.getCodeConfig().getDouble("codes." + name + ".money");

        claimed.add(ip);
        plugin.getConfig().set("claimed-ips." + name, claimed);
        plugin.saveConfig();

        sendNotify(p, name, "success");
        return true;
    }

    private void sendNotify(Player p, String code, String type) {
        String path = "codes." + code + ".messages.";
        boolean chat = plugin.getCodeConfig().getBoolean(path + "chat");
        boolean action = plugin.getCodeConfig().getBoolean(path + "actionbar");
        String msg = plugin.getCodeConfig().getString(path + type);

        if (msg == null || msg.isEmpty()) return;
        String colored = ChatColor.translateAlternateColorCodes('&', msg);

        if (chat) p.sendMessage(colored);
        if (action) p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(colored));
    }
}
