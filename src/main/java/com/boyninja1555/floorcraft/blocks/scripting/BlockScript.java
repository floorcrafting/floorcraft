package com.boyninja1555.floorcraft.blocks.scripting;

import org.jetbrains.annotations.NotNull;

public class BlockScript {
    private final @NotNull String name;
    private @NotNull BlockUpdateCallback onPlace;
    private @NotNull BlockUpdateCallback onBreak;
    private @NotNull BlockUpdateCallback onTick;

    public @NotNull String name() {
        return name;
    }

    public @NotNull BlockUpdateCallback onPlace() {
        return onPlace;
    }

    public void onPlace(@NotNull BlockUpdateCallback onPlace) {
        this.onPlace = onPlace;
    }

    public @NotNull BlockUpdateCallback onBreak() {
        return onBreak;
    }

    public void onBreak(@NotNull BlockUpdateCallback onBreak) {
        this.onBreak = onBreak;
    }

    public @NotNull BlockUpdateCallback onTick() {
        return onTick;
    }

    public void onTick(@NotNull BlockUpdateCallback onTick) {
        this.onTick = onTick;
    }

    public BlockScript(@NotNull String name, @NotNull BlockUpdateCallback onPlace, @NotNull BlockUpdateCallback onBreak, @NotNull BlockUpdateCallback onTick) {
        this.name = name;
        this.onPlace = onPlace;
        this.onBreak = onBreak;
        this.onTick = onTick;
    }

    public BlockScript(@NotNull String name) {
        this(name, (ignored, ignored1) -> {
        }, (ignored, ignored1) -> {
        }, (ignored, ignored1) -> {
        });
    }

    public boolean init(String scriptContent) {
        return true;
    }
}
