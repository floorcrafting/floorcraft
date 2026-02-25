package com.boyninja1555.floorcraft.settings;

import com.boyninja1555.floorcraft.settings.lib.SettingsProfile;
import com.boyninja1555.floorcraft.settings.lib.SettingsSection;
import com.boyninja1555.floorcraft.settings.sections.AudioSection;
import com.boyninja1555.floorcraft.settings.sections.ControlsSection;
import com.boyninja1555.floorcraft.settings.sections.GraphicsSection;
import com.boyninja1555.floorcraft.settings.sections.WorldCreationSection;

import java.util.List;

public class Settings extends SettingsProfile {

    @Override
    public List<Class<? extends SettingsSection<?>>> sections() {
        return List.of(
                GraphicsSection.class,
                AudioSection.class,
                ControlsSection.class,
                WorldCreationSection.class
        );
    }
}
