package com.boyninja1555.floorcraft.visual;

import com.boyninja1555.floorcraft.entities.Player;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private static final float SPRINT_FOV_MULTIPLIER = 1.2f;

    // View / projection values
    private final int width;
    private final int height;
    private final int definedFov;
    private final Matrix4f proj = new Matrix4f();
    private final Matrix4f view = new Matrix4f();
    private int fov;

    // Player reference
    private final Player playerRef;

    public Camera(Player playerRef, int width, int height, int fov) {
        this.playerRef = playerRef;
        this.width = width;
        this.height = height;
        this.definedFov = fov;
        this.fov = fov;
        updateProjection();
        updateView();
    }

    public void updateProjection() {
        proj.identity().perspective((float) Math.toRadians(fov), (float) width / height, .1f, 100f);
    }

    public void updateView() {
        Vector3f center = playerRef.position().add(playerRef.forward);
        view.identity().lookAt(playerRef.position(), center, playerRef.up);
    }

    public Matrix4f projection() {
        return proj;
    }

    public Matrix4f view() {
        return view;
    }

    public void handleSprintingFOV(boolean sprinting, float deltaTime) {
        float targetFov = sprinting ? (definedFov * SPRINT_FOV_MULTIPLIER) : definedFov;
        float fovSpeed = 100f;
        if (Math.abs(fov - targetFov) > .1f) {
            if (fov < targetFov) fov = Math.round(Math.min(targetFov, fov + (fovSpeed * deltaTime)));
            else fov = Math.round(Math.max(targetFov, fov - (fovSpeed * deltaTime)));

            updateProjection();
        }
    }
}
