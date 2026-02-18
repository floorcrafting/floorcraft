package com.boyninja1555.floorcraft.texture;

import com.boyninja1555.floorcraft.texture.atlas.AtlasRegion;

public class BlockTexture extends Texture {

    public BlockTexture(AtlasRegion top, AtlasRegion bottom, AtlasRegion front, AtlasRegion back, AtlasRegion left, AtlasRegion right) {
        super(top, bottom, front, back, left, right);
    }
}
