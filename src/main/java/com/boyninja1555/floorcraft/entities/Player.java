package com.boyninja1555.floorcraft.entities;

import com.boyninja1555.floorcraft.Floorcraft;
import com.boyninja1555.floorcraft.blocks.*;
import com.boyninja1555.floorcraft.blocks.Block;
import com.boyninja1555.floorcraft.lib.DiscordRichPresence;
import com.boyninja1555.floorcraft.lib.ErrorHandler;
import com.boyninja1555.floorcraft.settings.lib.SettingsProfile;
import com.boyninja1555.floorcraft.settings.sections.ControlsSection;
import com.boyninja1555.floorcraft.settings.sections.GraphicsSection;
import com.boyninja1555.floorcraft.visual.Camera;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.Map;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetKey;

public class Player extends Entity {
    // Constants
    private static final float MOVE_SPEED = 8f;
    private static final float SPRINT_MULTIPLIER = 2f;

    private final SettingsProfile settings;
    private final Camera camera;
    private final boolean you;

    // Directional values
    public static final Vector3f WORLD_UP = new Vector3f(0f, 1f, 0f);
    public Vector3f up = new Vector3f(0f, 1f, 0f);
    public Vector3f forward = new Vector3f(0f, 0f, -1f);
    public Vector3f right = new Vector3f(1f, 0f, 0f);

    // Inventory
    private Block activeBlock;

    public Player(SettingsProfile settings, Vector3f position, Vector2f rotation, float gravity, boolean you) {
        super(position, rotation, gravity);
        this.settings = settings;
        this.activeBlock = Floorcraft.blockRegistry().get(LemonBlock.class);

        Map<?, Object> graphicsSettings = settings.sectionByClass(GraphicsSection.class).values();

        if (graphicsSettings == null) ErrorHandler.crash("Missing graphics settings");

        Vector2i windowSize = (Vector2i) graphicsSettings.get(GraphicsSection.Keys.WINDOW_SIZE);

        this.you = you;
        if (you)
            camera = new Camera(this, windowSize.x, windowSize.y, (int) graphicsSettings.get(GraphicsSection.Keys.FOV));
        else camera = null;
    }

    public Player(SettingsProfile settings, Vector3f position, float gravity, boolean you) {
        super(position, gravity);
        this.settings = settings;
        this.activeBlock = Floorcraft.blockRegistry().get(LemonBlock.class);

        Map<?, Object> graphicsSettings = settings.sectionByClass(GraphicsSection.class).values();

        if (graphicsSettings == null) ErrorHandler.crash("Missing graphics settings");

        Vector2i windowSize = (Vector2i) graphicsSettings.get(GraphicsSection.Keys.WINDOW_SIZE);
        this.you = you;
        if (you)
            camera = new Camera(this, windowSize.x, windowSize.y, (int) graphicsSettings.get(GraphicsSection.Keys.FOV));
        else camera = null;
    }

    public Block activeBlock() {
        return activeBlock;
    }

    public void activeBlock(Block block) {
        activeBlock = block;
    }

    public Camera camera() {
        return camera;
    }

    // Movement

    @Override
    public void direction(Vector2f direction) {
        super.direction(direction);
        updateDirectionalVectors();
    }

    public void processMouseMovement(float xoffset, float yoffset) {
        if (!you) return;

        Map<?, Object> controlsSettings = settings.sectionByClass(ControlsSection.class).values();

        if (controlsSettings == null) return;
        float mouseSensitivity = (float) controlsSettings.get(ControlsSection.Keys.MOUSE_SENSITIVITY);
        xoffset *= mouseSensitivity;
        yoffset *= mouseSensitivity;
        direction(direction().add(new Vector2f(yoffset, xoffset)));

        if (direction().x < -89f) direction(new Vector2f(-89f, direction().y));
        if (direction().x > 89f) direction(new Vector2f(89f, direction().y));
    }

