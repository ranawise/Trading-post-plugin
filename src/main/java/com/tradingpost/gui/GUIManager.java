package com.tradingpost.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.tradingpost.TradingPostPlugin;
import com.tradingpost.model.TradeItem;
import com.tradingpost.model.TradingPost;

public class GUIManager {
    
    private final TradingPostPlugin plugin;
    
    public GUIManager(TradingPostPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void openBrowseGUI(Player player) {
        List<TradingPost> posts = plugin.getTradingPostManager().getAllTradingPosts();
        
        if (posts.isEmpty()) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-trading-posts"));
            return;
        }
        
        Inventory inv = Bukkit.createInventory(null, 27, plugin.getConfigManager().getBrowseTitle());
        
        for (int i = 0; i < Math.min(posts.size(), 27); i++) {
            TradingPost post = posts.get(i);
            ItemStack icon = createTradingPostIcon(post);
            inv.setItem(i, icon);
        }
        
        player.openInventory(inv);
        GUIListener.setPlayerGUI(player.getUniqueId(), GUIType.BROWSE, null);
    }
    
    public void openItemsGUI(Player player, UUID postId) {
        TradingPost post = plugin.getTradingPostManager().getTradingPost(postId);
        
        if (post == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-trading-posts"));
            return;
        }
        
        List<TradeItem> items = post.getAvailableItems();
        
        if (items.isEmpty()) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-trading-posts"));
            return;
        }
        
        int size = Math.min(54, ((items.size() + 8) / 9) * 9);
        Inventory inv = Bukkit.createInventory(null, size, plugin.getConfigManager().getItemsTitle());
        
        for (int i = 0; i < Math.min(items.size(), 54); i++) {
            TradeItem item = items.get(i);
            ItemStack display = createTradeItemIcon(item);
            inv.setItem(i, display);
        }
        
        player.openInventory(inv);
        GUIListener.setPlayerGUI(player.getUniqueId(), GUIType.ITEMS, postId);
    }
    
    public void openConfirmGUI(Player player, UUID postId, UUID itemId) {
        TradingPost post = plugin.getTradingPostManager().getTradingPost(postId);
        
        if (post == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("item-not-available"));
            return;
        }
        
        TradeItem item = post.getItem(itemId);
        
        if (item == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("item-not-available"));
            return;
        }
        
        Inventory inv = Bukkit.createInventory(null, 27, plugin.getConfigManager().getConfirmTitle());
        
        ItemStack offeredItem = item.getItemStack().clone();
        ItemMeta offeredMeta = offeredItem.getItemMeta();
        if (offeredMeta != null) {
            List<String> lore = offeredMeta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.add("");
            lore.add(plugin.getConfigManager().colorize("&7You will receive this item"));
            offeredMeta.setLore(lore);
            offeredItem.setItemMeta(offeredMeta);
        }
        inv.setItem(11, offeredItem);
        
        if (item.hasRequestedItem()) {
            ItemStack requestedItem = item.getRequestedItem().clone();
            ItemMeta requestedMeta = requestedItem.getItemMeta();
            if (requestedMeta != null) {
                List<String> lore = requestedMeta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }
                lore.add("");
                lore.add(plugin.getConfigManager().colorize("&7You need to provide this item"));
                requestedMeta.setLore(lore);
                requestedItem.setItemMeta(requestedMeta);
            }
            inv.setItem(15, requestedItem);
        }
        
        ItemStack confirmButton = new ItemStack(Material.LIME_WOOL);
        ItemMeta confirmMeta = confirmButton.getItemMeta();
        if (confirmMeta != null) {
            confirmMeta.setDisplayName(plugin.getConfigManager().colorize("&a&lConfirm Trade"));
            List<String> confirmLore = new ArrayList<>();
            confirmLore.add(plugin.getConfigManager().colorize("&7Click to confirm the trade"));
            confirmMeta.setLore(confirmLore);
            confirmButton.setItemMeta(confirmMeta);
        }
        inv.setItem(22, confirmButton);
        
        ItemStack cancelButton = new ItemStack(Material.RED_WOOL);
        ItemMeta cancelMeta = cancelButton.getItemMeta();
        if (cancelMeta != null) {
            cancelMeta.setDisplayName(plugin.getConfigManager().colorize("&c&lCancel"));
            List<String> cancelLore = new ArrayList<>();
            cancelLore.add(plugin.getConfigManager().colorize("&7Click to cancel"));
            cancelMeta.setLore(cancelLore);
            cancelButton.setItemMeta(cancelMeta);
        }
        inv.setItem(18, cancelButton);
        
        player.openInventory(inv);
        GUIListener.setPlayerGUI(player.getUniqueId(), GUIType.CONFIRM, postId, itemId);
    }
    
    private ItemStack createTradingPostIcon(TradingPost post) {
        ItemStack icon = new ItemStack(Material.CHEST);
        ItemMeta meta = icon.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(plugin.getConfigManager().colorize("&6" + post.getOwnerName() + "'s Trading Post"));
            
            List<String> lore = new ArrayList<>();
            lore.add(plugin.getConfigManager().colorize("&7Items: &e" + post.getItemCount()));
            lore.add(plugin.getConfigManager().colorize("&7Location: &e" + 
                    (int)post.getLocation().getX() + ", " + 
                    (int)post.getLocation().getY() + ", " + 
                    (int)post.getLocation().getZ()));
            lore.add("");
            lore.add(plugin.getConfigManager().colorize("&eClick to browse items"));
            
            meta.setLore(lore);
            icon.setItemMeta(meta);
        }
        
        return icon;
    }
    
    private ItemStack createTradeItemIcon(TradeItem item) {
        ItemStack display = item.getItemStack().clone();
        ItemMeta meta = display.getItemMeta();
        
        if (meta != null) {
            List<String> lore = meta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            
            lore.add("");
            if (item.hasRequestedItem()) {
                ItemStack requested = item.getRequestedItem();
                lore.add(plugin.getConfigManager().colorize("&7Requested: &e" + 
                        requested.getAmount() + "x " + 
                        formatMaterialName(requested.getType())));
            } else {
                lore.add(plugin.getConfigManager().colorize("&7Requested: &aFree"));
            }
            lore.add("");
            lore.add(plugin.getConfigManager().colorize("&eClick to trade"));
            
            meta.setLore(lore);
            display.setItemMeta(meta);
        }
        
        return display;
    }
    
    private String formatMaterialName(Material material) {
        String name = material.name().replace("_", " ");
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
