package com.boyninja1555.floorcraft.blocks;

import com.boyninja1555.floorcraft.texture.atlas.TextureAtlas;
import org.jetbrains.annotations.NotNull;

public class SeeSeeBlock extends Block {

    public SeeSeeBlock(TextureAtlas atlas) {
        super(atlas);
    }

    @Override
    public @NotNull String identifier() {
        return "see_see";
    }
}
