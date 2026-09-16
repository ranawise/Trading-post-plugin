package com.tradingpost.data;

import com.tradingpost.TradingPostPlugin;
import com.tradingpost.model.TradeItem;
import com.tradingpost.model.TradingPost;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class TradingPostManager {
    
    private final TradingPostPlugin plugin;
    private final Map<UUID, TradingPost> tradingPosts;
    private final Map<UUID, List<UUID>> playerPosts;
    private final File dataFile;
    
    public TradingPostManager(TradingPostPlugin plugin) {
        this.plugin = plugin;
        this.tradingPosts = new HashMap<>();
        this.playerPosts = new HashMap<>();
        this.dataFile = new File(plugin.getDataFolder(), "tradingposts.yml");
    }
    
    public TradingPost createTradingPost(Player player, Location location) {
        TradingPost post = new TradingPost(player.getUniqueId(), player.getName(), location);
        tradingPosts.put(post.getId(), post);
        
        playerPosts.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()).add(post.getId());
        
        return post;
    }
    
    public boolean removeTradingPost(UUID postId) {
        TradingPost post = tradingPosts.remove(postId);
        if (post != null) {
            List<UUID> posts = playerPosts.get(post.getOwnerId());
            if (posts != null) {
                posts.remove(postId);
                if (posts.isEmpty()) {
                    playerPosts.remove(post.getOwnerId());
                }
            }
            return true;
        }
        return false;
    }
    
    public TradingPost getTradingPost(UUID postId) {
        return tradingPosts.get(postId);
    }
    
    public List<TradingPost> getAllTradingPosts() {
        return new ArrayList<>(tradingPosts.values());
    }
    
    public List<TradingPost> getPlayerTradingPosts(UUID playerId) {
        List<UUID> postIds = playerPosts.get(playerId);
        if (postIds == null) {
            return new ArrayList<>();
        }
        
        return postIds.stream()
                .map(tradingPosts::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    public int getPlayerPostCount(UUID playerId) {
        List<UUID> posts = playerPosts.get(playerId);
        return posts != null ? posts.size() : 0;
    }
    
    public TradingPost getNearestTradingPost(Location location, double maxDistance) {
        return tradingPosts.values().stream()
                .filter(post -> post.getLocation().getWorld().equals(location.getWorld()))
                .filter(post -> post.getLocation().distance(location) <= maxDistance)
                .min(Comparator.comparingDouble(post -> post.getLocation().distance(location)))
                .orElse(null);
    }
    
    public boolean hasNearbyTradingPost(Location location, double minDistance) {
        return tradingPosts.values().stream()
                .filter(post -> post.getLocation().getWorld().equals(location.getWorld()))
                .anyMatch(post -> post.getLocation().distance(location) < minDistance);
    }
    
    public void addItemToPost(UUID postId, TradeItem item) {
        TradingPost post = tradingPosts.get(postId);
        if (post != null) {
            post.addItem(item);
        }
    }
    
    public void removeItemFromPost(UUID postId, UUID itemId) {
        TradingPost post = tradingPosts.get(postId);
        if (post != null) {
            post.removeItem(itemId);
        }
    }
    
    public void saveTradingPosts() {
        try {
            if (!dataFile.exists()) {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            }
            
            FileConfiguration data = new YamlConfiguration();
            
            for (TradingPost post : tradingPosts.values()) {
                String path = "posts." + post.getId().toString();
                data.set(path + ".owner-id", post.getOwnerId().toString());
                data.set(path + ".owner-name", post.getOwnerName());
                data.set(path + ".created-at", post.getCreatedAt());
                
                Location loc = post.getLocation();
                data.set(path + ".location.world", loc.getWorld().getName());
                data.set(path + ".location.x", loc.getX());
                data.set(path + ".location.y", loc.getY());
                data.set(path + ".location.z", loc.getZ());
                data.set(path + ".location.yaw", loc.getYaw());
                data.set(path + ".location.pitch", loc.getPitch());
                
                int itemIndex = 0;
                for (TradeItem item : post.getItems().values()) {
                    String itemPath = path + ".items." + itemIndex;
                    data.set(itemPath + ".id", item.getId().toString());
                    data.set(itemPath + ".item", item.getItemStack());
                    data.set(itemPath + ".requested", item.getRequestedItem());
                    data.set(itemPath + ".listed-at", item.getListedAt());
                    itemIndex++;
                }
            }
            
            data.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save trading posts: " + e.getMessage());
        }
    }
    
    public void loadTradingPosts() {
        if (!dataFile.exists()) {
            return;
        }
        
        try {
            FileConfiguration data = YamlConfiguration.loadConfiguration(dataFile);
            ConfigurationSection postsSection = data.getConfigurationSection("posts");
            
            if (postsSection == null) {
                return;
            }
            
            for (String postIdStr : postsSection.getKeys(false)) {
                try {
                    UUID postId = UUID.fromString(postIdStr);
                    String path = "posts." + postIdStr;
                    
                    UUID ownerId = UUID.fromString(data.getString(path + ".owner-id"));
                    String ownerName = data.getString(path + ".owner-name");
                    long createdAt = data.getLong(path + ".created-at");
                    
                    String worldName = data.getString(path + ".location.world");
                    World world = Bukkit.getWorld(worldName);
                    if (world == null) {
                        plugin.getLogger().warning("World not found for trading post: " + worldName);
                        continue;
                    }
                    
                    double x = data.getDouble(path + ".location.x");
                    double y = data.getDouble(path + ".location.y");
                    double z = data.getDouble(path + ".location.z");
                    float yaw = (float) data.getDouble(path + ".location.yaw");
                    float pitch = (float) data.getDouble(path + ".location.pitch");
                    Location location = new Location(world, x, y, z, yaw, pitch);
                    
                    Map<UUID, TradeItem> items = new HashMap<>();
                    ConfigurationSection itemsSection = data.getConfigurationSection(path + ".items");
                    if (itemsSection != null) {
                        for (String itemIndexStr : itemsSection.getKeys(false)) {
                            String itemPath = path + ".items." + itemIndexStr;
                            UUID itemId = UUID.fromString(data.getString(itemPath + ".id"));
                            ItemStack itemStack = data.getItemStack(itemPath + ".item");
                            ItemStack requestedItem = data.getItemStack(itemPath + ".requested");
                            long listedAt = data.getLong(itemPath + ".listed-at");
                            
                            if (itemStack != null) {
                                TradeItem tradeItem = new TradeItem(itemId, itemStack, requestedItem, listedAt);
                                items.put(itemId, tradeItem);
                            }
                        }
                    }
                    
                    TradingPost post = new TradingPost(postId, ownerId, ownerName, location, items, createdAt);
                    tradingPosts.put(postId, post);
                    playerPosts.computeIfAbsent(ownerId, k -> new ArrayList<>()).add(postId);
                    
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to load trading post " + postIdStr + ": " + e.getMessage());
                }
            }
            
            plugin.getLogger().info("Loaded " + tradingPosts.size() + " trading posts");
            
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load trading posts: " + e.getMessage());
        }
    }
}
