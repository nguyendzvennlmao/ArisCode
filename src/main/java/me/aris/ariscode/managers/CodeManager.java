package me.aris.ariscode.managers;

import me.aris.ariscode.ArisCode;
import me.aris.ariscode.models.GiftCode;
import me.aris.ariscode.models.CodeType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CodeManager {
    
    private final ArisCode plugin;
    private final Map<String, GiftCode> giftCodes;
    private final File dataFile;
    private YamlConfiguration dataConfig;
    
    public CodeManager(ArisCode plugin) {
        this.plugin = plugin;
        this.giftCodes = new HashMap<>();
        this.dataFile = new File(plugin.getDataFolder(), "ariscode.yml");
        loadData();
    }
    
    private void loadData() {
        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
                dataConfig = YamlConfiguration.loadConfiguration(dataFile);
                dataConfig.save(dataFile);
            } catch (IOException e) {
                plugin.getLogger().warning("Khong the tao file ariscode.yml");
                e.printStackTrace();
            }
        }
        
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        giftCodes.clear();
        
        for (String code : dataConfig.getKeys(false)) {
            try {
                GiftCode giftCode = new GiftCode(code, 
                    CodeType.valueOf(dataConfig.getString(code + ".Type", "NORMAL").toUpperCase()));
                
                giftCode.setLimit(dataConfig.getInt(code + ".Limit", 1));
                giftCode.setMaxUse(dataConfig.getInt(code + ".MaxUse", 0));
                giftCode.setRequireOnline(dataConfig.getInt(code + ".RequireOnline", 0));
                giftCode.setMoney(dataConfig.getDouble(code + ".Money", 0));
                giftCode.setPoints(dataConfig.getInt(code + ".Points", 0));
                giftCode.setExp(dataConfig.getInt(code + ".Exp", 0));
                giftCode.setCurrentUse(dataConfig.getInt(code + ".Use", 0));
                giftCode.setUsedPlayers(dataConfig.getStringList(code + ".Used"));
                giftCode.setUsedIps(dataConfig.getStringList(code + ".IpUsed"));
                giftCode.setRandomCodes(dataConfig.getStringList(code + ".Giftcode"));
                giftCode.setExpiredDate(dataConfig.getString(code + ".Expired", ""));
                giftCode.setCommands(dataConfig.getStringList(code + ".Command"));
                
                ConfigurationSection itemSection = dataConfig.getConfigurationSection(code + ".Item");
                if (itemSection != null) {
                    List<ItemStack> items = new ArrayList<>();
                    for (String key : itemSection.getKeys(false)) {
                        items.add(dataConfig.getItemStack(code + ".Item." + key));
                    }
                    giftCode.setItems(items);
                }
                
                giftCodes.put(code, giftCode);
            } catch (Exception e) {
                plugin.getLogger().warning("Loi khi tai code: " + code);
            }
        }
    }
    
    public void saveAll() {
        for (GiftCode giftCode : giftCodes.values()) {
            saveGiftCode(giftCode);
        }
        try {
            dataConfig.save(dataFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void saveGiftCode(GiftCode giftCode) {
        String code = giftCode.getCode();
        dataConfig.set(code + ".Type", giftCode.getType().toString());
        dataConfig.set(code + ".Limit", giftCode.getLimit());
        dataConfig.set(code + ".MaxUse", giftCode.getMaxUse());
        dataConfig.set(code + ".RequireOnline", giftCode.getRequireOnline());
        dataConfig.set(code + ".Money", giftCode.getMoney());
        dataConfig.set(code + ".Points", giftCode.getPoints());
        dataConfig.set(code + ".Exp", giftCode.getExp());
        dataConfig.set(code + ".Use", giftCode.getCurrentUse());
        dataConfig.set(code + ".Used", giftCode.getUsedPlayers());
        dataConfig.set(code + ".IpUsed", giftCode.getUsedIps());
        dataConfig.set(code + ".Giftcode", giftCode.getRandomCodes());
        dataConfig.set(code + ".Expired", giftCode.getExpiredDate());
        dataConfig.set(code + ".Command", giftCode.getCommands());
        
        dataConfig.set(code + ".Item", null);
        for (int i = 0; i < giftCode.getItems().size(); i++) {
            ItemStack item = giftCode.getItems().get(i);
            if (item != null) {
                dataConfig.set(code + ".Item." + i, item);
            }
        }
        
        try {
            dataConfig.save(dataFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void createGiftCode(String code, CodeType type) {
        if (giftCodes.containsKey(code)) return;
        giftCodes.put(code, new GiftCode(code, type));
        saveGiftCode(giftCodes.get(code));
    }
    
    public void deleteGiftCode(String code) {
        giftCodes.remove(code);
        dataConfig.set(code, null);
        try {
            dataConfig.save(dataFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public GiftCode getGiftCode(String code) {
        return giftCodes.get(code);
    }
    
    public boolean exists(String code) {
        return giftCodes.containsKey(code);
    }
    
    public Map<String, GiftCode> getAllGiftCodes() {
        return giftCodes;
    }
    
    public void addUsedPlayer(GiftCode giftCode, String playerName, String ip) {
        giftCode.getUsedPlayers().add(playerName);
        if (ip != null && !ip.isEmpty()) {
            giftCode.getUsedIps().add(ip);
        }
        giftCode.setCurrentUse(giftCode.getCurrentUse() + 1);
        saveGiftCode(giftCode);
    }
    
    public boolean hasUsed(GiftCode giftCode, String playerName) {
        return giftCode.getUsedPlayers().contains(playerName);
    }
    
    public boolean hasUsedIp(GiftCode giftCode, String ip) {
        return giftCode.getUsedIps().contains(ip);
    }
    
    public boolean isExpired(GiftCode giftCode) {
        String expiredStr = giftCode.getExpiredDate();
        if (expiredStr == null || expiredStr.isEmpty()) return false;
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
            Date expired = sdf.parse(expiredStr);
            Date now = new Date();
            return now.after(expired);
        } catch (Exception e) {
            return false;
        }
    }
    
    public List<String> generateRandomCodes(int amount, String format) {
        List<String> codes = new ArrayList<>();
        String[] chars = {"1","2","3","4","5","6","7","8","9","0",
                         "Q","W","E","R","T","Y","U","I","O","P",
                         "A","S","D","F","G","H","J","K","L",
                         "Z","X","C","V","B","N","M"};
        
        for (int i = 0; i < amount; i++) {
            StringBuilder code = new StringBuilder();
            String[] parts = format.split("-");
            
            for (int p = 0; p < parts.length; p++) {
                if (p > 0) code.append("-");
                for (int c = 0; c < parts[p].length(); c++) {
                    Random rand = new Random();
                    code.append(chars[rand.nextInt(chars.length)]);
                }
            }
            codes.add(code.toString());
        }
        return codes;
    }
    
    public int getPlayerOnlineHours(org.bukkit.entity.Player player) {
        try {
            return player.getStatistic(org.bukkit.Statistic.valueOf("PLAY_ONE_MINUTE")) / 60;
        } catch (Exception e) {
            return 0;
        }
    }
    
    public int countElement(List<String> list, String element) {
        int count = 0;
        for (String elem : list) {
            if (elem.equals(element)) {
                count++;
            }
        }
        return count;
    }
        }
