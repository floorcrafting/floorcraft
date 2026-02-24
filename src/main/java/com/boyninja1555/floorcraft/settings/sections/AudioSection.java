package com.boyninja1555.floorcraft.settings.sections;

import com.boyninja1555.floorcraft.settings.lib.SettingsSection;

import java.util.Map;

public class AudioSection extends SettingsSection<AudioSection.Keys> {

    public enum Keys {
        MUSIC_VOLUME, OTHER_VOLUME,
    }

    @Override
    public Map<Keys, Object> values() {
        return Map.of(Keys.MUSIC_VOLUME, 1f, Keys.OTHER_VOLUME, 1f);
    }
}
