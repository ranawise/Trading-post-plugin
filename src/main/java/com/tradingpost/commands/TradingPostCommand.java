package com.tradingpost.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.tradingpost.TradingPostPlugin;
import com.tradingpost.gui.GUIManager;
import com.tradingpost.model.TradeItem;
import com.tradingpost.model.TradingPost;

public class TradingPostCommand implements CommandExecutor, TabCompleter {
    
    private final TradingPostPlugin plugin;
    private final GUIManager guiManager;
    
    public TradingPostCommand(TradingPostPlugin plugin) {
        this.plugin = plugin;
        this.guiManager = new GUIManager(plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "create":
                handleCreate(player);
                break;
            case "list":
                handleList(player);
                break;
            case "browse":
                handleBrowse(player);
                break;
            case "remove":
                handleRemove(player, args);
                break;
            case "add":
                handleAdd(player, args);
                break;
            case "reload":
                handleReload(player);
                break;
            case "reset":
                handleReset(player, args);
                break;
            case "help":
                sendHelp(player);
                break;
            default:
                player.sendMessage(plugin.getConfigManager().colorize("&cUnknown command. Use /tradingpost help"));
                break;
        }
        
        return true;
    }
    
    private void handleCreate(Player player) {
        if (!player.hasPermission("tradingpost.create")) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return;
        }
        
        int currentPosts = plugin.getTradingPostManager().getPlayerPostCount(player.getUniqueId());
        int maxSlots = plugin.getConfigManager().getMaxTradingSlots();
        
        if (currentPosts >= maxSlots) {
            player.sendMessage(plugin.getConfigManager().getMessage("max-slots-reached")
                    .replace("{max}", String.valueOf(maxSlots)));
            return;
        }
        
        List<String> allowedWorlds = plugin.getConfigManager().getAllowedWorlds();
        if (!allowedWorlds.isEmpty() && !allowedWorlds.contains(player.getWorld().getName())) {
            player.sendMessage(plugin.getConfigManager().getMessage("invalid-location"));
            return;
        }
        
        Location location = player.getLocation();
        double minDistance = plugin.getConfigManager().getMinDistanceBetweenPosts();
        
        if (plugin.getTradingPostManager().hasNearbyTradingPost(location, minDistance)) {
            player.sendMessage(plugin.getConfigManager().getMessage("too-close")
                    .replace("{distance}", String.valueOf(minDistance)));
            return;
        }
        
        if (plugin.getConfigManager().isTradingFeeEnabled()) {
            String feeType = plugin.getConfigManager().getFeeType();
            
            if (feeType.equalsIgnoreCase("ITEM")) {
                Material feeMaterial = plugin.getConfigManager().getItemMaterial();
                int feeAmount = plugin.getConfigManager().getItemAmount();
                ItemStack feeItem = new ItemStack(feeMaterial, feeAmount);
                
                if (!hasItem(player, feeItem)) {
                    player.sendMessage(plugin.getConfigManager().getMessage("insufficient-fee")
                            .replace("{amount}", String.valueOf(feeAmount))
                            .replace("{item}", formatMaterialName(feeMaterial)));
                    return;
                }
                
                player.getInventory().removeItem(feeItem);
            } else if (feeType.equalsIgnoreCase("MONEY")) {
                if (!plugin.getEconomyManager().isVaultEnabled()) {
                    player.sendMessage(plugin.getConfigManager().colorize("&cEconomy system not available. Please use item-based fees."));
                    return;
                }
                
                double moneyAmount = plugin.getConfigManager().getMoneyAmount();
                
                if (!plugin.getEconomyManager().hasBalance(player, moneyAmount)) {
                    player.sendMessage(plugin.getConfigManager().getMessage("insufficient-fee")
                            .replace("{amount}", plugin.getEconomyManager().formatMoney(moneyAmount))
                            .replace("{item}", "money"));
                    return;
                }
                
                if (!plugin.getEconomyManager().withdrawMoney(player, moneyAmount)) {
                    player.sendMessage(plugin.getConfigManager().getMessage("insufficient-fee")
                            .replace("{amount}", plugin.getEconomyManager().formatMoney(moneyAmount))
                            .replace("{item}", "money"));
                    return;
                }
            }
        }
        
