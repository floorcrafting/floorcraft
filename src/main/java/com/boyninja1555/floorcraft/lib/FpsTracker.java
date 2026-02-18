package com.boyninja1555.floorcraft.lib;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class FpsTracker {
    private static int frames;
    private static int slowFrames;
    private static double lastTime = glfwGetTime();

    public static void updateFPS() {
        double currentTime = glfwGetTime();
        frames++;

        if (currentTime - lastTime >= 1.0) {
            slowFrames = frames;
            frames = 0;
            lastTime = currentTime;
        }
    }

    public static int current() {
        return frames;
    }

    public static String to3digits() {
        int number = Math.min(Math.max(slowFrames, 0), 999);
        int hundreds = number / 100;
        int tens = (number / 10) % 10;
        int ones = number % 10;
        return String.valueOf(hundreds) + tens + ones + " fps";
    }
}
