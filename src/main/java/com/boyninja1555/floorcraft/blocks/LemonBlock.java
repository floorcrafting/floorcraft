package com.boyninja1555.floorcraft.blocks;

import com.boyninja1555.floorcraft.blocks.lib.Block;
import com.boyninja1555.floorcraft.texture.BlockTexture;
import com.boyninja1555.floorcraft.texture.atlas.AtlasRegion;
import com.boyninja1555.floorcraft.texture.atlas.TextureAtlas;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

public class LemonBlock extends Block {

    public LemonBlock(TextureAtlas atlas) {
        super(atlas);
    }

    @Override
    public @NotNull BlockTexture texture() {
        final AtlasRegion TEXTURE = atlas.region(3, 0);
        final AtlasRegion TOP = atlas.region(4, 0);
        return new BlockTexture(TOP, TEXTURE, TEXTURE, TEXTURE, TEXTURE, TEXTURE);
    }

    @Override
    public boolean transparent() {
        return false;
    }
}
