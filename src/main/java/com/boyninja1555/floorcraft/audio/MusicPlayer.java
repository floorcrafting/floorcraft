package com.boyninja1555.floorcraft.audio;

import com.boyninja1555.floorcraft.lib.AssetManager;
import com.boyninja1555.floorcraft.settings.Settings;
import com.boyninja1555.floorcraft.settings.sections.AudioSection;

import java.io.File;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.lwjgl.openal.AL10.*;

public class MusicPlayer {
    private static final Map<String, Integer> buffers = new HashMap<>();
    private static final List<Integer> activeSources = new ArrayList<>();
    private static final int MAX_SOURCES = 32;
    private static final float INTERVAL_MS = 30 * 1000; // 30 seconds
    private static final Random RANDOM = new Random();
    private static ScheduledExecutorService scheduler;
    private static boolean running = false;
    private static Settings settings;

    public static void init(Settings settings) {
        MusicPlayer.settings = settings;
        File musicDir = AssetManager.musicPath().toFile();
        File[] musicFiles = musicDir.listFiles();

        if (musicFiles == null) return;
        for (File file : musicFiles) {
            String filename = file.getName();
            if (!filename.endsWith(".ogg")) continue;

            String name = filename.substring(0, filename.lastIndexOf(".ogg"));
            System.out.println("Discovered music file named " + filename + " (" + name + ")");
            register(name, name);
        }

        if (!buffers.isEmpty()) startSequence();
    }

    public static void register(String name, String filename) {
        int bufferId = OggLoader.load(AssetManager.musicPath().resolve(filename + ".ogg").toString());
        if (bufferId != -1) buffers.put(name, bufferId);
    }

    public static void play(String name, float volume, float pitch) {
        Integer bufferId = buffers.get(name);

        if (bufferId == null) return;
        int sourceId = getAvailableSource();
        if (sourceId == -1) return;

        alSourcei(sourceId, AL_BUFFER, bufferId);
        Map<?, Object> audioSection = settings.sectionByClass(AudioSection.class).values();

        if (audioSection == null) alSourcef(sourceId, AL_GAIN, volume);
        else alSourcef(sourceId, AL_GAIN, volume * (float) audioSection.get(AudioSection.Keys.MUSIC_VOLUME));

        alSourcef(sourceId, AL_PITCH, pitch);
        alSourcef(sourceId, AL_ROLLOFF_FACTOR, 1f);
        alSourcePlay(sourceId);
    }

    private static void startSequence() {
        running = true;
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "MusicSequence");
            t.setDaemon(true);
            return t;
        });

        planNextTrack(0);
    }

    private static void planNextTrack(long delayMs) {
        if (!running) return;

        scheduler.schedule(() -> {
            try {
                sequence();
                planNextTrack((long) INTERVAL_MS + 41 * 1000); // Currently a fixed length
            } catch (Exception ignored) {
            }
        }, delayMs, TimeUnit.MILLISECONDS);
    }

    public static void sequence() {
        if (!running || buffers.isEmpty()) return;

        List<String> keys = new ArrayList<>(buffers.keySet());
        String randomTrack = keys.get(RANDOM.nextInt(keys.size()));
        play(randomTrack, 1f, 1f);
    }

    private static int getAvailableSource() {
        for (int source : activeSources)
            if (alGetSourcei(source, AL_SOURCE_STATE) == AL_STOPPED) return source;

        if (activeSources.size() < MAX_SOURCES) {
            int source = alGenSources();
            activeSources.add(source);
            return source;
        }

        return -1;
    }

    public static void cleanup() {
        running = false;

        if (scheduler != null) scheduler.shutdownNow();

        for (int source : activeSources) {
            alSourceStop(source);
            alDeleteSources(source);
        }

        for (int buffer : buffers.values())
            alDeleteBuffers(buffer);
    }
}
