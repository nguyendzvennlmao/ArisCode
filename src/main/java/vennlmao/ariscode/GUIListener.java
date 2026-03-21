package vennlmao.ariscode;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.UUID;

public class GUIListener implements Listener {
    private final Main plugin;
    private final HashMap<UUID, String> inputMoney = new HashMap<>();
    private final HashMap<UUID, String> inputExpiry = new HashMap<>();

    public GUIListener(Main plugin) { this.plugin = plugin; }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().startsWith("§0Edit Code:")) return;
        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();
        String code = e.getView().getTitle().replace("§0Edit Code: ", "");

        if (e.getRawSlot() == 0) {
            p.closeInventory();
            p.sendMessage("§eNhập số tiền:");
            inputMoney.put(p.getUniqueId(), code);
        } else if (e.getRawSlot() == 7) {
            p.closeInventory();
            p.sendMessage("§cĐã hủy.");
        } else if (e.getRawSlot() == 8) {
            p.closeInventory();
            p.sendMessage("§eNhập ngày hết hạn (0=vĩnh viễn):");
            inputExpiry.put(p.getUniqueId(), code);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (inputMoney.containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            String code = inputMoney.remove(p.getUniqueId());
            try {
                plugin.getCodeConfig().set("codes." + code + ".money", Double.parseDouble(e.getMessage()));
                plugin.saveCodeConfig();
                p.sendMessage("§aĐã cập nhật tiền!");
            } catch (Exception ex) {}
        } else if (inputExpiry.containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            String code = inputExpiry.remove(p.getUniqueId());
            try {
                plugin.getCodeConfig().set("codes." + code + ".expiry-days", Integer.parseInt(e.getMessage()));
                plugin.saveCodeConfig();
                p.sendMessage("§aEdit thành công by VennLMAO!");
            } catch (Exception ex) {}
        }
    }
}
