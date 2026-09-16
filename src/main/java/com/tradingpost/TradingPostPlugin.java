package com.tradingpost;

import com.tradingpost.commands.TradingPostCommand;
import com.tradingpost.config.ConfigManager;
import com.tradingpost.data.TradingPostManager;
import com.tradingpost.economy.EconomyManager;
import com.tradingpost.gui.GUIListener;
import org.bukkit.plugin.java.JavaPlugin;

public class TradingPostPlugin extends JavaPlugin {
    
    private static TradingPostPlugin instance;
    private ConfigManager configManager;
    private TradingPostManager tradingPostManager;
    private EconomyManager economyManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        saveDefaultConfig();
        
        configManager = new ConfigManager(this);
        economyManager = new EconomyManager(this);
        tradingPostManager = new TradingPostManager(this);
        
        if (economyManager.isVaultEnabled()) {
            getLogger().info("Vault economy integration enabled!");
        } else {
            getLogger().info("Vault not found - using item-based fees only");
        }
        
        TradingPostCommand commandExecutor = new TradingPostCommand(this);
        getCommand("tradingpost").setExecutor(commandExecutor);
        getCommand("tradingpost").setTabCompleter(commandExecutor);
        
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        
        tradingPostManager.loadTradingPosts();
        
        getLogger().info("TradingPost plugin has been enabled!");
    }
    
    @Override
    public void onDisable() {
        if (tradingPostManager != null) {
            tradingPostManager.saveTradingPosts();
        }
        
        getLogger().info("TradingPost plugin has been disabled!");
    }
    
    public static TradingPostPlugin getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public TradingPostManager getTradingPostManager() {
        return tradingPostManager;
    }
    
    public EconomyManager getEconomyManager() {
        return economyManager;
    }
}
