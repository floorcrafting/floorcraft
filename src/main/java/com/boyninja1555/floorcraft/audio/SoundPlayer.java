package com.boyninja1555.floorcraft.audio;

import com.boyninja1555.floorcraft.Floorcraft;
import com.boyninja1555.floorcraft.blocks.Block;
import com.boyninja1555.floorcraft.lib.AssetManager;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.openal.AL10.*;

public class SoundPlayer {
    private static final Map<String, Integer> buffers = new HashMap<>();
    private static final List<Integer> activeSources = new ArrayList<>();
    private static final int MAX_SOURCES = 32;

    public enum BlockSoundType {
        BREAK("break"), PLACE("place");

        public final String string;

        BlockSoundType(String string) {
            this.string = string;
        }
    }

    public static void register(String name, String filename) {
        int bufferId = OggLoader.load(AssetManager.soundsPath().resolve(filename + ".ogg").toString());
        if (bufferId != -1) buffers.put(name, bufferId);
    }

    public static void register(String filename) {
        register(filename.replaceAll("/", "."), filename);
    }

    public static void registerForBlock(Class<? extends Block> blockClass) {
        Block block = Floorcraft.blockRegistry().get(blockClass);
        String root = "blocks/" + block.identifier();

        for (BlockSoundType type : BlockSoundType.values())
            register(root + "/" + type.string);
    }

    public static void registerForBlock(Class<? extends Block> blockClass, String... extra) {
        Block block = Floorcraft.blockRegistry().get(blockClass);
        registerForBlock(blockClass);

        for (String x : extra) register("blocks/" + block.identifier() + "/" + x);
    }

    public static void play(String name, Vector3f position, float volume, float pitch) {
        Integer bufferId = buffers.get(name);

        if (bufferId == null) return;
        int sourceId = getAvailableSource();
        if (sourceId == -1) return;

        alSourcei(sourceId, AL_BUFFER, bufferId);
        alSourcef(sourceId, AL_GAIN, volume);
        alSourcef(sourceId, AL_PITCH, pitch);
        alSource3f(sourceId, AL_POSITION, position.x, position.y, position.z);

        alSourcef(sourceId, AL_REFERENCE_DISTANCE, volume * 2f);
        alSourcef(sourceId, AL_MAX_DISTANCE, volume * 15f);
        alSourcef(sourceId, AL_ROLLOFF_FACTOR, 1f);
        alSourcePlay(sourceId);
    }

    public static void playForBlock(Class<? extends Block> blockClass, BlockSoundType type, Vector3i position, float volume, float pitch) {
        Block block = Floorcraft.blockRegistry().get(blockClass);
        play("blocks." + block.identifier() + "." + type.string, new Vector3f(position), volume, pitch);
    }

    private static int getAvailableSource() {
        for (int i = 0; i < activeSources.size(); i++) {
            int source = activeSources.get(i);
            if (alGetSourcei(source, AL_SOURCE_STATE) == AL_STOPPED) return source;
        }

        if (activeSources.size() < MAX_SOURCES) {
            int source = alGenSources();
            activeSources.add(source);
            return source;
        }

        return -1;
    }

    public static void cleanup() {
        for (int source : activeSources) {
            alSourceStop(source);
            alDeleteSources(source);
        }

        for (int buffer : buffers.values())
            alDeleteBuffers(buffer);
    }
}
