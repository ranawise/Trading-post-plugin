package com.tradingpost.economy;

import com.tradingpost.TradingPostPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyManager {
    
    private final TradingPostPlugin plugin;
    private Economy economy;
    private boolean vaultEnabled;
    
    public EconomyManager(TradingPostPlugin plugin) {
        this.plugin = plugin;
        this.vaultEnabled = setupEconomy();
    }
    
    private boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        
        RegisteredServiceProvider<Economy> rsp = plugin.getServer()
                .getServicesManager()
                .getRegistration(Economy.class);
        
        if (rsp == null) {
            return false;
        }
        
        economy = rsp.getProvider();
        return economy != null;
    }
    
    public boolean isVaultEnabled() {
        return vaultEnabled;
    }
    
    public boolean hasBalance(Player player, double amount) {
        if (!vaultEnabled) {
            return false;
        }
        return economy.has(player, amount);
    }
    
    public boolean withdrawMoney(Player player, double amount) {
        if (!vaultEnabled) {
            return false;
        }
        
        if (!economy.has(player, amount)) {
            return false;
        }
        
        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }
    
    public String formatMoney(double amount) {
        if (!vaultEnabled) {
            return String.valueOf(amount);
        }
        return economy.format(amount);
    }
}
