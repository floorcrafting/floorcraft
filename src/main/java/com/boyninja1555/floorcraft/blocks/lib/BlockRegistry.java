package com.boyninja1555.floorcraft.blocks.lib;

import com.boyninja1555.floorcraft.lib.ErrorHandler;
import com.boyninja1555.floorcraft.texture.atlas.TextureAtlas;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class BlockRegistry {
    private final TextureAtlas atlas;
    private final List<Class<? extends Block>> blocks;

    public BlockRegistry(TextureAtlas atlas) {
        this.atlas = atlas;
        blocks = new ArrayList<>();
    }

    public void register(Class<? extends Block> block) {
        blocks.add(block);
    }

    public Block get(Class<? extends Block> block) {
        if (!blocks.contains(block)) return null;
        try {
            return block.getConstructor(TextureAtlas.class).newInstance(atlas);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException ex) {
            ErrorHandler.crash("Could not load " + block.getName() + " block!\n" + ex);
            return null;
        }
    }
}
