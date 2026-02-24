package com.boyninja1555.floorcraft.lib;

import com.boyninja1555.floorcraft.blocks.Block;
import com.boyninja1555.floorcraft.entities.Player;
import com.boyninja1555.floorcraft.world.World;
import org.joml.Vector3i;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.lwjgl.glfw.GLFW.*;

public class Controls {

    public static void register(long window, World world, Player player, AtomicInteger width, AtomicInteger height) {
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

            if (!cursorLocked.get()) return;

            if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS && cursorLocked.get()) {
                Vector3i blockPosition = world.raycast(player.position(), player.forward, 5f, false);

                if (blockPosition == null) return;

                world.removeBlock(blockPosition);
            } else if (button == GLFW_MOUSE_BUTTON_RIGHT && action == GLFW_PRESS && cursorLocked.get()) {
                Vector3i blockPosition = world.raycast(player.position(), player.forward, 5f, true);

                if (blockPosition == null) return;

                world.setBlock(blockPosition, player.activeBlock().getClass());
            } else if (button == GLFW_MOUSE_BUTTON_MIDDLE && action == GLFW_PRESS && cursorLocked.get()) {
                Vector3i blockPosition = world.raycast(player.position(), player.forward, 5f, false);

                if (blockPosition == null) return;

                Block block = world.blockAt(blockPosition);

                if (block == null) return;

                player.activeBlock(block);
            }
        });

        glfwSetKeyCallback(window, (ignored, key, ignored1, action, mods) -> {
            // Cursor unlock
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                cursorLocked.set(false);
                return;
            }

            // World saving
            if ((key == GLFW_KEY_HOME || key == GLFW_KEY_Z) && action == GLFW_PRESS) {
                world.save();
                return;
            }

            // Show storage directory
            if (key == GLFW_KEY_X && action == GLFW_PRESS) {
                cursorLocked.set(false);
                glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                String path = AssetManager.storagePath().toAbsolutePath().toString();
                String os = System.getProperty("os.name").toLowerCase();

                try {
                    if (os.contains("win")) new ProcessBuilder("explorer.exe", path).start();
                    else if (os.contains("mac")) new ProcessBuilder("open", path).start();
                    else new ProcessBuilder("xdg-open", path).start();
                } catch (Exception ex) {
                    ErrorHandler.error("Could not open storage directory!\n" + ex);
                }
            }

            // Wiki
            if (key == GLFW_KEY_SLASH && (mods & GLFW_MOD_SHIFT) != 0) {
                cursorLocked.set(false);
                glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                String url = AppProperties.wiki();
                String os = System.getProperty("os.name").toLowerCase();

                try {
                    if (os.contains("win")) new ProcessBuilder("cmd", "/c", "start", url).start();
                    else if (os.contains("mac")) new ProcessBuilder("open", url).start();
                    else if (os.contains("nix") || os.contains("nux")) new ProcessBuilder("xdg-open", url).start();
                } catch (Exception ex) {
                    ErrorHandler.error("Could not open wiki!\n" + ex);
                }
            }
        });
    }
}
