package com.boyninja1555.floorcraft.blocks;

import com.boyninja1555.floorcraft.audio.SoundPlayer;
import com.boyninja1555.floorcraft.texture.atlas.TextureAtlas;
import com.boyninja1555.floorcraft.world.World;
import com.boyninja1555.floorcraft.world.tick.WorldTicker;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

public class SeeSeeBlock extends Block {
    public static final float FALL_DELAY = .1f;

    public SeeSeeBlock(TextureAtlas atlas) {
        super(atlas);
    }

    @Override
    public @NotNull String identifier() {
        return "see_see";
    }

    @Override
    public void onPlace(World world, Vector3i position) {
        SoundPlayer.playForBlock(getClass(), SoundPlayer.BlockSoundType.PLACE, position, 1f, 1f);
        WorldTicker.schedule(position, FALL_DELAY);
    }

    @Override
    public void onBreak(World world, Vector3i position) {
        SoundPlayer.playForBlock(getClass(), SoundPlayer.BlockSoundType.BREAK, position, .7f, .8f);
    }

    @Override
    public void onTick(World world, Vector3i position, float deltaTime) {
        checkFall(world, position);
    }

    private void checkFall(World world, Vector3i position) {
        Vector3i below = new Vector3i(position.x, position.y - 1, position.z);

        if (position.y > 0 && world.blockAt(below) == null) {
            world.removeBlock(position);
            world.setBlock(below, getClass());
            WorldTicker.schedule(below, FALL_DELAY);
        }
    }
}
