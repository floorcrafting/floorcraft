package com.boyninja1555.floorcraft.blocks;

import com.boyninja1555.floorcraft.blocks.lib.Block;
import com.boyninja1555.floorcraft.texture.atlas.TextureAtlas;
import org.jetbrains.annotations.NotNull;

public class DisturbedHeadBlock extends Block {

    public DisturbedHeadBlock(TextureAtlas atlas) {
        super(atlas);
    }

    @Override
    public @NotNull String identifier() {
        return "disturbed_head";
    }
}
