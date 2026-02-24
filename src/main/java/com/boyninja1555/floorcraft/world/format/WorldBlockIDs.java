package com.boyninja1555.floorcraft.world.format;

import com.boyninja1555.floorcraft.blocks.*;
import com.boyninja1555.floorcraft.blocks.Block;
import com.boyninja1555.floorcraft.blocks.NoBlock;

import java.util.HashMap;
import java.util.Map;

public class WorldBlockIDs {

    public static Map<Class<? extends Block>, Integer> all() {
        Map<Class<? extends Block>, Integer> values = new HashMap<>();
        values.put(NoBlock.class, 0);
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
}
