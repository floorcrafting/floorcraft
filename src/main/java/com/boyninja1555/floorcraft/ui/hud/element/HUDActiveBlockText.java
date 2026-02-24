package com.boyninja1555.floorcraft.ui.hud.element;

import com.boyninja1555.floorcraft.Floorcraft;
import com.boyninja1555.floorcraft.ui.hud.element.lib.HUDText;
import com.boyninja1555.floorcraft.ui.hud.element.lib.base.HUDSide;
import org.jetbrains.annotations.Nullable;

public class HUDActiveBlockText extends HUDText {

    @Override
    public String text() {
        return Floorcraft.player().activeBlock().definition().name();
    }

    @Override
    public @Nullable HUDSide side() {
        return HUDSide.RIGHT;
    }

    @Override
    public float size() {
        return 16f;
    }
}
