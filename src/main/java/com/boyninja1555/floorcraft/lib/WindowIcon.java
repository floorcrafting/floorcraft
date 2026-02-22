package com.boyninja1555.floorcraft.lib;

import com.boyninja1555.floorcraft.Floorcraft;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;

public class WindowIcon {

    public static void setWindowIcon(long window, String path) {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("mac")) return;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);
            ByteBuffer icon = STBImage.stbi_load(Floorcraft.class.getResource("/" + path).getPath(), w, h, comp, 4);

            if (icon == null) return;

            GLFWImage image = GLFWImage.malloc();
            GLFWImage.Buffer imageBuffer = GLFWImage.malloc(1);
            image.set(w.get(0), h.get(0), icon);
            imageBuffer.put(0, image);

            glfwSetWindowIcon(window, imageBuffer);

            STBImage.stbi_image_free(icon);
        }
    }
}
