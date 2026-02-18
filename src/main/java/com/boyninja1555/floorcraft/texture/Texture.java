package com.boyninja1555.floorcraft.texture;

import com.boyninja1555.floorcraft.texture.atlas.AtlasRegion;

import java.util.ArrayList;
import java.util.List;

public abstract class Texture {
    private final List<AtlasRegion> sides;

    public Texture(AtlasRegion... sides) {
        this.sides = new ArrayList<>(List.of(sides));
    }

    public List<AtlasRegion> sides() {
        return sides;
    }
}
