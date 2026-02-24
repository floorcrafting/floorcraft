package com.boyninja1555.floorcraft.ui;

import com.boyninja1555.floorcraft.mesh.UIMesh;
import com.boyninja1555.floorcraft.texture.atlas.AtlasRegion;
import com.boyninja1555.floorcraft.ui.element.lib.base.HUDElement;
import com.boyninja1555.floorcraft.ui.element.lib.base.HUDSide;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class HUDManager {
    private final List<HUDElement> elements;

    public HUDManager() {
        elements = new ArrayList<>();
    }

    public void newElement(Class<? extends HUDElement> elementClass) {
        try {
            HUDElement element = elementClass.getConstructor().newInstance();
            elements.add(element);
        } catch (Exception ignored) {
        }
    }

    public void render(float[] matrixBuffer, int uModel, Vector2i windowSize, float paddingX, float paddingY) {
        Map<HUDSide, Float> offsets = new EnumMap<>(HUDSide.class);

        for (HUDElement element : elements) {
            float elementSize = element.size();

            Vector2f position = element.position(windowSize, offsets, paddingX);
            HUDSide side = element.side();

            if (side != null) {
                float current = offsets.getOrDefault(side, paddingY);
                offsets.put(side, current + elementSize + paddingY);
            }

            element.atlas().bind();

            float x = position.x;
            for (AtlasRegion region : element.regions()) {
                Matrix4f model = new Matrix4f().translation(x, position.y, 0f).scale(elementSize, elementSize, 1f);
                glUniformMatrix4fv(uModel, false, model.get(matrixBuffer));

                UIMesh mesh = new UIMesh(region);
                mesh.render();
                x += (elementSize + paddingX);
            }
        }
    }
}
