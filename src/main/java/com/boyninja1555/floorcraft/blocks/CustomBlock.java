package com.boyninja1555.floorcraft.blocks;

import com.boyninja1555.floorcraft.texture.atlas.TextureAtlas;
import org.jetbrains.annotations.NotNull;

public class CustomBlock extends Block {

    public CustomBlock(TextureAtlas atlas, String identifier, String modelData) {
        super(atlas, identifier + ";" + modelData);
    }

    @Override
    public @NotNull String identifier() {
        return modelData.split(";")[0];
    }

    @Override
    public boolean cacheEnabled() {
        return false;
    }
}
