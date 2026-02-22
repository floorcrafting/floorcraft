package com.boyninja1555.floorcraft.ui.element.lib;

import com.boyninja1555.floorcraft.Floorcraft;
import com.boyninja1555.floorcraft.blocks.lib.Block;
import com.boyninja1555.floorcraft.texture.BlockItemTexture;
import com.boyninja1555.floorcraft.texture.atlas.AtlasRegion;
import com.boyninja1555.floorcraft.texture.atlas.TextureAtlas;
import com.boyninja1555.floorcraft.ui.element.lib.base.UIElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class UIBlock extends UIElement {

    @Override
    public @NotNull TextureAtlas atlas() {
        return Floorcraft.textures();
    }

    @Override
    public @NotNull List<AtlasRegion> regions() {
        return List.of(BlockItemTexture.get(Floorcraft.blockRegistry().get(block())));
    }

    /**
     * Called every frame to determine its coordinates in the block textures atlas.
     *
     * @return Position in block texture atlas.
     */
    public abstract @NotNull Class<? extends Block> block();
}
