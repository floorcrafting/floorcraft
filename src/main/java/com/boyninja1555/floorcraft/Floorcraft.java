package com.boyninja1555.floorcraft;

import com.boyninja1555.floorcraft.audio.AudioManager;
import com.boyninja1555.floorcraft.audio.MusicPlayer;
import com.boyninja1555.floorcraft.audio.SoundPlayer;
import com.boyninja1555.floorcraft.blocks.*;
import com.boyninja1555.floorcraft.blocks.lib.BlockRegistry;
import com.boyninja1555.floorcraft.blocks.scripting.BlockScript;
import com.boyninja1555.floorcraft.blocks.scripting.BlockScripts;
import com.boyninja1555.floorcraft.entities.Player;
import com.boyninja1555.floorcraft.lib.*;
import com.boyninja1555.floorcraft.mesh.WorldCage;
import com.boyninja1555.floorcraft.settings.Settings;
import com.boyninja1555.floorcraft.settings.sections.GraphicsSection;
import com.boyninja1555.floorcraft.settings.sections.WorldCreationSection;
import com.boyninja1555.floorcraft.texture.atlas.TextureAtlas;
import com.boyninja1555.floorcraft.ui.hud.HUDManager;
import com.boyninja1555.floorcraft.ui.hud.element.*;
import com.boyninja1555.floorcraft.visual.Font;
import com.boyninja1555.floorcraft.visual.ShaderProgram;
import com.boyninja1555.floorcraft.world.Chunk;
import com.boyninja1555.floorcraft.world.World;
import com.google.gson.Gson;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Floorcraft {
    public static final Gson gson = new Gson();

    private Settings settings;
    private long window;

    private static BlockRegistry blockRegistry;

    // Textures
    private static TextureAtlas textures;
    private static TextureAtlas uiIcons;
    private static Font font;

    // Shaders
    private ShaderProgram shader;
    private ShaderProgram uiShader;
    private ShaderProgram skyShader;
    private ShaderProgram barrierShader;

    // Objects
    private static World world;
    private static Player player;

    // UI meshes
    private static HUDManager hud;

    private final float[] matrixBuffer = new float[16];

    public static BlockRegistry blockRegistry() {
        return blockRegistry;
    }

    public static TextureAtlas textures() {
        return textures;
    }

    public static TextureAtlas uiIcons() {
        return uiIcons;
    }

    public static Font font() {
        return font;
    }

    public static HUDManager hud() {
        return hud;
    }

    public static World world() {
        return world;
    }

    public static Player player() {
        return player;
    }

    public void run() throws Exception {
        settings = new Settings();

        Map<?, Object> graphicsSection = settings.sectionByClass(GraphicsSection.class).values();

        if (graphicsSection == null) ErrorHandler.crash("Missing graphics settings");

        Map<?, Object> worldCreationSection = settings.sectionByClass(WorldCreationSection.class).values();

        if (worldCreationSection == null) ErrorHandler.crash("Missing world creation settings");

        init(graphicsSection, worldCreationSection);
        loop(graphicsSection, worldCreationSection);

        // Shader cleanup
        shader = null;
        uiShader = null;
        skyShader = null;
        barrierShader = null;

        // Cleanup
        world = null;
        WorldCage.cleanup();

        // End
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void defaultBlockScripts() {
        BlockScripts.register(blockRegistry.get(LemonBlock.class), "lemon.lua");
    }

    private void defaultBlocks() {
        blockRegistry.register(NoBlock.class);
        blockRegistry.register(StoneBlock.class);
        blockRegistry.register(DirtBlock.class);
        blockRegistry.register(GlassBlock.class);
        blockRegistry.register(LemonBlock.class);
        blockRegistry.register(HeartBlock.class);
        blockRegistry.register(SeeSeeBlock.class);
        blockRegistry.register(SkinnedBlock.class);
        blockRegistry.register(AgonyBlock.class);
        blockRegistry.register(PreservedDeityHeadBlock.class);
        blockRegistry.register(DisturbedHeadBlock.class);
    }

    private void defaultSounds() {
        SoundPlayer.registerForBlock(SeeSeeBlock.class);
    }

    private void init(Map<?, Object> graphicsSection, Map<?, Object> worldCreationSection) throws Exception {
        // Block registration
        blockRegistry = new BlockRegistry();
        defaultBlocks();
        defaultBlockScripts();

        // Audio registration
        AudioManager.init();
        SoundPlayer.settings(settings);
        MusicPlayer.init(settings);

        // GLFW
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) ErrorHandler.crash("Unable to load GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        Vector2i windowSize = (Vector2i) graphicsSection.get(GraphicsSection.Keys.WINDOW_SIZE);
        AtomicInteger width = new AtomicInteger(windowSize.x);
        AtomicInteger height = new AtomicInteger(windowSize.y);
        window = glfwCreateWindow(width.get(), height.get(), "Floorcraft", NULL, NULL);
        System.out.println("GLFW window created");

        if (window == NULL) ErrorHandler.crash("Failed to create the window");

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        WindowIcon.setWindowIcon(window, "icon.png");
        glfwShowWindow(window);
        System.out.println("GLFW window showing");

        GL.createCapabilities();
        glEnable(GL_CULL_FACE);
        System.out.println("GLFW capabilities now active");

        // Window resizing
        glfwSetFramebufferSizeCallback(window, (ignored, w, h) -> {
            width.set(w);
            height.set(h);
            glViewport(0, 0, width.get(), height.get());
            player.camera().updateProjection();
        });

        // Viewport size
        glViewport(0, 0, width.get(), height.get());

        // Shaders
        shader = new ShaderProgram("default.vert", "default.frag");
        uiShader = new ShaderProgram("hud.vert", "hud.frag");
        skyShader = new ShaderProgram("sky.vert", "sky.frag");
        barrierShader = new ShaderProgram("barrier.vert", "barrier.frag");
        WorldCage.init();
        System.out.println("Shaders loaded");

        // Blocks texture atlas
        textures = new TextureAtlas("blocks.png", 8);
        textures.bind();
        shader.uniformInt("uTexture", 0);
        blockRegistry.loadCustomBlocks();
        defaultSounds();
        System.out.println("Block atlas loaded");

        // Player & world
        player = new Player(settings, new Vector3f(Chunk.WIDTH / 2f + .5f, Chunk.HEIGHT - 5, Chunk.DEPTH / 2f + 5.5f), 0f, true);
        world = new World(player);
        Controls.register(window, world, player, width, height);
        System.out.println("Player & world created (world not generated yet)");

        // UI texture atlas
        uiIcons = new TextureAtlas("ui.png", 8);
        font = new Font();
        System.out.println("UI atlas loaded");

        // HUD
        hud = new HUDManager();
        hud.newElement(HUDCrosshair.class);
        hud.newElement(HUDFpsText.class);
        hud.newElement(HUDCoordinatesText.class);
        hud.newElement(HUDActiveBlock.class);
        hud.newElement(HUDActiveBlockText.class);
        System.out.println("HUD elements created");

        // Chunks
        Block[] chunkBlocks = new Block[Chunk.WIDTH * Chunk.DEPTH * Chunk.HEIGHT];
        Block stone = blockRegistry.get(StoneBlock.class);
        Block dirt = blockRegistry.get(DirtBlock.class);
        System.out.println("Creating world chunks...");

        int airHeight = 32;
        for (int x = 0; x < Chunk.WIDTH; x++) {
            for (int y = 0; y < Chunk.HEIGHT; y++) {
                for (int z = 0; z < Chunk.DEPTH; z++) {
                    int index = x + (y * Chunk.WIDTH) + (z * Chunk.WIDTH * Chunk.HEIGHT);
                    if (y < Chunk.HEIGHT - airHeight - 4) chunkBlocks[index] = stone;
                    else if (y < Chunk.HEIGHT - airHeight) chunkBlocks[index] = dirt;
                    else chunkBlocks[index] = null;
                    // else {
                    //     if (x == 8 && z >= 8 && z <= Chunk.DEPTH - 8 || x == Chunk.WIDTH - 8 && z >= 8 && z <= Chunk.DEPTH - 8 || z == 8 && x >= 8 && x <= Chunk.WIDTH - 8 || z == Chunk.DEPTH - 8 && x >= 8 && x <= Chunk.WIDTH - 8)
                    //         if (y == Chunk.HEIGHT - 1) chunkBlocks[index] = lemon;
                    //         else chunkBlocks[index] = glass;
                    //     else if (y == Chunk.HEIGHT - 20 && (x == Chunk.WIDTH / 2 - 1 && z == Chunk.DEPTH / 2 || x == Chunk.WIDTH / 2 - 2 && z == Chunk.DEPTH / 2 || x == Chunk.WIDTH / 2 + 1 && z == Chunk.DEPTH / 2 || x == Chunk.WIDTH / 2 + 2 && z == Chunk.DEPTH / 2))
                    //         chunkBlocks[index] = dirt;
                    //     else if (y == Chunk.HEIGHT - 20 && x == Chunk.WIDTH / 2 && z == Chunk.DEPTH / 2)
                    //         chunkBlocks[index] = stone;
                    //     else chunkBlocks[index] = null;
                    // }
                }
            }
        }

        System.out.println("Chunk blocks created");
        Vector2i worldDimensions = (Vector2i) worldCreationSection.get(WorldCreationSection.Keys.WORLD_DIMENSIONS);
        Map<Vector2i, Block[]> chunks = new HashMap<>();

        for (int x = 0; x < worldDimensions.x; x++)
            for (int z = 0; z < worldDimensions.y; z++)
                chunks.put(new Vector2i(x, z), chunkBlocks);

        world.init(chunks);
        System.out.println("World generated");
        DiscordRichPresence.updateStatus();

        if (world.justCreated()) {
            player.teleport(new Vector3f((worldDimensions.x * Chunk.WIDTH) / 2f, Chunk.HEIGHT - airHeight + 2, (worldDimensions.x * Chunk.DEPTH) / 2f));
            world.save();
        }
    }

    private void renderSky(Vector3f color) {
        glDepthFunc(GL_LEQUAL);
        glDepthMask(false);
        glDisable(GL_CULL_FACE);

        skyShader.bind();
        skyShader.uniformVec3f("uSkyColor", color);
        glUniformMatrix4fv(skyShader.uniformLocation("uProjection"), false, player.camera().projection().get(matrixBuffer));
        glUniformMatrix4fv(skyShader.uniformLocation("uView"), false, player.camera().view().get(matrixBuffer));
        WorldCage.render();

        glEnable(GL_CULL_FACE);
        glDepthMask(true);
        glDepthFunc(GL_LESS);
    }

    private void updateWorld(Map<?, Object> graphicsSection, float deltaTime, int uProj, int uView, int uModel) {
        world.tick(deltaTime);

        // Movement updates
        player.processKeyboard(window, deltaTime);
        player.camera().updateView();

        // Rendering
        renderSky((Vector3f) graphicsSection.get(GraphicsSection.Keys.SKY_COLOR));
        textures.bind();
        shader.bind();
        glUniformMatrix4fv(uProj, false, player.camera().projection().get(matrixBuffer));
        glUniformMatrix4fv(uView, false, player.camera().view().get(matrixBuffer));
        world.render(uModel, matrixBuffer);
    }

    private void renderUI(int uProj, int uModel) {
        int[] w = new int[1];
        int[] h = new int[1];
        glfwGetWindowSize(window, w, h);

        int width = w[0];
        int height = h[0];
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        uiShader.bind();
        Matrix4f ortho = new Matrix4f().ortho(0f, width, height, 0f, -1f, 1f);
        glUniformMatrix4fv(uProj, false, ortho.get(matrixBuffer));
        hud.render(matrixBuffer, uModel, new Vector2i(width, height), 10f, 15f);

        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
    }

    private void renderBarrier(Vector2i worldDimensions) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);
        glDisable(GL_CULL_FACE);
        glDepthMask(false);
        barrierShader.bind();

        Vector3f min = new Vector3f(-.1f, -.1f, -.1f);
        Vector3f max = new Vector3f(Chunk.WIDTH * worldDimensions.x + .1f, Chunk.HEIGHT + .1f, Chunk.DEPTH * worldDimensions.y + .1f);

        Vector3f center = new Vector3f(min);
        center.add(max);
        center.div(2f);

        Vector3f size = new Vector3f(max);
        size.sub(min);

        Matrix4f model = new Matrix4f().translation(center).scale(size);
        glUniformMatrix4fv(barrierShader.uniformLocation("uProjection"), false, player.camera().projection().get(matrixBuffer));
        glUniformMatrix4fv(barrierShader.uniformLocation("uView"), false, player.camera().view().get(matrixBuffer));
        glUniformMatrix4fv(barrierShader.uniformLocation("uModel"), false, model.get(matrixBuffer));

        barrierShader.uniformVec3f("uMinBound", min);
        barrierShader.uniformVec3f("uMaxBound", max);
        barrierShader.uniformVec3f("uPlayerPosition", player.position());
        barrierShader.uniformFloat("uTime", (float) glfwGetTime());
        WorldCage.render();

        glDepthMask(true);
        glEnable(GL_CULL_FACE);
        glDisable(GL_BLEND);
    }

    private void loop(Map<?, Object> graphicsSection, Map<?, Object> worldCreationSection) {
        boolean outlineEnabled = false;
        try {
            BlockOutline.init(new ShaderProgram("outline.vert", "outline.frag", "outline.geom"));
            outlineEnabled = true;
        } catch (Exception ex) {
            String message = "Could not enable block outline! It will not show.\n" + ex;
            System.err.println(message);
            ErrorHandler.error(message);
        }

        AtomicReference<Float> lastFrame = new AtomicReference<>(0f);
        int uProj = shader.uniformLocation("uProjection");
        int uView = shader.uniformLocation("uView");
        int uModel = shader.uniformLocation("uModel");
        int uiUProj = uiShader.uniformLocation("uProjection");
        int uiUModel = uiShader.uniformLocation("uModel");
        while (!glfwWindowShouldClose(window)) {
            glClearColor(0f, 0f, 0f, 1f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            float currentTime = (float) glfwGetTime();
            float deltaTime = currentTime - lastFrame.get();
            lastFrame.set(currentTime);

            player.tick(deltaTime);
            updateWorld(graphicsSection, deltaTime, uProj, uView, uModel);
            renderUI(uiUProj, uiUModel);
            renderBarrier((Vector2i) worldCreationSection.get(WorldCreationSection.Keys.WORLD_DIMENSIONS));

            if (outlineEnabled) {
                Vector3i selectedBlock = world.raycast(player.position(), player.forward, 5f, false);
                BlockOutline.render(selectedBlock, player.camera().projection(), player.camera().view(), .01f, 0f, 0f, 0f);
            }

            glfwSwapBuffers(window);
            glfwPollEvents();
            FpsTracker.updateFPS();
            DiscordRichPresence.tick();
        }

        shader.unbind();
        BlockOutline.cleanup();
        SoundPlayer.cleanup();
        MusicPlayer.cleanup();
        DiscordRichPresence.stop();
    }

    public static void main(String[] args) {
        AppProperties.load();
        AssetManager.init().join();
        BlockKeybinds.init();
        DiscordRichPresence.init();

        try {
            new Floorcraft().run();
        } catch (Exception ex) {
            ex.printStackTrace();

            String message = "Could not launch Floorcraft!\n" + ex;
            System.err.println(message);
            ErrorHandler.crash(message);
        }
    }
}
