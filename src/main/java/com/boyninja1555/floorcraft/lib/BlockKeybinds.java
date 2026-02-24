package com.boyninja1555.floorcraft.lib;

import com.boyninja1555.floorcraft.blocks.*;
import com.boyninja1555.floorcraft.world.format.WorldBlockIDs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.lwjgl.glfw.GLFW.*;

public class BlockKeybinds {

    @FunctionalInterface
    public interface PressedCallback {
        void run(Class<? extends Block> block);
    }

    private static final Map<Integer, Class<? extends Block>> keys = new HashMap<>();
    private static final Map<Integer, Boolean> keyState = new HashMap<>();
    private static final File file = AssetManager.storagePath().resolve("block-keybinds.properties").toFile();

    public static Map<Integer, Class<? extends Block>> all() {
        return new HashMap<>(keys);
    }

    public static void init() {
        if (!file.exists()) {
            setDefaultKeys();
            save();
            return;
        }

        Properties prop = new Properties();

        try (FileInputStream in = new FileInputStream(file)) {
            prop.load(in);

            for (String keyStr : prop.stringPropertyNames()) {
                int key = Integer.parseInt(keyStr);
                int blockId = Integer.parseInt(prop.getProperty(keyStr));
                keys.put(key, WorldBlockIDs.blockClassFromId(blockId));
            }
        } catch (IOException | NumberFormatException ex) {
            setDefaultKeys();
        }
    }

    public static void save() {
        Properties config = new Properties();

        for (var entry : keys.entrySet())
            config.setProperty(String.valueOf(entry.getKey()), String.valueOf(WorldBlockIDs.idFromBlock(entry.getValue())));

        try (FileOutputStream out = new FileOutputStream(file)) {
            config.store(out, "Floorcraft Block Selection Keybinds");
        } catch (IOException ex) {
            String message = "Could not save block keybinds! You will not be able to select any blocks.\n" + ex;
            System.err.println(message);
            ErrorHandler.error(message);
        }
    }

    private static void setDefaultKeys() {
        keys.put(GLFW_KEY_1, StoneBlock.class);
        keys.put(GLFW_KEY_2, DirtBlock.class);
        keys.put(GLFW_KEY_3, GlassBlock.class);
        keys.put(GLFW_KEY_4, LemonBlock.class);
        keys.put(GLFW_KEY_5, HeartBlock.class);
        keys.put(GLFW_KEY_6, SeeSeeBlock.class);
        keys.put(GLFW_KEY_7, SkinnedBlock.class);
        keys.put(GLFW_KEY_8, AgonyBlock.class);
        keys.put(GLFW_KEY_9, PreservedDeityHeadBlock.class);
        keys.put(GLFW_KEY_0, DisturbedHeadBlock.class);
    }

    public static void detectKeys(long window, PressedCallback callback) {
        for (int key : keys.keySet()) {
            boolean isDown = glfwGetKey(window, key) == GLFW_PRESS;
            boolean wasDown = keyState.getOrDefault(key, false);
            if (isDown && !wasDown) callback.run(keys.get(key));

            keyState.put(key, isDown);
        }
    }
}
