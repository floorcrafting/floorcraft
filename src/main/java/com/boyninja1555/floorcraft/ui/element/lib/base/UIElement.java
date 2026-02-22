package com.boyninja1555.floorcraft.ui.element.lib.base;

import com.boyninja1555.floorcraft.texture.atlas.AtlasRegion;
import com.boyninja1555.floorcraft.texture.atlas.TextureAtlas;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.List;
import java.util.Map;

public abstract class UIElement {

    /**
     * The side this element will appear on.
     *
     * @return Instance of UISide (enum) or null if you override the position() method.
     */
    public abstract @Nullable UISide side();

    /**
     * The texture atlas to pull regions from during rendering.
     *
     * @return Instance of TextureAtlas.
     */
    public abstract @NotNull TextureAtlas atlas();

    /**
     * Defines the regions put side-by-side with padding in between during rendering.
     *
     * @return List of AtlasRegion.
     */
    public abstract @NotNull List<AtlasRegion> regions();

    /**
     * The scale/size of this element.
     */
    public abstract float size();

    /**
     * Override this if you did not specify a side, or it will render at (0,0).
     */
    public Vector2f position(Vector2i windowSize, Map<UISide, Float> existingOffsetOnSides, float padding) {
        UISide side = side();

        if (side == null) return new Vector2f(0f, 0f);
        float size = size();
        float yOffset = existingOffsetOnSides.getOrDefault(side, padding);
        if (side == UISide.LEFT) return new Vector2f(padding, yOffset);
        else {
            float totalWidth = (regions().size() * size) + ((regions().size() - 1) * padding);
            float x = windowSize.x - totalWidth - padding;
            return new Vector2f(x, yOffset);
        }
    }
}
