package com.boyninja1555.floorcraft.ui.element;

import com.boyninja1555.floorcraft.lib.FpsTracker;
import com.boyninja1555.floorcraft.ui.element.lib.UIText;
import com.boyninja1555.floorcraft.ui.element.lib.base.UISide;
import org.jetbrains.annotations.Nullable;

public class UIFpsText extends UIText {

    @Override
    public String text() {
        return FpsTracker.to3digits();
    }

    @Override
    public @Nullable UISide side() {
        return UISide.LEFT;
    }

    @Override
    public float size() {
        return 24f;
    }
}
