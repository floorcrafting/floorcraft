package com.boyninja1555.floorcraft.blocks.lib;

import com.boyninja1555.floorcraft.Floorcraft;
import com.boyninja1555.floorcraft.blocks.Block;
import com.boyninja1555.floorcraft.lib.ErrorHandler;
import com.boyninja1555.floorcraft.texture.atlas.TextureAtlas;
import com.boyninja1555.floorcraft.world.format.WorldBlockIDs;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockRegistry {
    private final List<Class<? extends Block>> blocks;
    private final Map<Class<? extends Block>, Block> getterCache;

    public BlockRegistry() {
        blocks = new ArrayList<>();
        getterCache = new HashMap<>();
    }

    public void register(Class<? extends Block> block) {
        blocks.add(block);
    }

    public Block get(Class<? extends Block> blockClass) {
        if (getterCache.containsKey(blockClass)) return getterCache.get(blockClass);
        if (!blocks.contains(blockClass)) return null;
        try {
            Block block = blockClass.getConstructor(TextureAtlas.class).newInstance(Floorcraft.textures());
            if (block.cacheEnabled()) getterCache.put(blockClass, block);
            return block;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException ex) {
            ErrorHandler.crash("Could not load " + blockClass.getName() + " block!\n" + ex);
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
