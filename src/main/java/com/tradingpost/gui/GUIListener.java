package com.tradingpost.gui;

import com.tradingpost.TradingPostPlugin;
import com.tradingpost.model.TradeItem;
import com.tradingpost.model.TradingPost;
import com.tradingpost.util.TradeValidator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIListener implements Listener {
    
    private final TradingPostPlugin plugin;
    private static final Map<UUID, GUIData> playerGUIs = new HashMap<>();
    
    public GUIListener(TradingPostPlugin plugin) {
        this.plugin = plugin;
    }
    
    public static void setPlayerGUI(UUID playerId, GUIType type, UUID postId) {
        playerGUIs.put(playerId, new GUIData(type, postId, null));
    }
    
    public static void setPlayerGUI(UUID playerId, GUIType type, UUID postId, UUID itemId) {
        playerGUIs.put(playerId, new GUIData(type, postId, itemId));
    }
    
    public static void removePlayerGUI(UUID playerId) {
        playerGUIs.remove(playerId);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        UUID playerId = player.getUniqueId();
        
        if (!playerGUIs.containsKey(playerId)) {
            return;
        }
        
        event.setCancelled(true);
        
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        
        GUIData guiData = playerGUIs.get(playerId);
        
        switch (guiData.type) {
            case BROWSE:
                handleBrowseClick(player, event.getSlot());
                break;
            case ITEMS:
                handleItemsClick(player, guiData.postId, event.getSlot());
                break;
            case CONFIRM:
                handleConfirmClick(player, guiData.postId, guiData.itemId, event.getSlot());
                break;
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            removePlayerGUI(event.getPlayer().getUniqueId());
        }
    }
    
    private void handleBrowseClick(Player player, int slot) {
        TradingPost post = plugin.getTradingPostManager().getAllTradingPosts().get(slot);
        
        if (post != null) {
            player.closeInventory();
            new GUIManager(plugin).openItemsGUI(player, post.getId());
        }
    }
    
    private void handleItemsClick(Player player, UUID postId, int slot) {
        TradingPost post = plugin.getTradingPostManager().getTradingPost(postId);
        
        if (post == null) {
            player.closeInventory();
            player.sendMessage(plugin.getConfigManager().getMessage("item-not-available"));
            return;
        }
        
        TradeItem item = post.getAvailableItems().get(slot);
        
        if (item != null) {
            player.closeInventory();
            new GUIManager(plugin).openConfirmGUI(player, postId, item.getId());
        }
    }
    
    private void handleConfirmClick(Player player, UUID postId, UUID itemId, int slot) {
        if (slot == 22) {
            executeTrade(player, postId, itemId);
        } else if (slot == 18) {
            player.closeInventory();
        }
    }
    
    private void executeTrade(Player player, UUID postId, UUID itemId) {
        TradingPost post = plugin.getTradingPostManager().getTradingPost(postId);
        
        if (post == null) {
            player.closeInventory();
            player.sendMessage(plugin.getConfigManager().getMessage("item-not-available"));
            return;
        }
        
        TradeItem item = post.getItem(itemId);
        
        if (item == null) {
            player.closeInventory();
            player.sendMessage(plugin.getConfigManager().getMessage("item-not-available"));
            return;
        }
        
        TradeValidator validator = new TradeValidator(plugin);
        if (!validator.validateTrade(player, item)) {
            player.closeInventory();
            return;
        }
        
        if (item.hasRequestedItem()) {
            ItemStack requested = item.getRequestedItem();
            player.getInventory().removeItem(requested);
        }
        
        ItemStack tradedItem = item.getItemStack();
        HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(tradedItem);
        
        if (!leftover.isEmpty()) {
            for (ItemStack leftoverItem : leftover.values()) {
                player.getWorld().dropItem(player.getLocation(), leftoverItem);
            }
        }
        
        plugin.getTradingPostManager().removeItemFromPost(postId, itemId);
        
        if (post.isEmpty()) {
            plugin.getTradingPostManager().removeTradingPost(postId);
        }
        
        player.closeInventory();
        player.sendMessage(plugin.getConfigManager().getMessage("trade-successful"));
    }
    
    private static class GUIData {
        final GUIType type;
        final UUID postId;
        final UUID itemId;
        
        GUIData(GUIType type, UUID postId, UUID itemId) {
            this.type = type;
            this.postId = postId;
            this.itemId = itemId;
        }
    }
}
