package com.boyninja1555.floorcraft.ui.element;

import com.boyninja1555.floorcraft.ui.element.lib.UIIcon;
import com.boyninja1555.floorcraft.ui.element.lib.base.UISide;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.Map;

public class UICrosshair extends UIIcon {

    @Override
    public @NotNull Vector2i iconRegion() {
        return new Vector2i(0, 0);
    }

    @Override
    public @Nullable UISide side() {
        return null;
    }

    @Override
    public float size() {
        return 24f;
    }

    @Override
    public Vector2f position(Vector2i windowSize, Map<UISide, Float> existingCountOnSides, float padding) {
        return new Vector2f(windowSize.x / 2f - size() / 2f, windowSize.y / 2f - size() / 2f);
    }
}
