# TradingPost

A Minecraft plugin that lets players set up trading posts in the world and trade items with each other through a GUI. Built for Paper/Spigot 1.21+.

---

## What it does

Players can place a trading post at their location, stock it with items, and other players nearby can browse and buy from it. Everything goes through an inventory GUI so it's pretty intuitive. Trades are validated before executing so no duplication glitches or inventory overflow issues.

---

## Commands

All commands use `/tradingpost` (not `/tp` — that's already taken by vanilla teleport).

```
/tradingpost create         create a trading post where you're standing
/tradingpost add            add the item in your hand to your post
/tradingpost remove <slot>  take an item back from your post
/tradingpost browse         open the GUI and see what's available
/tradingpost info           check your own post
/tradingpost delete         remove your post entirely
/tradingpost help           list everything
```

---

## Permissions

| Node | What it does | Default |
|---|---|---|
| `tradingpost.create` | create a post | everyone |
| `tradingpost.browse` | browse posts | everyone |
| `tradingpost.trade` | actually buy stuff | everyone |
| `tradingpost.remove` | take your items back | everyone |
| `tradingpost.delete` | delete your post | everyone |
| `tradingpost.admin` | bypass restrictions | op only |

---

## Config

```yaml
trading-post:
  max-slots: 5                # how many items each player can list
  interaction-distance: 5.0   # blocks away before you can interact
  allowed-worlds:             # remove this section to allow all worlds
    - world

trading-fee:
  enabled: true
  type: ITEM
  item-material: EMERALD
  item-amount: 1
```

To turn off fees entirely:
```yaml
trading-fee:
  enabled: false
```

---

## Building

```bash
# windows
gradlew.bat shadowJar

# linux/mac
./gradlew shadowJar
```

Drop the JAR from `build/libs/` into your plugins folder and restart. Config generates on first run.

**Requirements:** Java 21, Minecraft 1.21+

---

## Project layout

```
src/main/java/com/tradingpost/
├── TradingPostPlugin.java
├── commands/TradingPostCommand.java
├── config/ConfigManager.java
├── data/TradingPostManager.java
├── gui/
│   ├── GUIManager.java
│   ├── GUIListener.java
│   └── GUIType.java
├── model/
│   ├── TradingPost.java
│   └── TradeItem.java
└── util/TradeValidator.java
```

---

## A few notes on the trade logic

- Items are cloned before any trade happens so there's no reference weirdness
- Buyer inventory gets checked before the trade executes, not after
- Item availability is verified at trade time in case it was already sold
- Empty posts get cleaned up automatically
- Distance is re-checked server-side so you can't interact from far away

---

## Known issues / things to be aware of

- Data is stored in YAML so it'll get slow if someone has thousands of trades. Good enough for most servers though
- If the server crashes mid-trade the item could theoretically get lost — hasn't happened in testing but worth knowing
- No economy plugin support yet, fees are item-based only for now

---

## Troubleshooting

**Plugin won't load** — you need Java 21. Check your startup flags.

**Commands doing nothing** — check permissions, the defaults should work but double check your permission plugin config.

**Items not saving** — the server process needs write access to the plugins folder, check your file permissions.

**GUI won't open** — player needs `tradingpost.browse`, make sure it's not being denied somewhere.

---

*by [ranawise](https://github.com/ranawise)*
