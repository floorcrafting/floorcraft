package com.boyninja1555.floorcraft.world.format;

import com.boyninja1555.floorcraft.Floorcraft;
import com.boyninja1555.floorcraft.blocks.*;

import java.util.HashMap;
import java.util.Map;

public class WorldBlockIDs {
    private static final Map<Integer, Class<? extends Block>> reversedCache = new HashMap<>();

    public static Map<Class<? extends Block>, Integer> all() {
        Map<Class<? extends Block>, Integer> values = new HashMap<>();
        values.put(NoBlock.class, -1);
        values.put(StoneBlock.class, 1);
        values.put(DirtBlock.class, 2);
        values.put(GlassBlock.class, 3);
        values.put(LemonBlock.class, 4);
        values.put(HeartBlock.class, 5);
        values.put(SeeSeeBlock.class, 6);
        values.put(SkinnedBlock.class, 7);
        values.put(AgonyBlock.class, 8);
        values.put(PreservedDeityHeadBlock.class, 9);
        values.put(DisturbedHeadBlock.class, 10);
        values.put(CustomBlock.class, 11);
        return values;
    }

    public static int idFromBlock(Class<? extends Block> block) {
        return all().get(block);
    }

    public static int idFromBlock(Block block) {
        return all().get(block.getClass());
    }

    public static Class<? extends Block> blockClassFromId(int id) {
        if (reversedCache.containsKey(id)) return reversedCache.get(id);
        for (Map.Entry<Class<? extends Block>, Integer> entry : all().entrySet()) {
            Class<? extends Block> key = entry.getKey();
            int value = entry.getValue();
            if (value != id) continue;

            reversedCache.put(value, key);
            return key;
        }

        reversedCache.put(id, null);
        return null;
    }

    public static Block blockFromId(int id) {
        Class<? extends Block> blockClass = blockClassFromId(id);

        if (blockClass == null) return null;
        return Floorcraft.blockRegistry().get(blockClass);
    }
}
