package com.boyninja1555.floorcraft.ui.hud.element;

import com.boyninja1555.floorcraft.Floorcraft;
import com.boyninja1555.floorcraft.blocks.Block;
import com.boyninja1555.floorcraft.ui.hud.element.lib.HUDBlock;
import com.boyninja1555.floorcraft.ui.hud.element.lib.base.HUDSide;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HUDActiveBlock extends HUDBlock {

    @Override
    public @NotNull Block block() {
        return Floorcraft.player().activeBlock();
    }

    @Override
    public @Nullable HUDSide side() {
        return HUDSide.RIGHT;
    }

    @Override
    public float size() {
        return 48f;
    }
}
