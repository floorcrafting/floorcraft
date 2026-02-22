package com.boyninja1555.floorcraft.lib;

import com.boyninja1555.floorcraft.Floorcraft;
import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;

public class DiscordRichPresence {
    private static long lastReconnectAttempt = 0;
    private static Core core;
    private static Activity activity;

    public static void init() {
        CreateParams params = new CreateParams();
        params.setClientID(AppProperties.discordId());
        params.setFlags(CreateParams.Flags.NO_REQUIRE_DISCORD);

        core = new Core(params);
        activity = new Activity();
        activity.assets().setLargeImage("icon");
        activity.assets().setLargeText("Floorcraft");
        updateStatus();
    }

    public static void updateStatus() {
        if (Floorcraft.player() == null) {
            activity.setDetails("Loading Floorcraft");
            activity.setState("Loading...");
        } else {
            activity.setDetails("Building in Floorcraft");
            activity.setState("Holding " + Floorcraft.player().activeBlock().definition().name() + " block");
        }

        core.activityManager().updateActivity(activity);
    }

    public static void tick() {
        if (core == null) {
            if (System.currentTimeMillis() - lastReconnectAttempt > 5000) {
                lastReconnectAttempt = System.currentTimeMillis();

                try {
                    init();
                } catch (Exception ignored) {
                }
            }

            return;
        }

        try {
            core.runCallbacks();
        } catch (Exception e) {
            core = null;
        }
    }

    public static void stop() {
        activity.setDetails("Exiting Floorcraft");
        activity.setState("Exiting...");
        core.activityManager().updateActivity(activity);
        core.close();
    }
}
