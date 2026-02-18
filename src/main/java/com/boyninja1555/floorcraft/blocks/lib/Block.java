package com.boyninja1555.floorcraft.blocks.lib;

import com.boyninja1555.floorcraft.texture.BlockTexture;
import com.boyninja1555.floorcraft.texture.atlas.TextureAtlas;
import org.jetbrains.annotations.NotNull;

public abstract class Block {
    public final TextureAtlas atlas;

    public Block(TextureAtlas atlas) {
        this.atlas = atlas;
    }

    public abstract @NotNull BlockTexture texture();

    public abstract boolean transparent();
}
