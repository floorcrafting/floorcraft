package com.boyninja1555.floorcraft.blocks;

import com.boyninja1555.floorcraft.texture.atlas.TextureAtlas;
import org.jetbrains.annotations.NotNull;

public class DirtBlock extends Block {

    public DirtBlock(TextureAtlas atlas) {
        super(atlas);
    }

    @Override
    public @NotNull String identifier() {
        return "dirt";
    }
}
