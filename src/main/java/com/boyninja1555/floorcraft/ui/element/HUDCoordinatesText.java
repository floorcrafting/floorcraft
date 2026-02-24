package com.boyninja1555.floorcraft.ui.element;

import com.boyninja1555.floorcraft.Floorcraft;
import com.boyninja1555.floorcraft.ui.element.lib.HUDText;
import com.boyninja1555.floorcraft.ui.element.lib.base.HUDSide;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class HUDCoordinatesText extends HUDText {

    @Override
    public String text() {
        Vector3f position = Floorcraft.player().position();
        String x = String.valueOf((int) position.x);
        String y = String.valueOf((int) position.y);
        String z = String.valueOf((int) position.z);
        return x + " " + y + " " + z;
    }

    @Override
    public @Nullable HUDSide side() {
        return HUDSide.LEFT;
    }

    @Override
    public float size() {
        return 16f;
    }
}
