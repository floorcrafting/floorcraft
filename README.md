<div align="center">
  <h1>Floorcraft</h1>
</div>

## Overview

**Floorcraft** is a voxel game built for developers first.

Instead of distributing it like a traditional game, you run it directly as a **Gradle project**. The focus is on:

- Modding freedom
- Easy debugging
- Full extensibility

```shell
./gradlew run
```

If you prefer distributing prebuilt binaries, that’s completely fine. Packaging scripts are included:

- `./package` (for compiling to a macOS app, which is what I originally made the game for)
- `./package-win` (for compiling to a `.msix` file)
- `./compile-assets` (for compiling default assets to the hosted `assets.zip` file from your local `assets` directory)
- `./decompile-assets` (for decompiling default assets from the hosted `assets.zip` file)

Players can override default assets in-game by pressing **`X`**, which opens the local assets directory.

The direct URL for `assets.zip` is defined in  
`com.boyninja1555.floorcraft.lib.AssetManager`  
(typically a GitHub raw link).

There is no built-in modding system in the base game.  
You mod it by cloning the repository, making changes, and running it.

If you’d like to fork the project and add a simpler modding layer, go for it.

---

## Controls

| Keybind(s)      | Description |
|-----------------|------------|
| `Escape`        | Unfocuses the game window and disables cursor lock |
| `W / A / S / D` | Move forward / left / backward / right |
| `Space`         | Move up |
| `Control`       | Move down |
| `Left Shift`    | Sprint |
| `1–9`, `0`      | Select block type |
| `Z` / `Home`    | Save the current world |
| `X`             | Open the game files in your file manager (useful for textures or world management) |

---

## Controls (Window Unfocused)

| Keybind(s) | Description |
|------------|------------|
| `Left Click` | Focus the game window and enable cursor lock |