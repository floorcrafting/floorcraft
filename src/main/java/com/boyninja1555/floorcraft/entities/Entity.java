package com.boyninja1555.floorcraft.entities;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public abstract class Entity {

    @FunctionalInterface
    public interface PositionChangeHook {
        void run(Vector3f position);
    }

    public static final float DEFAULT_GRAVITY = .11f;

    private final Vector3f position;
    private final Vector2f rotation;
    private final List<PositionChangeHook> positionChangeHooks;
    private float gravity;

    public Entity(Vector3f position, Vector2f rotation, float gravity) {
        this.position = position;
        this.rotation = rotation;
        this.positionChangeHooks = new ArrayList<>();
        this.gravity = gravity;
    }

    public Entity(Vector3f position, Vector2f rotation) {
        this.position = position;
        this.rotation = rotation;
        this.positionChangeHooks = new ArrayList<>();
        this.gravity = DEFAULT_GRAVITY;
    }

    public Entity(Vector3f position, float gravity) {
        this.position = position;
        this.rotation = new Vector2f(0f, 0f);
        this.positionChangeHooks = new ArrayList<>();
        this.gravity = gravity;
    }

    public Entity(Vector3f position) {
        this.position = position;
        this.rotation = new Vector2f(0f, 0f);
        this.positionChangeHooks = new ArrayList<>();
        this.gravity = DEFAULT_GRAVITY;
    }

    public Vector3f position() {
        return new Vector3f(position.x, position.y, position.z);
    }

    public Vector2f rotation() {
        return new Vector2f(rotation.x, rotation.y);
    }

    public float gravity() {
        return gravity;
    }

    public void teleport(Vector3f position) {
        this.position.x = position.x;
        this.position.y = position.y;
        this.position.z = position.z;
        runPositionChangeHooks();
    }

    public void rotation(Vector2f rotation) {
        this.rotation.x = rotation.x;
        this.rotation.y = rotation.y;
    }

    public void gravity(float gravity) {
        this.gravity = gravity;
    }

    public void tick(float deltaTime) {
        position.y -= gravity;
    }

    // Hook management

    public void positionChangeHook(PositionChangeHook hook) {
        positionChangeHooks.add(hook);
    }

    private void runPositionChangeHooks() {
        for (PositionChangeHook hook : positionChangeHooks)
            hook.run(position);
    }
}
