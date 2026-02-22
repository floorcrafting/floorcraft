package com.boyninja1555.floorcraft.lib;

import com.boyninja1555.floorcraft.Floorcraft;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppProperties {
    private static String wiki;
    private static long discordId;

    public static String wiki() {
        return wiki;
    }

    public static long discordId() {
        return discordId;
    }

    public static void load() {
        Properties props = new Properties();

        try (InputStream stream = Floorcraft.class.getResourceAsStream("/globals.properties")) {
            props.load(stream);
            wiki = props.getProperty("wiki");
            discordId = Long.parseLong(props.getProperty("discord-id"));
        } catch (IOException ex) {
            String message = "Could not load app properties!\n" + ex;
            System.err.println(message);
            ErrorHandler.crash(message);
        }
    }
}
