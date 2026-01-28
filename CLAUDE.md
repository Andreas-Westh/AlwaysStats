# CLAUDE.md - AI Assistant Guide for AlwaysStats

## Project Overview

**AlwaysStats** is a lightweight Minecraft Fabric mod that displays a customizable HUD overlay showing various gameplay statistics. The mod keeps important statistics always visible on the player's screen.

- **Mod ID:** `alwaysstats`
- **Version:** 1.0.0 (heavily work in progress)
- **License:** LGPL-3.0-or-later
- **Author:** westh
- **Repository:** https://github.com/Andreas-Westh/AlwaysStats

## Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| Language | Java | 21 |
| Minecraft | Fabric | 1.21.11 |
| Mod Loader | Fabric Loader | 0.18.2+ |
| Build System | Gradle + Fabric Loom | 1.14-SNAPSHOT |
| Config UI | ClothConfig2 | 21.11.151 |
| Mod Menu | ModMenu | 17.0.0-beta.2 |
| Mappings | Official Mojang Mappings | - |

## Project Structure

```
AlwaysStats/
├── src/
│   ├── client/
│   │   ├── java/com/westh/alwaysstats/
│   │   │   ├── AlwaysStatsClient.java      # Entry point - client initializer
│   │   │   ├── config/
│   │   │   │   ├── StatsConfig.java        # Main config (AutoConfig/GSON)
│   │   │   │   ├── FontSize.java           # Font scaling enum (SMALL/MEDIUM/LARGE)
│   │   │   │   ├── ScreenCorner.java       # HUD position enum (4 corners)
│   │   │   │   └── ModMenuIntegration.java # ModMenu API implementation
│   │   │   ├── render/
│   │   │   │   └── StatsRenderer.java      # Central rendering logic
│   │   │   ├── stats/
│   │   │   │   ├── StatProvider.java       # Interface for all stats
│   │   │   │   ├── FpsStat.java            # FPS display
│   │   │   │   ├── BiomeStat.java          # Biome + temperature (color-coded)
│   │   │   │   ├── CoordStat.java          # Player coordinates
│   │   │   │   ├── DirectionStat.java      # Cardinal direction + optional yaw
│   │   │   │   ├── LightLevelStat.java     # Block light level
│   │   │   │   ├── TargetStat.java         # What player is looking at
│   │   │   │   └── TimeOfDayStat.java      # Game time + sleep indicator
│   │   │   └── mixin/client/
│   │   │       └── HudMixin.java           # Injects into Gui.render()
│   │   └── resources/
│   │       └── alwaysstats.client.mixins.json
│   └── main/resources/
│       ├── fabric.mod.json                 # Mod metadata
│       └── assets/alwaysstats/icon.png     # Mod icon
├── gradle.properties                       # Version configuration
├── build.gradle                            # Build configuration
└── settings.gradle                         # Repository configuration
```

## Build Commands

```bash
# Build the mod
./gradlew build

# Output: build/libs/alwaysstats-1.0.0.jar
```

## Architecture & Design Patterns

### 1. StatProvider Interface (Strategy Pattern)

All stats implement `StatProvider` interface (`src/client/java/com/westh/alwaysstats/stats/StatProvider.java`):

```java
public interface StatProvider {
    String getConfigKey();       // Unique key for config (e.g., "fps", "biome")
    String getConfigName();      // Display name (e.g., "FPS", "Biome")
    default String getDisplayText(Minecraft client) { ... }
    default Component getDisplayComponent(Minecraft client) { ... }
}
```

**To add a new stat:**
1. Create a new class implementing `StatProvider` in `stats/`
2. Register it in `StatsRenderer.ALL_STATS` list
3. Add default enabled state in `StatsConfig.validatePostLoad()` if desired

### 2. Registry Pattern (StatsRenderer)

`StatsRenderer` maintains a static list of all available stats:
```java
private static final List<StatProvider> ALL_STATS = List.of(
    new FpsStat(),
    new BiomeStat(),
    // ... add new stats here
);
```

### 3. Mixin Injection

`HudMixin` injects at `@At("TAIL")` of `Gui.render()` to append stats rendering:
- Only renders when debug overlay (F3) is hidden
- Validates player/level existence before rendering

### 4. Configuration System

Uses AutoConfig with GSON serialization:
- Config file: `config/alwaysstats.json` (created at runtime)
- Access via `StatsConfig.get()` and `StatsConfig.save()`

