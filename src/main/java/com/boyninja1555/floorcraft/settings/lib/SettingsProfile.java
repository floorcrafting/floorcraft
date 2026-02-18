package com.boyninja1555.floorcraft.settings.lib;

import com.boyninja1555.floorcraft.lib.ErrorHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public abstract class SettingsProfile {

    public abstract List<Class<? extends SettingsSection<?>>> sections();

    public SettingsSection<?> sectionByClass(Class<? extends SettingsSection<?>> clazz) {
        List<Class<? extends SettingsSection<?>>> results = sections().stream()
                .filter(s -> s.equals(clazz))
                .toList();

        if (results.isEmpty())
            return null;

        try {
            return results.getFirst().getConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            ErrorHandler.crash("Could not locate settings section!\n" + ex);
            return null;
        }
    }
}
