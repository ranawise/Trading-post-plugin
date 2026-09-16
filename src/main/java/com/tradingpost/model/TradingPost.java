package com.tradingpost.model;

import org.bukkit.Location;

import java.util.*;

public class TradingPost {
    
    private final UUID id;
    private final UUID ownerId;
    private final String ownerName;
    private final Location location;
    private final Map<UUID, TradeItem> items;
    private final long createdAt;
    
    public TradingPost(UUID ownerId, String ownerName, Location location) {
        this.id = UUID.randomUUID();
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.location = location;
        this.items = new HashMap<>();
        this.createdAt = System.currentTimeMillis();
    }
    
    public TradingPost(UUID id, UUID ownerId, String ownerName, Location location, 
                      Map<UUID, TradeItem> items, long createdAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.location = location;
        this.items = items;
        this.createdAt = createdAt;
    }
    
    public UUID getId() {
        return id;
    }
    
    public UUID getOwnerId() {
        return ownerId;
    }
    
    public String getOwnerName() {
        return ownerName;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public Map<UUID, TradeItem> getItems() {
        return new HashMap<>(items);
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void addItem(TradeItem item) {
        items.put(item.getId(), item);
    }
    
    public void removeItem(UUID itemId) {
        items.remove(itemId);
    }
    
    public TradeItem getItem(UUID itemId) {
        return items.get(itemId);
    }
    
    public boolean hasItem(UUID itemId) {
        return items.containsKey(itemId);
    }
    
    public int getItemCount() {
        return items.size();
    }
    
    public List<TradeItem> getAvailableItems() {
        return new ArrayList<>(items.values());
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
}
