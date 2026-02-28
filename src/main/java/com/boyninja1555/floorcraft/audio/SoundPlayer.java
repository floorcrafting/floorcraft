package com.boyninja1555.floorcraft.audio;

import com.boyninja1555.floorcraft.Floorcraft;
import com.boyninja1555.floorcraft.blocks.Block;
import com.boyninja1555.floorcraft.lib.AssetManager;
import com.boyninja1555.floorcraft.settings.Settings;
import com.boyninja1555.floorcraft.settings.sections.AudioSection;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.*;

import static org.lwjgl.openal.AL10.*;

public class SoundPlayer {
    private static final Map<String, Integer> buffers = new HashMap<>();
    private static final List<Integer> activeSources = new ArrayList<>();
    private static final int MAX_SOURCES = 32;
    private static Settings settings;

    public static void settings(Settings settings) {
        SoundPlayer.settings = settings;
    }

    public enum BlockSoundType {
        BREAK("break"), PLACE("place");

        public final String string;

        BlockSoundType(String string) {
            this.string = string;
        }

        public static BlockSoundType fromString(String string) {
            List<BlockSoundType> results = Arrays.stream(values()).filter(r -> r.string.equals(string)).toList();
            if (results.isEmpty()) return null;
            return results.getFirst();
        }
    }

    public static void register(String name, String filename) {
        int bufferId = OggLoader.load(AssetManager.soundsPath().resolve(filename + ".ogg").toString());
        if (bufferId != -1) buffers.put(name, bufferId);
        System.out.println("Sound buffers updated! " + buffers);
    }

    public static void register(String filename) {
        register(filename.replaceAll("/", "."), filename);
    }

    public static void registerForBlock(Class<? extends Block> blockClass) {
        Block block = Floorcraft.blockRegistry().get(blockClass);
        String root = "blocks/" + block.identifier();
        System.out.println("Registered root sound " + root);

        for (BlockSoundType type : BlockSoundType.values()) {
            String typePath = root + "/" + type.string;
            register(typePath);
            System.out.println("Registered sound " + typePath);
        }
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

    public static void playForBlock(String blockId, BlockSoundType type, Vector3i position, float volume, float pitch) {
        Map<?, Object> audioSection = settings.sectionByClass(AudioSection.class).values();

        if (audioSection == null) {
            play("blocks." + blockId + "." + type.string, new Vector3f(position), volume, pitch);
            return;
        }

        play("blocks." + blockId + "." + type.string, new Vector3f(position), volume * (float) audioSection.get(AudioSection.Keys.OTHER_VOLUME), pitch);
    }

    // playForBlock - Lua edition
    public static void play_for_block(String blockId, String type, Vector3i position, double volume, double pitch) {
        System.out.println("[Lua Audio Debug] Attempting to play sound named " + blockId + " of type " + type);
        String soundName = "blocks." + blockId + "." + type;
        Integer bufferId = buffers.get(soundName);

        if (bufferId == null) {
            System.err.println("[Lua Audio Debug] FAILED! No buffer registered for name: " + soundName);
            System.err.println("[Lua Audio Debug] Registered buffers are: " + buffers.keySet());
            return;
        }

        playForBlock(blockId, BlockSoundType.fromString(type), position, (float) volume, (float) pitch);
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
