package com.boyninja1555.floorcraft.ui.element;

import com.boyninja1555.floorcraft.Floorcraft;
import com.boyninja1555.floorcraft.ui.element.lib.UIText;
import com.boyninja1555.floorcraft.ui.element.lib.base.UISide;
import org.jetbrains.annotations.Nullable;

public class UIActiveBlockText extends UIText {

    @Override
    public String text() {
        return Floorcraft.player().activeBlock().definition().name();
    }

    @Override
    public @Nullable UISide side() {
        return UISide.RIGHT;
    }

    @Override
    public float size() {
        return 16f;
    }
}
