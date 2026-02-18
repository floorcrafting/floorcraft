package com.boyninja1555.floorcraft.settings.sections;

import com.boyninja1555.floorcraft.settings.lib.SettingsSection;

import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class ControlsSection extends SettingsSection<ControlsSection.Keys> {

    public enum Keys {
        // Mouse
        MOUSE_SENSITIVITY,

        // Movement
        MOVE_FORWARDS,
        MOVE_BACKWARDS,
        MOVE_LEFT,
        MOVE_RIGHT,
        SPRINT,
        JUMP,
        SNEAK,
    }

    @Override
    public Map<Keys, Object> values() {
        return Map.of(
                // Mouse
                Keys.MOUSE_SENSITIVITY, .1f,

                // Movement
                Keys.MOVE_FORWARDS, GLFW_KEY_W,
                Keys.MOVE_BACKWARDS, GLFW_KEY_S,
                Keys.MOVE_LEFT, GLFW_KEY_A,
                Keys.MOVE_RIGHT, GLFW_KEY_D,
                Keys.SPRINT, GLFW_KEY_LEFT_SHIFT,
                Keys.JUMP, GLFW_KEY_SPACE,
                Keys.SNEAK, GLFW_KEY_LEFT_CONTROL
        );
    }
}
