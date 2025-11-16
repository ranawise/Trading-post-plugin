package com.tradingpost.model;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class TradeItem {
    
    private final UUID id;
    private final ItemStack itemStack;
    private final ItemStack requestedItem;
    private final long listedAt;
    
    public TradeItem(ItemStack itemStack, ItemStack requestedItem) {
        this.id = UUID.randomUUID();
        this.itemStack = itemStack.clone();
        this.requestedItem = requestedItem != null ? requestedItem.clone() : null;
        this.listedAt = System.currentTimeMillis();
    }
    
    public TradeItem(UUID id, ItemStack itemStack, ItemStack requestedItem, long listedAt) {
        this.id = id;
        this.itemStack = itemStack.clone();
        this.requestedItem = requestedItem != null ? requestedItem.clone() : null;
        this.listedAt = listedAt;
    }
    
    public UUID getId() {
        return id;
    }
    
    public ItemStack getItemStack() {
        return itemStack.clone();
    }
    
    public ItemStack getRequestedItem() {
        return requestedItem != null ? requestedItem.clone() : null;
    }
    
    public boolean hasRequestedItem() {
        return requestedItem != null;
    }
    
    public long getListedAt() {
        return listedAt;
    }
}