    public void processKeyboard(long window, float deltaTime) {
        if (!you) return;

        Map<?, Object> controlsSettings = settings.sectionByClass(ControlsSection.class).values();
        if (controlsSettings == null) return;
        boolean isSprinting = glfwGetKey(window, (int) controlsSettings.get(ControlsSection.Keys.SPRINT)) == GLFW_PRESS;
        float currentSpeed = MOVE_SPEED * (isSprinting ? SPRINT_MULTIPLIER : 1f);
        float velocity = currentSpeed * deltaTime;
        camera.handleSprintingFOV(isSprinting, deltaTime);
        Vector3f planarForward = new Vector3f(forward.x, 0f, forward.z).normalize();
        Vector3f planarRight = new Vector3f(right.x, 0f, right.z).normalize();
        Vector3f moveDirection = new Vector3f(0f, 0f, 0f);

        if (glfwGetKey(window, (int) controlsSettings.get(ControlsSection.Keys.MOVE_FORWARDS)) == GLFW_PRESS)
            moveDirection.add(planarForward);

        if (glfwGetKey(window, (int) controlsSettings.get(ControlsSection.Keys.MOVE_BACKWARDS)) == GLFW_PRESS)
            moveDirection.sub(planarForward);

        if (glfwGetKey(window, (int) controlsSettings.get(ControlsSection.Keys.MOVE_LEFT)) == GLFW_PRESS)
            moveDirection.sub(planarRight);

        if (glfwGetKey(window, (int) controlsSettings.get(ControlsSection.Keys.MOVE_RIGHT)) == GLFW_PRESS)
            moveDirection.add(planarRight);

        if (moveDirection.length() > 0f) {
            moveDirection.normalize().mul(velocity);
            teleport(position().add(moveDirection));
        }

        if (glfwGetKey(window, (int) controlsSettings.get(ControlsSection.Keys.JUMP)) == GLFW_PRESS)
            teleport(position().add(0f, velocity, 0f));

        if (glfwGetKey(window, (int) controlsSettings.get(ControlsSection.Keys.SNEAK)) == GLFW_PRESS)
            teleport(position().add(0f, -velocity, 0f));

        // Block selection

        if (glfwGetKey(window, (int) controlsSettings.get(ControlsSection.Keys.SELECT_STONE)) == GLFW_PRESS) {
            activeBlock(Floorcraft.blockRegistry().get(StoneBlock.class));
            DiscordRichPresence.updateStatus();
        }

        if (glfwGetKey(window, (int) controlsSettings.get(ControlsSection.Keys.SELECT_DIRT)) == GLFW_PRESS) {
            activeBlock(Floorcraft.blockRegistry().get(DirtBlock.class));
            DiscordRichPresence.updateStatus();
        }

        if (glfwGetKey(window, (int) controlsSettings.get(ControlsSection.Keys.SELECT_GLASS)) == GLFW_PRESS) {
            activeBlock(Floorcraft.blockRegistry().get(GlassBlock.class));
            DiscordRichPresence.updateStatus();
        }

        if (glfwGetKey(window, (int) controlsSettings.get(ControlsSection.Keys.SELECT_LEMON)) == GLFW_PRESS) {
            activeBlock(Floorcraft.blockRegistry().get(LemonBlock.class));
            DiscordRichPresence.updateStatus();
        }

        if (glfwGetKey(window, (int) controlsSettings.get(ControlsSection.Keys.SELECT_HEART)) == GLFW_PRESS) {
            activeBlock(Floorcraft.blockRegistry().get(HeartBlock.class));
            DiscordRichPresence.updateStatus();
        }

        if (glfwGetKey(window, (int) controlsSettings.get(ControlsSection.Keys.SELECT_SEE_SEE)) == GLFW_PRESS) {
            activeBlock(Floorcraft.blockRegistry().get(SeeSeeBlock.class));
            DiscordRichPresence.updateStatus();
        }

        if (glfwGetKey(window, (int) controlsSettings.get(ControlsSection.Keys.SELECT_SKINNED)) == GLFW_PRESS) {
            activeBlock(Floorcraft.blockRegistry().get(SkinnedBlock.class));
            DiscordRichPresence.updateStatus();
        }

        if (glfwGetKey(window, (int) controlsSettings.get(ControlsSection.Keys.SELECT_AGONY)) == GLFW_PRESS) {
            activeBlock(Floorcraft.blockRegistry().get(AgonyBlock.class));
            DiscordRichPresence.updateStatus();
        }

        if (glfwGetKey(window, (int) controlsSettings.get(ControlsSection.Keys.SELECT_PRESERVED_DEITY_HEAD)) == GLFW_PRESS) {
            activeBlock(Floorcraft.blockRegistry().get(PreservedDeityHeadBlock.class));
            DiscordRichPresence.updateStatus();
        }

        if (glfwGetKey(window, (int) controlsSettings.get(ControlsSection.Keys.SELECT_DISTURBED_HEAD)) == GLFW_PRESS) {
            activeBlock(Floorcraft.blockRegistry().get(DisturbedHeadBlock.class));
            DiscordRichPresence.updateStatus();
        }
    }

    private void updateDirectionalVectors() {
        if (!you) return;
        float pitch = direction().x;
        float yaw = direction().y;

        Vector3f newFront = new Vector3f();
        newFront.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        newFront.y = (float) (Math.sin(Math.toRadians(pitch)));
        newFront.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        forward = newFront.normalize();
        right = new Vector3f(forward).cross(WORLD_UP).normalize();
        up = new Vector3f(right).cross(forward).normalize();
    }
}
