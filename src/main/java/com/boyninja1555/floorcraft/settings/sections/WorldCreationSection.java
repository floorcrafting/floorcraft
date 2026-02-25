package com.boyninja1555.floorcraft.settings.sections;

import com.boyninja1555.floorcraft.settings.lib.SettingsSection;
import org.joml.Vector2i;

import java.util.Map;

public class WorldCreationSection extends SettingsSection<WorldCreationSection.Keys> {

    public enum Keys {
        WORLD_DIMENSIONS,
    }

    @Override
    public Map<Keys, Object> values() {
        return Map.of(Keys.WORLD_DIMENSIONS, new Vector2i(4, 4));
    }
}
