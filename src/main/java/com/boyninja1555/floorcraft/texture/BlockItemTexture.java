package com.boyninja1555.floorcraft.texture;

import com.boyninja1555.floorcraft.Floorcraft;
import com.boyninja1555.floorcraft.blocks.Block;
import com.boyninja1555.floorcraft.texture.atlas.AtlasRegion;

public class BlockItemTexture {
    private static final int FACE = 2;

    public static AtlasRegion get(Block block) {
        Integer[] xy = block.definition().texture()[FACE];
        return Floorcraft.textures().region(xy[0], xy[1]);
    }
}
