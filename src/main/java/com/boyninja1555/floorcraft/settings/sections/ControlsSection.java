package com.boyninja1555.floorcraft.settings.sections;

import com.boyninja1555.floorcraft.settings.lib.SettingsSection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class ControlsSection extends SettingsSection<ControlsSection.Keys> {

    public enum Keys {
        // Mouse
        MOUSE_SENSITIVITY, BLOCK_MODIFY_RANGE,

        // Movement
        MOVE_FORWARDS, MOVE_BACKWARDS, MOVE_LEFT, MOVE_RIGHT, SPRINT, JUMP, SNEAK,

        // Block selection
        SELECT_STONE, SELECT_DIRT, SELECT_GLASS, SELECT_LEMON, SELECT_HEART, SELECT_SEE_SEE, SELECT_SKINNED, SELECT_AGONY, SELECT_PRESERVED_DEITY_HEAD, SELECT_DISTURBED_HEAD,
    }

    @Override
    public Map<Keys, Object> values() {
        HashMap<Keys, Object> values = new HashMap<>();

        // Mouse
        values.put(Keys.MOUSE_SENSITIVITY, .1f);
        values.put(Keys.BLOCK_MODIFY_RANGE, 5f);

        // Movement
        values.put(Keys.MOVE_FORWARDS, GLFW_KEY_W);
        values.put(Keys.MOVE_BACKWARDS, GLFW_KEY_S);
        values.put(Keys.MOVE_LEFT, GLFW_KEY_A);
        values.put(Keys.MOVE_RIGHT, GLFW_KEY_D);
        values.put(Keys.SPRINT, GLFW_KEY_LEFT_SHIFT);
        values.put(Keys.JUMP, GLFW_KEY_SPACE);
        values.put(Keys.SNEAK, GLFW_KEY_LEFT_CONTROL);

        // Block selection
        values.put(Keys.SELECT_STONE, GLFW_KEY_1);
        values.put(Keys.SELECT_DIRT, GLFW_KEY_2);
        values.put(Keys.SELECT_GLASS, GLFW_KEY_3);
        values.put(Keys.SELECT_LEMON, GLFW_KEY_4);
        values.put(Keys.SELECT_HEART, GLFW_KEY_5);
        values.put(Keys.SELECT_SEE_SEE, GLFW_KEY_6);
        values.put(Keys.SELECT_SKINNED, GLFW_KEY_7);
        values.put(Keys.SELECT_AGONY, GLFW_KEY_8);
        values.put(Keys.SELECT_PRESERVED_DEITY_HEAD, GLFW_KEY_9);
        values.put(Keys.SELECT_DISTURBED_HEAD, GLFW_KEY_0);
        return Collections.unmodifiableMap(values);
    }
}
