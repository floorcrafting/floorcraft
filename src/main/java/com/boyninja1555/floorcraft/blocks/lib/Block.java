package com.boyninja1555.floorcraft.blocks.lib;

import com.boyninja1555.floorcraft.Floorcraft;
import com.boyninja1555.floorcraft.lib.AssetManager;
import com.boyninja1555.floorcraft.lib.ErrorHandler;
import com.boyninja1555.floorcraft.texture.atlas.TextureAtlas;
import org.jetbrains.annotations.NotNull;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public abstract class Block {
    public final TextureAtlas atlas;
    private StaticBlockDefinition definitionCache;

    public Block(TextureAtlas atlas) {
        this.atlas = atlas;
        this.definitionCache = null;
    }

    public StaticBlockDefinition definition() {
        if (definitionCache != null) return definitionCache;

        Path path = AssetManager.blocksPath().resolve(identifier() + ".json");

        if (!path.toFile().isFile()) return Floorcraft.blockRegistry().get(NoBlock.class).definition();
        try (FileReader reader = new FileReader(path.toFile())) {
            definitionCache = Floorcraft.gson.fromJson(reader, StaticBlockDefinition.class);

            if (definitionCache.name() == null)
                definitionCache = new StaticBlockDefinition("block." + identifier(), definitionCache.texture(), definition().transparent());

            if (definitionCache.texture() == null) throw new IOException("Missing \"texture\" field");
            if (definitionCache.transparent() == null) throw new IOException("Missing \"transparent\" field");
        } catch (IOException ex) {
            ErrorHandler.error("Could not load the block definition of " + identifier() + "!\n" + ex);
            definitionCache = Floorcraft.blockRegistry().get(NoBlock.class).definition();
        }

        return definitionCache;
    }

    public abstract @NotNull String identifier();
}