        TradingPost post = plugin.getTradingPostManager().createTradingPost(player, location);
        player.sendMessage(plugin.getConfigManager().getMessage("trading-post-created"));
    }
    
    private void handleList(Player player) {
        if (!player.hasPermission("tradingpost.use")) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return;
        }
        
        List<TradingPost> posts = plugin.getTradingPostManager().getPlayerTradingPosts(player.getUniqueId());
        
        if (posts.isEmpty()) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-trading-posts"));
            return;
        }
        
        player.sendMessage(plugin.getConfigManager().colorize("&6&lYour Trading Posts:"));
        for (int i = 0; i < posts.size(); i++) {
            TradingPost post = posts.get(i);
            Location loc = post.getLocation();
            player.sendMessage(plugin.getConfigManager().colorize(
                    "&e" + (i + 1) + ". &7Location: &f" + 
                    (int)loc.getX() + ", " + (int)loc.getY() + ", " + (int)loc.getZ() + 
                    " &7Items: &f" + post.getItemCount()));
        }
    }
    
    private void handleBrowse(Player player) {
        if (!player.hasPermission("tradingpost.browse")) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return;
        }
        
        guiManager.openBrowseGUI(player);
    }
    
    private void handleRemove(Player player, String[] args) {
        if (!player.hasPermission("tradingpost.remove")) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return;
        }
        
        double maxDistance = plugin.getConfigManager().getMaxDistance();
        TradingPost nearestPost = plugin.getTradingPostManager().getNearestTradingPost(
                player.getLocation(), maxDistance);
        
        if (nearestPost == null) {
            player.sendMessage(plugin.getConfigManager().colorize(
                    "&cNo trading post found within " + maxDistance + " blocks."));
            return;
        }
        
        if (!nearestPost.getOwnerId().equals(player.getUniqueId()) && 
            !player.hasPermission("tradingpost.admin")) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return;
        }
        
        plugin.getTradingPostManager().removeTradingPost(nearestPost.getId());
        player.sendMessage(plugin.getConfigManager().getMessage("trading-post-removed"));
    }
    
    private void handleAdd(Player player, String[] args) {
        if (!player.hasPermission("tradingpost.create")) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return;
        }
        
        double maxDistance = plugin.getConfigManager().getMaxDistance();
        TradingPost nearestPost = plugin.getTradingPostManager().getNearestTradingPost(
                player.getLocation(), maxDistance);
        
        if (nearestPost == null || !nearestPost.getOwnerId().equals(player.getUniqueId())) {
            player.sendMessage(plugin.getConfigManager().colorize(
                    "&cNo trading post found within " + maxDistance + " blocks."));
            return;
        }
        
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        
        if (itemInHand.getType() == Material.AIR) {
            player.sendMessage(plugin.getConfigManager().colorize("&cYou must hold an item to add it to the trading post."));
            return;
        }
        
        ItemStack requestedItem = null;
        if (args.length > 1 && args[1].equalsIgnoreCase("for")) {
            if (args.length >= 4) {
                try {
                    Material requestedMaterial = Material.valueOf(args[2].toUpperCase());
                    int requestedAmount = Integer.parseInt(args[3]);
                    requestedItem = new ItemStack(requestedMaterial, requestedAmount);
                } catch (IllegalArgumentException e) {
                    player.sendMessage(plugin.getConfigManager().colorize("&cInvalid material or amount."));
                    return;
                }
            }
        }
        
        TradeItem tradeItem = new TradeItem(itemInHand.clone(), requestedItem);
        plugin.getTradingPostManager().addItemToPost(nearestPost.getId(), tradeItem);
        
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        
        player.sendMessage(plugin.getConfigManager().colorize("&aItem added to trading post!"));
    }
    
    private void handleReload(Player player) {
        if (!player.hasPermission("tradingpost.reload")) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return;
        }
        
        plugin.reloadConfig();
        plugin.getConfigManager().reload();
        player.sendMessage(plugin.getConfigManager().colorize("&aConfiguration reloaded"));
    }
    
    private void handleReset(Player player, String[] args) {
        if (!player.hasPermission("tradingpost.reset")) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return;
        }
        
        if (args.length < 2) {
            player.sendMessage(plugin.getConfigManager().colorize("&cUsage: /tradingpost reset <player>"));
            return;
        }
        
        String targetName = args[1];
        org.bukkit.OfflinePlayer target = plugin.getServer().getOfflinePlayer(targetName);
        
        if (target == null || !target.hasPlayedBefore()) {
            player.sendMessage(plugin.getConfigManager().colorize("&cPlayer not found"));
            return;
        }
        
        java.util.List<TradingPost> posts = plugin.getTradingPostManager()
                .getPlayerTradingPosts(target.getUniqueId());
        
        for (TradingPost post : posts) {
            plugin.getTradingPostManager().removeTradingPost(post.getId());
        }
        
        player.sendMessage(plugin.getConfigManager().colorize(
                "&aRemoved " + posts.size() + " trading posts for " + targetName));
    }
    
    private void sendHelp(Player player) {
        player.sendMessage(plugin.getConfigManager().colorize("&6Trading Post Commands:"));
        player.sendMessage(plugin.getConfigManager().colorize("&e/tradingpost create &8- &7Create post"));
        player.sendMessage(plugin.getConfigManager().colorize("&e/tradingpost add &8- &7Add item"));
        player.sendMessage(plugin.getConfigManager().colorize("&e/tradingpost browse &8- &7Browse posts"));
        player.sendMessage(plugin.getConfigManager().colorize("&e/tradingpost list &8- &7Your posts"));
        player.sendMessage(plugin.getConfigManager().colorize("&e/tradingpost remove &8- &7Remove post"));
        if (player.hasPermission("tradingpost.admin")) {
            player.sendMessage(plugin.getConfigManager().colorize("&e/tradingpost reload &8- &7Reload config"));
            player.sendMessage(plugin.getConfigManager().colorize("&e/tradingpost reset <player> &8- &7Reset player posts"));
        }
    }
    
    private boolean hasItem(Player player, ItemStack item) {
        int count = 0;
        
        for (ItemStack invItem : player.getInventory().getContents()) {
            if (invItem != null && invItem.getType() == item.getType()) {
                count += invItem.getAmount();
                if (count >= item.getAmount()) {
                    return true;
                }
            }
        }
        
        return false;
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
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            completions.addAll(Arrays.asList("create", "add", "browse", "list", "remove", "help"));
            if (sender.hasPermission("tradingpost.admin")) {
                completions.add("reload");
                completions.add("reset");
            }
            return completions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
            completions.add("for");
            return completions;
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("reset")) {
            return null;
        }
        
        if (args.length == 3 && args[0].equalsIgnoreCase("add") && args[1].equalsIgnoreCase("for")) {
            return Arrays.stream(Material.values())
                    .filter(Material::isItem)
                    .map(m -> m.name().toLowerCase())
                    .filter(s -> s.startsWith(args[2].toLowerCase()))
                    .limit(20)
                    .collect(Collectors.toList());
        }
        
        if (args.length == 4 && args[0].equalsIgnoreCase("add") && args[1].equalsIgnoreCase("for")) {
            completions.addAll(Arrays.asList("1", "2", "4", "8", "16", "32", "64"));
            return completions;
        }
        
        return completions;
    }
}
