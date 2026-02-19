package com.boyninja1555.floorcraft.blocks;

import com.boyninja1555.floorcraft.blocks.lib.Block;
import com.boyninja1555.floorcraft.texture.atlas.TextureAtlas;
import org.jetbrains.annotations.NotNull;

public class LemonBlock extends Block {

    public LemonBlock(TextureAtlas atlas) {
        super(atlas);
    }

    @Override
    public @NotNull String identifier() {
        return "lemon";
    }
}
