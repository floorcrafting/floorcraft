package com.boyninja1555.floorcraft.ui.element;

import com.boyninja1555.floorcraft.Floorcraft;
import com.boyninja1555.floorcraft.blocks.lib.Block;
import com.boyninja1555.floorcraft.ui.element.lib.UIBlock;
import com.boyninja1555.floorcraft.ui.element.lib.base.UISide;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UIActiveBlock extends UIBlock {

    @Override
    public @NotNull Class<? extends Block> block() {
        return Floorcraft.player().activeBlock().getClass();
    }

    @Override
    public @Nullable UISide side() {
        return UISide.RIGHT;
    }

    @Override
    public float size() {
        return 48f;
    }
}
