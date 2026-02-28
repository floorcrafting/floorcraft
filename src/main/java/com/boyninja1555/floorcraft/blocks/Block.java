package com.boyninja1555.floorcraft.blocks;

import com.boyninja1555.floorcraft.Floorcraft;
import com.boyninja1555.floorcraft.blocks.lib.StaticBlockDefinition;
import com.boyninja1555.floorcraft.blocks.scripting.BlockScripts;
import com.boyninja1555.floorcraft.lib.AssetManager;
import com.boyninja1555.floorcraft.lib.ErrorHandler;
import com.boyninja1555.floorcraft.texture.atlas.TextureAtlas;
import com.boyninja1555.floorcraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public abstract class Block {
    private static final StaticBlockDefinition EMERGENCY_FALLBACK = new StaticBlockDefinition("block.none", new Integer[][]{{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}}, true, null);
    public final @NotNull TextureAtlas atlas;
    public final @NotNull String modelData;
    private StaticBlockDefinition definitionCache;

    public Block(@NotNull TextureAtlas atlas, @NotNull String modelData) {
        this.atlas = atlas;
        this.modelData = modelData;
        this.definitionCache = null;
    }

    public Block(@NotNull TextureAtlas atlas) {
        this(atlas, "");
    }

    public StaticBlockDefinition definition() {
        if (definitionCache != null) return definitionCache;

        Path path = AssetManager.blocksPath().resolve(identifier() + ".json");

        if (!path.toFile().isFile()) return fallbackDefinition();
        try (FileReader reader = new FileReader(path.toFile())) {
            definitionCache = Floorcraft.gson.fromJson(reader, StaticBlockDefinition.class);
            if (definitionCache == null) throw new IOException("Definition is empty");

            String name = definitionCache.name() == null ? "block." + identifier() : definitionCache.name();
            definitionCache = new StaticBlockDefinition(name, definitionCache.texture(), definitionCache.transparent(), definitionCache.script());

            if (definitionCache.texture() == null) throw new IOException("Missing \"texture\" field");
            if (definitionCache.transparent() == null) throw new IOException("Missing \"transparent\" field");
        } catch (IOException ex) {
            ErrorHandler.error("Could not load the block definition of " + identifier() + "!\n" + ex);
            definitionCache = fallbackDefinition();
        }

        return definitionCache;
    }

    private StaticBlockDefinition fallbackDefinition() {
        if (Floorcraft.blockRegistry() == null) return EMERGENCY_FALLBACK;
        Block noBlock = Floorcraft.blockRegistry().get(NoBlock.class);
        if (noBlock == null || noBlock == this) return EMERGENCY_FALLBACK;
        return noBlock.definition();
    }

    public abstract @NotNull String identifier();

    public boolean cacheEnabled() {
        return true;
    }

    public void onPlace(World world, Vector3i position) {
        Block block = world.blockAt(position);
        if (!BlockScripts.hasScript(block)) return;
        BlockScripts.onPlace(block, world, position);
    }

    public void onBreak(World world, Vector3i position) {
        Block block = world.blockAt(position);
        if (!BlockScripts.hasScript(block)) return;
        BlockScripts.onBreak(block, world, position);
    }

    public void onTick(World world, Vector3i position, float deltaTime) {
        Block block = world.blockAt(position);
        if (!BlockScripts.hasScript(block)) return;
        BlockScripts.onTick(block, world, position);
    }
}
