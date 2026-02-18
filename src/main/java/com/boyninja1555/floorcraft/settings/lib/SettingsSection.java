package com.boyninja1555.floorcraft.settings.lib;

import java.util.Map;

public abstract class SettingsSection<T> {

    public abstract Map<T, Object> values();
}
