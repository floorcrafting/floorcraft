package com.boyninja1555.floorcraft.settings.sections;

import com.boyninja1555.floorcraft.settings.lib.SettingsSection;
import org.joml.Vector2i;
import org.joml.Vector4f;

import java.util.Map;

public class GraphicsSection extends SettingsSection<GraphicsSection.Keys> {

    public enum Keys {
        WINDOW_SIZE,
        FOV,
        SKY_COLOR,
    }

    @Override
    public Map<Keys, Object> values() {
        return Map.of(
                Keys.WINDOW_SIZE, new Vector2i(1280, 720),
                Keys.FOV, 90,
                Keys.SKY_COLOR, new Vector4f(0f, .4f, .8f, 1f)
        );
    }
}
