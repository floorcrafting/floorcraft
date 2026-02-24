package com.boyninja1555.floorcraft.ui.element.lib;

import com.boyninja1555.floorcraft.Floorcraft;
import com.boyninja1555.floorcraft.texture.atlas.AtlasRegion;
import com.boyninja1555.floorcraft.texture.atlas.TextureAtlas;
import com.boyninja1555.floorcraft.ui.element.lib.base.HUDElement;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.List;

public abstract class HUDIcon extends HUDElement {

    @Override
    public @NotNull TextureAtlas atlas() {
        return Floorcraft.uiIcons();
    }

    @Override
    public @NotNull List<AtlasRegion> regions() {
        return List.of(Floorcraft.uiIcons().region(iconRegion().x, iconRegion().y));
    }

    /**
     * Called every frame to determine its coordinates in the UI icon atlas.
     *
     * @return Position in UI icon atlas.
     */
    public abstract @NotNull Vector2i iconRegion();
}
