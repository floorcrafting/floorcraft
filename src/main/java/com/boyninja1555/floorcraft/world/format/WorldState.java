package com.boyninja1555.floorcraft.world.format;

import com.boyninja1555.floorcraft.blocks.Block;
import com.boyninja1555.floorcraft.world.Chunk;
import org.joml.Vector2f;
import org.joml.Vector3f;

public record WorldState(Vector3f playerPosition, Vector2f playerDirection, Block activeBlock, Chunk[] chunks) {
}