## Code Conventions

### Naming Conventions
- **Packages:** `com.westh.alwaysstats.{config,render,stats,mixin}`
- **Classes:** `PascalCase` (e.g., `BiomeStat`, `StatsRenderer`)
- **Methods/Fields:** `camelCase` (e.g., `getConfigKey`, `enabledStats`)
- **Constants:** `UPPER_SNAKE_CASE` (e.g., `BASE_LINE_HEIGHT`, `COLOR_HOT`)
- **Enums:** `PascalCase` for type, `UPPER_SNAKE_CASE` for values

### Config Keys
Each stat has a unique config key matching its type:
- `"fps"` - FpsStat
- `"biome"` - BiomeStat
- `"coords"` - CoordStat
- `"direction"` - DirectionStat
- `"lightLevel"` - LightLevelStat
- `"target"` - TargetStat
- `"timeOfDay"` - TimeOfDayStat

### Rendering Constants
```java
BASE_LINE_HEIGHT = 11    // Base pixel height per line
PADDING = 2              // Internal box padding
MARGIN = 5               // Screen edge margin
BACKGROUND_COLOR = 0x90000000  // Semi-transparent black
TEXT_COLOR = 0xFFFFFFFF        // White
```

### Color Coding (BiomeStat)
- `0xFF5555` (Red) - Hot biomes (temp >= 1.0)
- `0x55FFFF` (Cyan) - Cold biomes (temp < 0.15)

## Important Implementation Details

### Biome Temperature Calculation
Temperature decreases with altitude above Y=64:
```java
float heightAdjustment = Math.max(0, pos.getY() - 64) * 0.00166667f;
float temperature = baseTemp - heightAdjustment;
```

### Time of Day Conversion
Minecraft ticks to 24-hour format (0 ticks = 6:00 AM):
- Sleep indicator `*` shown when ticks are between 12542-23460

### Entity Variant Detection
`TargetStat` handles variant detection for: Cow, Cat, Fox, Frog, Axolotl, Parrot, Rabbit, MushroomCow, Sheep, Villager

### Font Scaling
Uses Minecraft 1.21+ JOML Matrix API:
```java
guiGraphics.pose().pushMatrix();
guiGraphics.pose().translate(x, y);
guiGraphics.pose().scale(scale, scale);
// ... render text
guiGraphics.pose().popMatrix();
```

## Dependencies

**Required Runtime:**
- Fabric Loader >= 0.18.2
- Minecraft ~1.21.11
- Java >= 21

**Bundled:**
- ClothConfig2 (config UI)
- ModMenu (settings integration)

## CI/CD

GitHub Actions workflow (`.github/workflows/build.yml`):
- Triggers on push and pull requests
- Runs on Ubuntu 24.04 with Java 21
- Uploads build artifacts

## Key Files for Common Tasks

| Task | Files to Modify |
|------|-----------------|
| Add new stat | `stats/NewStat.java`, `render/StatsRenderer.java` |
| Modify config options | `config/StatsConfig.java` |
| Change HUD rendering | `render/StatsRenderer.java` |
| Modify injection point | `mixin/client/HudMixin.java` |
| Update mod metadata | `src/main/resources/fabric.mod.json` |
| Change versions | `gradle.properties` |

## Testing

No automated tests currently. Manual testing requires:
1. Build the mod: `./gradlew build`
2. Copy JAR to Minecraft mods folder
3. Launch Minecraft with Fabric Loader
4. Verify HUD displays correctly in-game
5. Test configuration through ModMenu

## Common Gotchas

1. **Split Source Sets:** This project uses Fabric Loom's `splitEnvironmentSourceSets()` - client code goes in `src/client/`, not `src/main/`

2. **Mixin Registration:** New mixins must be registered in `alwaysstats.client.mixins.json`

3. **Config Persistence:** Call `StatsConfig.save()` after modifying config programmatically

4. **Null Safety:** Always check `client.level` and `client.player` before accessing game state (see `HudMixin`)

5. **Debug Overlay Conflict:** Stats HUD hides when F3 debug screen is active

## Maven Repositories

```
https://maven.fabricmc.net/          # Fabric
https://maven.shedaniel.me/          # ClothConfig
https://maven.terraformersmc.com/    # ModMenu
```
