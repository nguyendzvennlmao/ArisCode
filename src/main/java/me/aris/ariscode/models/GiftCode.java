package me.aris.ariscode.models;

import org.bukkit.inventory.ItemStack;
import java.util.*;

public class GiftCode {
    private final String code;
    private final CodeType type;
    private int limit;
    private int maxUse;
    private int requireOnline;
    private String expiredDate;
    private double money;
    private int points;
    private int exp;
    private List<ItemStack> items;
    private List<String> commands;
    private List<String> usedPlayers;
    private List<String> usedIps;
    private int currentUse;
    private List<String> randomCodes;
    
    public GiftCode(String code, CodeType type) {
        this.code = code;
        this.type = type;
        this.limit = 1;
        this.maxUse = 0;
        this.requireOnline = 0;
        this.expiredDate = "";
        this.money = 0;
        this.points = 0;
        this.exp = 0;
        this.items = new ArrayList<>();
        this.commands = new ArrayList<>();
        this.usedPlayers = new ArrayList<>();
        this.usedIps = new ArrayList<>();
        this.currentUse = 0;
        this.randomCodes = new ArrayList<>();
    }
    
    public String getCode() { return code; }
    public CodeType getType() { return type; }
    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }
    public int getMaxUse() { return maxUse; }
    public void setMaxUse(int maxUse) { this.maxUse = maxUse; }
    public int getRequireOnline() { return requireOnline; }
    public void setRequireOnline(int requireOnline) { this.requireOnline = requireOnline; }
    public String getExpiredDate() { return expiredDate; }
    public void setExpiredDate(String expiredDate) { this.expiredDate = expiredDate; }
    public double getMoney() { return money; }
    public void setMoney(double money) { this.money = money; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    public int getExp() { return exp; }
    public void setExp(int exp) { this.exp = exp; }
    public List<ItemStack> getItems() { return items; }
    public void setItems(List<ItemStack> items) { this.items = items; }
    public List<String> getCommands() { return commands; }
    public void setCommands(List<String> commands) { this.commands = commands; }
    public List<String> getUsedPlayers() { return usedPlayers; }
    public void setUsedPlayers(List<String> usedPlayers) { this.usedPlayers = usedPlayers; }
    public List<String> getUsedIps() { return usedIps; }
    public void setUsedIps(List<String> usedIps) { this.usedIps = usedIps; }
    public int getCurrentUse() { return currentUse; }
    public void setCurrentUse(int currentUse) { this.currentUse = currentUse; }
    public List<String> getRandomCodes() { return randomCodes; }
    public void setRandomCodes(List<String> randomCodes) { this.randomCodes = randomCodes; }
                    }
