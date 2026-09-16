package com.tradingpost.config;

import com.tradingpost.TradingPostPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {
    
    private final TradingPostPlugin plugin;
    private FileConfiguration config;
    
    public ConfigManager(TradingPostPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }
    
    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }
    
    public int getMaxTradingSlots() {
        return config.getInt("max-trading-slots", 5);
    }
    
    public boolean isTradingFeeEnabled() {
        return config.getBoolean("trading-fee.enabled", true);
    }
    
    public String getFeeType() {
        return config.getString("trading-fee.type", "ITEM");
    }
    
    public double getMoneyAmount() {
        return config.getDouble("trading-fee.money-amount", 100.0);
    }
    
    public Material getItemMaterial() {
        String materialName = config.getString("trading-fee.item-material", "DIAMOND");
        try {
            return Material.valueOf(materialName);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid material in config: " + materialName + ". Using DIAMOND.");
            return Material.DIAMOND;
        }
    }
    
    public int getItemAmount() {
        return config.getInt("trading-fee.item-amount", 1);
    }
    
    public double getMaxDistance() {
        return config.getDouble("trading-post.max-distance", 5.0);
    }
    
    public List<String> getAllowedWorlds() {
        return config.getStringList("trading-post.allowed-worlds");
    }
    
    public double getMinDistanceBetweenPosts() {
        return config.getDouble("trading-post.min-distance-between-posts", 10.0);
    }
    
    public String getBrowseTitle() {
        return colorize(config.getString("gui.browse-title", "&6&lTrading Posts"));
    }
    
    public String getItemsTitle() {
        return colorize(config.getString("gui.items-title", "&6&lAvailable Items"));
    }
    
    public String getConfirmTitle() {
        return colorize(config.getString("gui.confirm-title", "&6&lConfirm Trade"));
    }
    
    public String getMessage(String key) {
        String prefix = colorize(config.getString("messages.prefix", "&8[&6TradingPost&8]&r "));
        String message = config.getString("messages." + key, "&cMessage not found: " + key);
        return prefix + colorize(message);
    }
    
    public String getMessageWithoutPrefix(String key) {
        String message = config.getString("messages." + key, "&cMessage not found: " + key);
        return colorize(message);
    }
    
    public String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
