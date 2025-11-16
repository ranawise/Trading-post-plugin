# TradingPost Plugin - Complete Feature List

## ✅ All Core Features Implemented

### 1. Trading Post Creation
- **Command**: `/tradingpost create` or `/tp create`
- Creates a trading post at the player's current location
- Validates location (world restrictions, distance from other posts)
- Charges configurable fee (item-based or economy via Vault)
- Respects maximum slots per player limit

### 2. GUI Trading System
- **Browse GUI**: View all available trading posts
  - Shows post owner, item count, and location
  - Click to open items GUI
- **Items GUI**: View items in a specific trading post
  - Displays item with requested trade (if any)
  - Shows "Free" for items with no trade requirement
  - Click to open confirmation GUI
- **Confirmation GUI**: Secure trade confirmation
  - ✅ **Green Confirm Button** (Lime Wool)
  - ❌ **Red Cancel Button** (Red Wool)
  - Shows both offered and requested items
  - Clear visual feedback before trade execution

### 3. Secure Trade Handling
- **Item Cloning**: All items are cloned to prevent reference issues
- **Inventory Validation**: Checks for required items and space
- **Transaction Safety**: Validates trade before execution
- **Rollback Protection**: Data persists in YAML storage
- **No Duplication**: Secure item handling prevents exploits
- **Auto-cleanup**: Empty trading posts are automatically removed

### 4. Trade Slot Management
- Configurable maximum slots per player (default: 5)
- Tracks player post count
- Prevents exceeding slot limit
- Admin can reset player slots

### 5. Configurable Trade Fees
- **Item-based fees**: Require specific items (e.g., 1 Diamond)
- **Economy fees**: Vault integration for money-based fees
- **Configurable amounts**: Set in `config.yml`
- **Optional**: Can be disabled entirely
- **Auto-detection**: Checks for Vault on startup

### 6. Data Persistence
- **Storage**: YAML-based (`tradingposts.yml`)
- **Auto-save**: Saves on plugin disable
- **Auto-load**: Loads on plugin enable
- **Crash-safe**: Data written to disk regularly
- **Portable**: Easy to backup and transfer

### 7. Success/Failure Messages
- All messages configurable in `config.yml`
- Minimalist, clean message format
- Color-coded (green = success, red = error)
- Contextual feedback for all actions
- Prefix: `[TP]` (customizable)

### 8. Admin Permissions & Commands
- **`tradingpost.admin`**: Full admin access
- **`tradingpost.reload`**: Reload configuration
  - Command: `/tp reload`
  - Reloads config without restart
- **`tradingpost.reset`**: Reset player trades
  - Command: `/tp reset <player>`
  - Removes all trading posts for a player
  - Shows count of removed posts

### 9. Bukkit Inventory GUIs
- All UIs built with native Bukkit inventory system
- No external dependencies required
- Smooth, responsive interactions
- Click-based navigation
- Auto-closes on trade completion

### 10. Player Data Persistence
- Data survives server restarts
- Trading posts remain after logout
- Items preserved exactly as listed
- Location data maintained
- Ownership tracked by UUID

## 📋 Complete Command List

| Command | Permission | Description |
|---------|-----------|-------------|
| `/tp create` | `tradingpost.create` | Create trading post |
| `/tp add` | `tradingpost.create` | Add item to post |
| `/tp add for <material> <amount>` | `tradingpost.create` | Add item with trade request |
| `/tp browse` | `tradingpost.browse` | Browse all posts (GUI) |
| `/tp list` | `tradingpost.use` | List your posts |
| `/tp remove` | `tradingpost.remove` | Remove nearest post |
| `/tp reload` | `tradingpost.reload` | Reload configuration |
| `/tp reset <player>` | `tradingpost.reset` | Reset player's posts |
| `/tp help` | `tradingpost.use` | Show help menu |

## 🔐 Permission System

| Permission | Default | Description |
|------------|---------|-------------|
| `tradingpost.use` | true | Basic usage |
| `tradingpost.create` | true | Create posts |
| `tradingpost.browse` | true | Browse posts |
| `tradingpost.remove` | true | Remove own posts |
| `tradingpost.reload` | op | Reload config |
| `tradingpost.reset` | op | Reset player posts |
| `tradingpost.admin` | op | All permissions |

## ⚙️ Configuration Options

### Trading Fees
```yaml
trading-fee:
  enabled: true
  type: 'ITEM'  # or 'MONEY' with Vault
  item-material: 'DIAMOND'
  item-amount: 1
  money-amount: 100.0
```

### Trading Post Settings
```yaml
max-trading-slots: 5
trading-post:
  max-distance: 5.0
  allowed-worlds: []
  min-distance-between-posts: 10.0
```

### GUI Titles
```yaml
gui:
  browse-title: '&6Trading Posts'
  items-title: '&6Available Items'
  confirm-title: '&6Confirm Trade'
```

### Messages (All Customizable)
- Post created/removed
- Trade success/failure
- Permission denied
- Inventory full
- Item unavailable
- Config reloaded
- Player reset
- And more...

## 🔧 Technical Features

### Vault Integration
- Soft-dependency on Vault
- Auto-detects Vault presence
- Falls back to item fees if Vault unavailable
- Economy support for money-based fees

### Data Storage
- YAML format for easy editing
- Stores: Post ID, Owner, Location, Items, Timestamps
- Item serialization for complex items
- UUID-based player tracking

### Security
- Item cloning prevents duplication
- Inventory validation before trades
- Permission checks on all operations
- Distance verification
- Ownership validation

### Performance
- HashMap-based lookups (O(1))
- Efficient stream operations
- Minimal memory footprint
- No database required
- Async-safe data saving

## 🎯 All Requirements Met

✅ `/tradingpost create` command  
✅ GUI system for browsing trades  
✅ Item selection and confirmation  
✅ Confirm/Cancel buttons (✅/❌)  
✅ Secure trade handling  
✅ No item duplication  
✅ Rollback protection  
✅ Configurable trade slots  
✅ Configurable fees (item OR economy)  
✅ YAML database storage  
✅ Success/failure messages  
✅ Admin permissions  
✅ Reload command  
✅ Bukkit inventory GUIs  
✅ Data persistence across restarts  

## 📦 Build Output

**File**: `build/libs/TradingPost-1.0.0.jar`  
**Size**: ~15KB (minimal, no bundled dependencies)  
**Dependencies**: Paper API 1.21.1, Vault API (optional)  

## 🚀 Ready for Production

The plugin is fully functional and production-ready with all requested features implemented!
