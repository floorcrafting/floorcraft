package com.boyninja1555.floorcraft.blocks;

import com.boyninja1555.floorcraft.blocks.lib.Block;
import com.boyninja1555.floorcraft.texture.BlockTexture;
import com.boyninja1555.floorcraft.texture.atlas.AtlasRegion;
import com.boyninja1555.floorcraft.texture.atlas.TextureAtlas;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

public class DirtBlock extends Block {

    public DirtBlock(TextureAtlas atlas) {
        super(atlas);
    }

    @Override
    public @NotNull BlockTexture texture() {
        final AtlasRegion TEXTURE = atlas.region(1, 0);
        return new BlockTexture(TEXTURE, TEXTURE, TEXTURE, TEXTURE, TEXTURE, TEXTURE);
    }

    @Override
    public boolean transparent() {
        return false;
    }

    @Override
    public boolean physical() {
        return true;
    }
}
