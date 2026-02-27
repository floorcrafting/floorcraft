package com.boyninja1555.floorcraft.lib;

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
        void run(String blockIdentifier);
    }

    private static final Map<Integer, String> keys = new HashMap<>();
    private static final Map<Integer, Boolean> keyState = new HashMap<>();
    private static final File file = AssetManager.storagePath().resolve("block-keybinds.properties").toFile();

    public static Map<Integer, String> all() {
        return new HashMap<>(keys);
    }

    public static void init() {
        keys.clear();
        keyState.clear();

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
                String value = prop.getProperty(keyStr);
                if (value == null || value.isBlank()) continue;

                String blockIdentifier = parseLegacyValue(value);
                if (blockIdentifier == null) continue;
                keys.put(key, blockIdentifier);
            }
        } catch (IOException | NumberFormatException ex) {
            setDefaultKeys();
        }

        if (keys.isEmpty()) setDefaultKeys();
    }

    public static void save() {
        Properties config = new Properties();

        for (var entry : keys.entrySet())
            config.setProperty(String.valueOf(entry.getKey()), entry.getValue());

        try (FileOutputStream out = new FileOutputStream(file)) {
            config.store(out, "Floorcraft Block Selection Keybinds");
        } catch (IOException ex) {
            String message = "Could not save block keybinds! You will not be able to select any blocks.\n" + ex;
            System.err.println(message);
            ErrorHandler.error(message);
        }
    }

    private static void setDefaultKeys() {
        keys.clear();
        keys.put(GLFW_KEY_1, "stone");
        keys.put(GLFW_KEY_2, "dirt");
        keys.put(GLFW_KEY_3, "glass");
        keys.put(GLFW_KEY_4, "lemon");
        keys.put(GLFW_KEY_5, "heart");
        keys.put(GLFW_KEY_6, "see_see");
        keys.put(GLFW_KEY_7, "skinned");
        keys.put(GLFW_KEY_8, "agony");
        keys.put(GLFW_KEY_9, "preserved_deity_head");
        keys.put(GLFW_KEY_0, "disturbed_head");
    }

    public static void detectKeys(long window, PressedCallback callback) {
        for (int key : keys.keySet()) {
            boolean isDown = glfwGetKey(window, key) == GLFW_PRESS;
            boolean wasDown = keyState.getOrDefault(key, false);
            if (isDown && !wasDown) callback.run(keys.get(key));

            keyState.put(key, isDown);
        }
    }

    private static String parseLegacyValue(String rawValue) {
        String value = rawValue.trim();
        try {
            int blockId = Integer.parseInt(value);
            return WorldBlockIDs.identifierFromId(blockId);
        } catch (NumberFormatException ignored) {
            return value;
        }
    }
}
