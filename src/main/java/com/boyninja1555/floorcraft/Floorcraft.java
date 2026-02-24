package com.boyninja1555.floorcraft;

import com.boyninja1555.floorcraft.audio.AudioManager;
import com.boyninja1555.floorcraft.audio.MusicPlayer;
import com.boyninja1555.floorcraft.audio.SoundPlayer;
import com.boyninja1555.floorcraft.blocks.*;
import com.boyninja1555.floorcraft.blocks.lib.BlockRegistry;
import com.boyninja1555.floorcraft.entities.Player;
import com.boyninja1555.floorcraft.lib.*;
import com.boyninja1555.floorcraft.settings.Settings;
import com.boyninja1555.floorcraft.settings.sections.GraphicsSection;
import com.boyninja1555.floorcraft.texture.atlas.TextureAtlas;
import com.boyninja1555.floorcraft.ui.hud.HUDManager;
import com.boyninja1555.floorcraft.ui.hud.element.*;
import com.boyninja1555.floorcraft.visual.Font;
import com.boyninja1555.floorcraft.visual.ShaderProgram;
import com.boyninja1555.floorcraft.world.Chunk;
import com.boyninja1555.floorcraft.world.World;
import com.google.gson.Gson;
import org.joml.*;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

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

        init();
        loop();

        // Cleanup
        shader = null;
        uiShader = null;
        world = null;

        // End
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void defaultBlocks() {
        blockRegistry.register(CustomBlock.class);
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

    private void init() throws Exception {
        // Block registration
        blockRegistry = new BlockRegistry();
        defaultBlocks();

        // Audio registration
        AudioManager.init();
        SoundPlayer.settings(settings);
        MusicPlayer.init(settings);
        defaultSounds();

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
        Map<?, Object> graphicsSection = settings.sectionByClass(GraphicsSection.class).values();

        if (graphicsSection == null) ErrorHandler.crash("Missing graphics settings");

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
        System.out.println("Shaders loaded");

        // Blocks texture atlas
        textures = new TextureAtlas("blocks.png", 8);
        textures.bind();
        shader.uniformInt("uTexture", 0);
        System.out.println("Block atlas loaded");

        // Player & world
        player = new Player(settings, new Vector3f(Chunk.WIDTH / 2f + .5f, Chunk.HEIGHT - 19, Chunk.DEPTH / 2f + 5.5f), 0f, true);
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

        for (int x = 0; x < Chunk.WIDTH; x++) {
            for (int y = 0; y < Chunk.HEIGHT; y++) {
                for (int z = 0; z < Chunk.DEPTH; z++) {
                    int index = x + (y * Chunk.WIDTH) + (z * Chunk.WIDTH * Chunk.HEIGHT);
                    if (y < Chunk.HEIGHT - 24) chunkBlocks[index] = stone;
                    else if (y < Chunk.HEIGHT - 20) chunkBlocks[index] = dirt;
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

        world.init(Map.of(new Vector2i(0, 0), chunkBlocks.clone(), new Vector2i(1, 0), chunkBlocks.clone(), new Vector2i(1, 1), chunkBlocks.clone(), new Vector2i(0, 1), chunkBlocks.clone()));

        System.out.println("World generated");
        DiscordRichPresence.updateStatus();
    }

    private void updateWorld(float deltaTime, int uProj, int uView, int uModel) {
        world.tick(deltaTime);

        // Movement updates
        player.processKeyboard(window, deltaTime);
        player.camera().updateView();
        textures.bind();

        // Rendering
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

    private void loop() {
        boolean outlineEnabled = false;
        try {
            BlockOutline.init(new ShaderProgram("outline.vert", "outline.frag", "outline.geom"));
            outlineEnabled = true;
        } catch (Exception ex) {
            String message = "Could not enable block outline! It will not show.\n" + ex;
            System.err.println(message);
            ErrorHandler.error(message);
        }

        Map<?, Object> graphicsSection = settings.sectionByClass(GraphicsSection.class).values();

        if (graphicsSection == null) ErrorHandler.crash("Missing graphics settings");

        Vector4f skyColor = (Vector4f) graphicsSection.get(GraphicsSection.Keys.SKY_COLOR);
        AtomicReference<Float> lastFrame = new AtomicReference<>(0f);
        int uProj = shader.uniformLocation("uProjection");
        int uView = shader.uniformLocation("uView");
        int uModel = shader.uniformLocation("uModel");
        int uiUProj = uiShader.uniformLocation("uProjection");
        int uiUModel = uiShader.uniformLocation("uModel");
        while (!glfwWindowShouldClose(window)) {
            glClearColor(skyColor.x, skyColor.y, skyColor.z, skyColor.w);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            float currentTime = (float) glfwGetTime();
            float deltaTime = currentTime - lastFrame.get();
            lastFrame.set(currentTime);

            player.tick(deltaTime);
            updateWorld(deltaTime, uProj, uView, uModel);
            renderUI(uiUProj, uiUModel);

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
