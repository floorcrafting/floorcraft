package com.boyninja1555.floorcraft.ui.element;

import com.boyninja1555.floorcraft.lib.FpsTracker;
import com.boyninja1555.floorcraft.ui.element.lib.HUDText;
import com.boyninja1555.floorcraft.ui.element.lib.base.HUDSide;
import org.jetbrains.annotations.Nullable;

public class HUDFpsText extends HUDText {

    @Override
    public String text() {
        return FpsTracker.to3digits();
    }

    @Override
    public @Nullable HUDSide side() {
        return HUDSide.LEFT;
    }

    @Override
    public float size() {
        return 24f;
    }
}
