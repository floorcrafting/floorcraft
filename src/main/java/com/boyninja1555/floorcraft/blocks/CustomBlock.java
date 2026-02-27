package com.boyninja1555.floorcraft.blocks;

import com.boyninja1555.floorcraft.texture.atlas.TextureAtlas;
import org.jetbrains.annotations.NotNull;

public class CustomBlock extends Block {
    private final String identifier;

    public CustomBlock(TextureAtlas atlas, String identifier) {
        super(atlas);
        this.identifier = identifier;
    }

    @Override
    public @NotNull String identifier() {
        return identifier;
    }
}
