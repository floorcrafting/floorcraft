package com.boyninja1555.floorcraft.lib;

import com.boyninja1555.floorcraft.Floorcraft;
import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;

public class DiscordRichPresence {
    private static final long RECONNECT_DELAY_MS = 10000;
    private static final long UPDATE_COOLDOWN_MS = 5000;

    private static long lastReconnectAttempt = 0;
    private static long lastUpdateSent = 0;
    private static boolean needsUpdate = false;

    private static Core core;
    private static Activity activity;
    private static boolean dontEvenThinkAboutIt;

    public static void init() {
        dontEvenThinkAboutIt = false;

        try {
            CreateParams params = new CreateParams();
            params.setClientID(AppProperties.discordId());
            params.setFlags(CreateParams.Flags.NO_REQUIRE_DISCORD);

            core = new Core(params);
            activity = new Activity();
            activity.assets().setLargeImage("icon");
            activity.assets().setLargeText("Floorcraft");
            updateStatus();
        } catch (Exception ignored) {
            core = null;
            dontEvenThinkAboutIt = true;
        }
    }

    public static void updateStatus() {
        if (dontEvenThinkAboutIt) return;
        if (Floorcraft.player() == null) {
            activity.setDetails("Loading Floorcraft");
            activity.setState("Loading...");
        } else {
            activity.setDetails("Building in Floorcraft");
            activity.setState("Holding " + Floorcraft.player().activeBlock().definition().name() + " block");
        }

        needsUpdate = true;
    }

    public static void tick() {
        if (dontEvenThinkAboutIt) return;
        long now = System.currentTimeMillis();
        if (core == null) {
            if (now - lastReconnectAttempt > RECONNECT_DELAY_MS) {
                lastReconnectAttempt = now;
                init();
            }
            return;
        }

        if (needsUpdate && (now - lastUpdateSent > UPDATE_COOLDOWN_MS)) {
            core.activityManager().updateActivity(activity);
            lastUpdateSent = now;
            needsUpdate = false;
        }

        try {
            core.runCallbacks();
        } catch (Exception e) {
            core.close();
            core = null;
        }
    }

    public static void stop() {
        if (dontEvenThinkAboutIt || core == null) return;

        activity.setDetails("Exiting Floorcraft");
        activity.setState("Exiting...");
        core.activityManager().updateActivity(activity);
        core.close();
    }
}
