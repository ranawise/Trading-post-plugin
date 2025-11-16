# TradingPost Plugin - Setup Guide

## Quick Start

### 1. Build the Plugin

```bash
# Windows
gradlew.bat shadowJar

# Linux/Mac
./gradlew shadowJar
```

The compiled JAR will be located at: `build/libs/TradingPost-1.0.0.jar`

### 2. Install on Server

1. Copy `TradingPost-1.0.0.jar` to your server's `plugins` folder
2. Start or restart your server
3. The plugin will generate default configuration files

### 3. Configure (Optional)

Edit `plugins/TradingPost/config.yml` to customize:
- Trading fees
- Maximum slots per player
- Distance restrictions
- Messages and GUI titles

### 4. Test the Plugin

In-game commands to test:
```
/tp help              # View all commands
/tp create            # Create a trading post
/tp add               # Add item in hand to trading post
/tp browse            # Browse all trading posts
```

## Development Setup

### Prerequisites
- Java 21 JDK
- Gradle 8.x (included via wrapper)
- Minecraft 1.21 Paper/Spigot server

### IDE Setup

**IntelliJ IDEA:**
1. Open the project folder
2. IDEA will automatically detect Gradle
3. Wait for dependencies to download
4. Run `shadowJar` task to build

**Eclipse:**
1. Import as Gradle project
2. Wait for dependencies
3. Run Gradle build task

### Project Structure

```
src/main/java/com/tradingpost/
├── TradingPostPlugin.java          # Main plugin class
├── commands/
│   └── TradingPostCommand.java     # Command handler
├── config/
│   └── ConfigManager.java          # Configuration management
├── data/
│   └── TradingPostManager.java     # Trading post data management
├── gui/
│   ├── GUIManager.java             # GUI creation
│   ├── GUIListener.java            # GUI event handling
│   └── GUIType.java                # GUI type enum
├── model/
│   ├── TradingPost.java            # Trading post model
│   └── TradeItem.java              # Trade item model
└── util/
    └── TradeValidator.java         # Trade validation logic

src/main/resources/
├── plugin.yml                       # Plugin metadata
└── config.yml                       # Default configuration
```

## Features Implemented

✅ Trading post creation with location validation  
✅ Configurable trading fees (item-based)  
✅ Maximum slots per player restriction  
✅ Distance-based interaction  
✅ GUI for browsing trading posts  
✅ GUI for viewing items  
✅ Trade confirmation GUI  
✅ Secure trade execution  
✅ Item duplication prevention  
✅ Inventory validation  
✅ Data persistence (YAML storage)  
✅ Permission system  
✅ Tab completion  
✅ Configurable messages  
✅ World restrictions  

## Security Features

- **Item Cloning**: All items are cloned to prevent reference manipulation
- **Validation Checks**: Inventory space and item availability verified before trades
- **Permission Checks**: All actions require appropriate permissions
- **Distance Verification**: Players must be within configured distance
- **Ownership Verification**: Only owners can modify their trading posts

## Configuration Examples

### Free Trading (No Fees)
```yaml
trading-fee:
  enabled: false
```

### Custom Item Fee
```yaml
trading-fee:
  enabled: true
  type: 'ITEM'
  item-material: 'EMERALD'
  item-amount: 5
```

### Restrict to Specific Worlds
```yaml
trading-post:
  allowed-worlds:
    - world
    - world_nether
```

### Increase Trading Slots
```yaml
max-trading-slots: 10
```

## Testing Checklist

- [ ] Plugin loads without errors
- [ ] Commands are registered
- [ ] Trading post creation works
- [ ] Fee system functions correctly
- [ ] Items can be added to trading posts
- [ ] Browse GUI displays trading posts
- [ ] Items GUI shows available items
- [ ] Confirm GUI displays trade details
- [ ] Trades execute successfully
- [ ] Items are removed after trade
- [ ] Empty trading posts are removed
- [ ] Data persists after server restart
- [ ] Permissions work correctly
- [ ] Distance restrictions work
- [ ] World restrictions work (if configured)

## Common Issues

**Issue**: Plugin doesn't load  
**Solution**: Ensure you're using Java 21 and Minecraft 1.21+

**Issue**: Commands don't work  
**Solution**: Check permissions are set correctly

**Issue**: Trading posts not saving  
**Solution**: Check file permissions on the plugin folder

**Issue**: GUI not opening  
**Solution**: Ensure player has `tradingpost.browse` permission

## Support

For issues or questions, refer to the main README.md file.
