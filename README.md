# AlwaysStats

A simple & lightweight customizable HUD mod to always display the stats you want, no need to hold F3.

[Modrinth page](https://modrinth.com/mod/alwaysstats) can be found here, or check the [issues](https://github.com/Andreas-Westh/AlwaysStats/issues).

![Ingame Example](https://cdn.modrinth.com/data/GCBmVH41/images/a465a7c4602483afb91f9bfd1ed382bfc3b12102_350.webp)

Made because I just wanted to always see some specific stats. This is my first mod, so feedback is very welcome! Heavily work in progress, slowly working on customizability.

## Features

Pick and choose which stats you want on screen. Currently available:

* FPS, current frames per second
* Biome, with color coded temperature (hot/cold)
* Coordinates, player X / Y / Z
* Direction, cardinal direction with optional yaw
* Light Level, block light at the player's position
* Target, what block or entity you're looking at (with variant detection for many mobs)
* Time of Day, in game clock with a sleep indicator

## Configuration

Open the settings via [Mod Menu](https://modrinth.com/mod/modmenu), powered by [Cloth Config](https://modrinth.com/mod/cloth-config). You can toggle individual stats, change the font size (Small / Medium / Large), and pick which screen corner the HUD anchors to.

![Stats settings overview](https://cdn.modrinth.com/data/GCBmVH41/images/90156acee6803dcd06b952dd65b972589d713857_350.webp)

![Customizable ingame stats example](https://cdn.modrinth.com/data/GCBmVH41/images/e39988bee7bc94539138725159c33080328cb76c_350.webp)

## Requirements

| | |
|---|---|
| Minecraft | 1.21.11 |
| Loader | Fabric (>= 0.18.2) |
| Java | 21 |
| Side | Client only |

Mod Menu and Cloth Config are bundled.

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.11.
2. Download the latest jar from [Modrinth](https://modrinth.com/mod/alwaysstats), or [GitHub Releases](https://github.com/Andreas-Westh/AlwaysStats/releases).
3. Drop it into your `mods/` folder.
4. Launch the game and configure via Mod Menu.

## Building from source

```bash
./gradlew build
```

The packaged jar lands in `build/libs/`.

## License

LGPL-3.0-or-later.
