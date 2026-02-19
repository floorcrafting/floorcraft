package com.boyninja1555.floorcraft.blocks.lib;

import com.boyninja1555.floorcraft.Floorcraft;
import com.boyninja1555.floorcraft.lib.ErrorHandler;
import com.boyninja1555.floorcraft.texture.atlas.TextureAtlas;
import com.boyninja1555.floorcraft.world.format.WorldBlockIDs;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class BlockRegistry {
    private final List<Class<? extends Block>> blocks;

    public BlockRegistry() {
        this.blocks = new ArrayList<>();
    }

    public void register(Class<? extends Block> block) {
        blocks.add(block);
    }

    public Block get(Class<? extends Block> block) {
        if (!blocks.contains(block)) return null;
        try {
            return block.getConstructor(TextureAtlas.class).newInstance(Floorcraft.textures());
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException ex) {
            ErrorHandler.crash("Could not load " + block.getName() + " block!\n" + ex);
            return null;
        }
    }

    public Block getById(int id) {
        List<Block> results = blocks.stream().filter(b -> WorldBlockIDs.all().get(b) == id).map(this::get).toList();

        if (results.isEmpty()) return get(NoBlock.class);
        return results.getFirst();
    }

    public List<Block> getAll() {
        return blocks.stream().map(this::get).toList();
    }
}
