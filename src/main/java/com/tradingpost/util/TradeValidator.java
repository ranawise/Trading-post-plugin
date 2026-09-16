package com.tradingpost.util;

import com.tradingpost.TradingPostPlugin;
import com.tradingpost.model.TradeItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TradeValidator {
    
    private final TradingPostPlugin plugin;
    
    public TradeValidator(TradingPostPlugin plugin) {
        this.plugin = plugin;
    }
    
    public boolean validateTrade(Player player, TradeItem item) {
        if (item.hasRequestedItem()) {
            ItemStack requested = item.getRequestedItem();
            
            if (!hasItem(player, requested)) {
                player.sendMessage(plugin.getConfigManager().getMessage("insufficient-fee")
                        .replace("{amount}", String.valueOf(requested.getAmount()))
                        .replace("{item}", formatItemName(requested)));
                return false;
            }
        }
        
        if (!hasInventorySpace(player, item.getItemStack())) {
            player.sendMessage(plugin.getConfigManager().getMessage("inventory-full"));
            return false;
        }
        
        return true;
    }
    
    private boolean hasItem(Player player, ItemStack item) {
        int count = 0;
        
        for (ItemStack invItem : player.getInventory().getContents()) {
            if (invItem != null && invItem.isSimilar(item)) {
                count += invItem.getAmount();
                if (count >= item.getAmount()) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private boolean hasInventorySpace(Player player, ItemStack item) {
        int emptySlots = 0;
        int partialSlots = 0;
        
        for (ItemStack invItem : player.getInventory().getStorageContents()) {
            if (invItem == null) {
                emptySlots++;
            } else if (invItem.isSimilar(item) && invItem.getAmount() < invItem.getMaxStackSize()) {
                partialSlots++;
            }
        }
        
        return emptySlots > 0 || partialSlots > 0;
    }
    
    private String formatItemName(ItemStack item) {
        String name = item.getType().name().replace("_", " ");
        String[] words = name.split(" ");
        StringBuilder formatted = new StringBuilder();
        
        for (String word : words) {
            if (formatted.length() > 0) {
                formatted.append(" ");
            }
            formatted.append(word.substring(0, 1).toUpperCase())
                    .append(word.substring(1).toLowerCase());
        }
        
        return formatted.toString();
    }
}
