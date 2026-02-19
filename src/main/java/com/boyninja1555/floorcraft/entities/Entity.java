package com.boyninja1555.floorcraft.entities;

import org.joml.Vector2f;
import org.joml.Vector3f;

public abstract class Entity {
    public static final float DEFAULT_GRAVITY = .11f;

    private final Vector3f position;
    private final Vector2f direction;
    private float gravity;

    public Entity(Vector3f position, Vector2f direction, float gravity) {
        this.position = position;
        this.direction = direction;
        this.gravity = gravity;
    }

    public Entity(Vector3f position, Vector2f direction) {
        this.position = position;
        this.direction = direction;
        this.gravity = DEFAULT_GRAVITY;
    }

    public Entity(Vector3f position, float gravity) {
        this.position = position;
        this.direction = new Vector2f(0f, 0f);
        this.gravity = gravity;
    }

    public Entity(Vector3f position) {
        this.position = position;
        this.direction = new Vector2f(0f, 0f);
        this.gravity = DEFAULT_GRAVITY;
    }

    public Vector3f position() {
        return new Vector3f(position.x, position.y, position.z);
    }

    public Vector2f direction() {
        return new Vector2f(direction.x, direction.y);
    }

    public float gravity() {
        return gravity;
    }

    public void teleport(Vector3f position) {
        this.position.x = position.x;
        this.position.y = position.y;
        this.position.z = position.z;
    }

    public void direction(Vector2f rotation) {
        this.direction.x = rotation.x;
        this.direction.y = rotation.y;
    }

    public void gravity(float gravity) {
        this.gravity = gravity;
    }

    public void tick(float deltaTime) {
        position.y -= gravity;
    }
}
