package com.boyninja1555.floorcraft.blocks.lib;

import com.boyninja1555.floorcraft.blocks.Block;
import com.boyninja1555.floorcraft.texture.atlas.TextureAtlas;
import org.jetbrains.annotations.NotNull;

public class NoBlock extends Block {

    public NoBlock(TextureAtlas atlas) {
        super(atlas);
    }

    @Override
    public @NotNull String identifier() {
        return "none";
    }
}
