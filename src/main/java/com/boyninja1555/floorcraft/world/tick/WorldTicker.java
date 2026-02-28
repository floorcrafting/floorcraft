package com.boyninja1555.floorcraft.world.tick;

import com.boyninja1555.floorcraft.blocks.Block;
import com.boyninja1555.floorcraft.world.World;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

public class WorldTicker {
    private static final List<ScheduledTick> pending = new ArrayList<>();

    public static void tick(World world, float deltaTime) {
        for (int i = pending.size() - 1; i >= 0; i--) {
            ScheduledTick tick = pending.get(i);

            float newTime = tick.timeLeft() - deltaTime;
            if (newTime <= 0) {
                pending.remove(i);

                Block block = world.blockAt(tick.position());
                if (block != null) block.onTick(world, tick.position(), deltaTime);
            } else {
                pending.set(i, new ScheduledTick(tick.position(), newTime));
            }
        }
    }

    public static void schedule(Vector3i position, float delay) {
        if (pending.stream().noneMatch(t -> t.position().equals(position)))
            pending.add(new ScheduledTick(new Vector3i(position), delay));
    }

    // schedule - Lua edition
    public static void schedule(Vector3i position, double delay) {
        schedule(position, (float) delay);
    }
}
