package com.boyninja1555.floorcraft.blocks.scripting;

import com.boyninja1555.floorcraft.world.World;
import org.joml.Vector3i;

@FunctionalInterface
public interface BlockUpdateCallback {
    void run(World world, Vector3i position, float deltaTime);
}
