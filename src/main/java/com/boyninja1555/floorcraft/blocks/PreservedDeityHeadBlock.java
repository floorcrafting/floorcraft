package com.boyninja1555.floorcraft.blocks;

import com.boyninja1555.floorcraft.texture.atlas.TextureAtlas;
import org.jetbrains.annotations.NotNull;

public class PreservedDeityHeadBlock extends Block {

    public PreservedDeityHeadBlock(TextureAtlas atlas) {
        super(atlas);
    }

    @Override
    public @NotNull String identifier() {
        return "preserved_deity_head";
    }
}
