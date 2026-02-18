package com.boyninja1555.floorcraft.texture.atlas;

import com.boyninja1555.floorcraft.lib.AssetManager;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class TextureAtlas {
    private final int textureId;
    private final int atlasWidth, atlasHeight;
    private final int tileSize;

    public TextureAtlas(String path, int tileSize) {
        this.tileSize = tileSize;
        int[] w = new int[1], h = new int[1], comp = new int[1];
        ByteBuffer image;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pw = stack.mallocInt(1);
            IntBuffer ph = stack.mallocInt(1);
            IntBuffer pcomp = stack.mallocInt(1);
            image = STBImage.stbi_load(AssetManager.storagePath().resolve("assets") + "/" + path, pw, ph, pcomp, 4);

            if (image == null)
                throw new RuntimeException("Failed to load texture atlas! " + STBImage.stbi_failure_reason());

            w[0] = pw.get(0);
            h[0] = ph.get(0);
            comp[0] = pcomp.get(0);
        }

        atlasWidth = w[0];
        atlasHeight = h[0];
        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, atlasWidth, atlasHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        STBImage.stbi_image_free(image);
    }

    public void bind() {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);
    }

    public AtlasRegion region(int gridX, int gridY) {
        float u0 = (gridX * tileSize) / (float) atlasWidth;
        float v0 = (gridY * tileSize) / (float) atlasHeight;
        float u1 = ((gridX + 1) * tileSize) / (float) atlasWidth;
        float v1 = ((gridY + 1) * tileSize) / (float) atlasHeight;
        return new AtlasRegion(u0, v0, u1, v1);
    }
}
