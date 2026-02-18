package com.boyninja1555.floorcraft.lib;

import com.boyninja1555.floorcraft.entities.Player;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.lwjgl.glfw.GLFW.*;

public class Controls {

    public static void register(long window, Player player, AtomicInteger width, AtomicInteger height) {
        // Camera rotation
        AtomicBoolean cursorLocked = new AtomicBoolean(false);
        AtomicReference<Float> lastX = new AtomicReference<>((float) width.get() / 2);
        AtomicReference<Float> lastY = new AtomicReference<>((float) height.get() / 2);

        glfwSetCursorPosCallback(window, (ignored, xpos, ypos) -> {
            if (!cursorLocked.get()) return;
            float xoffset = (float) (xpos - lastX.get());
            float yoffset = (float) (lastY.get() - ypos);
            lastX.set((float) xpos);
            lastY.set((float) ypos);
            player.processMouseMovement(xoffset, yoffset);
        });

        // Cursor lock

        glfwSetMouseButtonCallback(window, (ignored, button, action, ignored1) -> {
            if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
                if (cursorLocked.get()) return;

                glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                cursorLocked.set(true);
            }
        });

        glfwSetKeyCallback(window, (ignored, key, ignored1, action, ignored2) -> {
            if (key != GLFW_KEY_ESCAPE || action != GLFW_PRESS) return;

            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            cursorLocked.set(false);
        });
    }
}
