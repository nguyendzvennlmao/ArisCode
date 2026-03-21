package vennlmao.ariscode;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AdminCommand implements CommandExecutor {
    private final Main plugin;
    public AdminCommand(Main plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;

        if (args.length >= 2 && args[0].equalsIgnoreCase("create")) {
            String name = args[1];
            plugin.getCodeConfig().set("codes." + name + ".money", 0);
            plugin.saveCodeConfig();
            p.sendMessage("§a[ArisCode] Đã tạo code: " + name);
            return true;
        }

        if (args.length >= 2 && args[0].equalsIgnoreCase("edit")) {
            openGUI(p, args[1]);
            return true;
        }
        return false;
    }

    private void openGUI(Player p, String name) {
        Inventory inv = Bukkit.createInventory(null, 9, "§0Edit Code: " + name);
        inv.setItem(0, createItem(Material.EMERALD, "§aSet Money"));
        inv.setItem(7, createItem(Material.RED_STAINED_GLASS_PANE, "§cHủy"));
        inv.setItem(8, createItem(Material.LIME_STAINED_GLASS_PANE, "§aXác nhận"));
        p.openInventory(inv);
    }

    private ItemStack createItem(Material m, String name) {
        ItemStack i = new ItemStack(m);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(name);
        i.setItemMeta(meta);
        return i;
    }
          }
