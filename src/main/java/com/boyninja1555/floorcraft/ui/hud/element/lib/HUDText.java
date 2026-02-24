package com.boyninja1555.floorcraft.ui.hud.element.lib;

import com.boyninja1555.floorcraft.Floorcraft;
import com.boyninja1555.floorcraft.texture.atlas.AtlasRegion;
import com.boyninja1555.floorcraft.texture.atlas.TextureAtlas;
import com.boyninja1555.floorcraft.ui.hud.element.lib.base.HUDElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class HUDText extends HUDElement {

    @Override
    public @NotNull TextureAtlas atlas() {
        return Floorcraft.font().atlas;
    }

    @Override
    public @NotNull List<AtlasRegion> regions() {
        return Floorcraft.font().string(text());
    }

    /**
     * Called every frame to determine its text value.
     *
     * @return Text to display at this position.
     */
    public abstract String text();
}
