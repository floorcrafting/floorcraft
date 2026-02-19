package com.boyninja1555.floorcraft.world.format;

import com.boyninja1555.floorcraft.blocks.DirtBlock;
import com.boyninja1555.floorcraft.blocks.GlassBlock;
import com.boyninja1555.floorcraft.blocks.LemonBlock;
import com.boyninja1555.floorcraft.blocks.StoneBlock;
import com.boyninja1555.floorcraft.blocks.lib.Block;
import com.boyninja1555.floorcraft.blocks.lib.NoBlock;

import java.util.Map;

public class WorldBlockIDs {

    public static Map<Class<? extends Block>, Integer> all() {
        return Map.of(
                NoBlock.class, 0,
                StoneBlock.class, 1,
                DirtBlock.class, 2,
                GlassBlock.class, 3,
                LemonBlock.class, 4
        );
    }
}
