package com.boyninja1555.floorcraft.lib;

import com.boyninja1555.floorcraft.blocks.DirtBlock;
import com.boyninja1555.floorcraft.blocks.LemonBlock;
import com.boyninja1555.floorcraft.entities.Player;
import com.boyninja1555.floorcraft.settings.lib.SettingsProfile;
import com.boyninja1555.floorcraft.settings.sections.ControlsSection;
import com.boyninja1555.floorcraft.world.World;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.lwjgl.glfw.GLFW.*;

public class Controls {

    public static void register(SettingsProfile settings, long window, World world, Player player, AtomicInteger width, AtomicInteger height) {
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

        glfwSetMouseButtonCallback(window, (ignored, button, action, ignored1) -> {
            // Cursor lock
            if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS && !cursorLocked.get()) {
                glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                cursorLocked.set(true);
                return;
            }

            // TODO: Block breaking & placing

            Vector3i blockPosition = world.raycast(player.position(), player.forward, 6f);

            if (blockPosition == null) return;

            world.setBlock(blockPosition, LemonBlock.class);
        });

        // Cursor unlock
        glfwSetKeyCallback(window, (ignored, key, ignored1, action, ignored2) -> {
            if (key != GLFW_KEY_ESCAPE || action != GLFW_PRESS) return;

            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            cursorLocked.set(false);
        });
    }

    public static Vector3f directionFromRotation(Vector2f py) {
        float radPitch = (float) Math.toRadians(py.x);
        float radYaw = (float) Math.toRadians(py.y);

        float x = (float) (Math.cos(radPitch) * Math.sin(radYaw));
        float y = (float) (Math.sin(radPitch));
        float z = (float) (Math.cos(radPitch) * Math.cos(radYaw));
        return new Vector3f(x, y, z).normalize();
    }
}
