package com.boyninja1555.floorcraft;

import com.boyninja1555.floorcraft.blocks.*;
import com.boyninja1555.floorcraft.blocks.lib.Block;
import com.boyninja1555.floorcraft.blocks.lib.BlockRegistry;
import com.boyninja1555.floorcraft.entities.Player;
import com.boyninja1555.floorcraft.lib.AssetManager;
import com.boyninja1555.floorcraft.lib.Controls;
import com.boyninja1555.floorcraft.lib.ErrorHandler;
import com.boyninja1555.floorcraft.lib.FpsTracker;
import com.boyninja1555.floorcraft.mesh.UIMesh;
import com.boyninja1555.floorcraft.settings.Settings;
import com.boyninja1555.floorcraft.settings.sections.GraphicsSection;
import com.boyninja1555.floorcraft.texture.atlas.TextureAtlas;
import com.boyninja1555.floorcraft.visual.Font;
import com.boyninja1555.floorcraft.visual.ShaderProgram;
import com.boyninja1555.floorcraft.world.Chunk;
import com.boyninja1555.floorcraft.world.World;
import org.joml.*;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.lang.Math;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Floorcraft {
    private Settings settings;
    private long window;

    private static BlockRegistry blockRegistry;

    private TextureAtlas textures;
    private TextureAtlas uiIcons;
    private Font font;

    private ShaderProgram shader;
    private ShaderProgram uiShader;

    private World world;
    private Player player;

    // UI meshes
    private UIMesh crosshair;
    private UIMesh[] fpsCounter;
    private UIMesh[] coords;

    private final float[] matrixBuffer = new float[16];

    public static BlockRegistry blockRegistry() {
        return blockRegistry;
    }

    public void run() throws Exception {
        settings = new Settings();

        Map<?, Object> graphicsSection = settings.sectionByClass(GraphicsSection.class).values();

        if (graphicsSection == null) ErrorHandler.crash("Missing graphics settings");

        init();
        loop();

        // General cleanup
        shader = null;
        uiShader = null;
        world = null;

        // UI cleanup
        crosshair.cleanup();
        Arrays.stream(fpsCounter).toList().forEach(UIMesh::cleanup);
        Arrays.stream(coords).toList().forEach(UIMesh::cleanup);

        // End
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void defaultBlocks() {
        blockRegistry.register(StoneBlock.class);
        blockRegistry.register(DirtBlock.class);
        blockRegistry.register(GlassBlock.class);
        blockRegistry.register(LemonBlock.class);
    }

    private void init() throws Exception {
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

        if (window == NULL) ErrorHandler.crash("Failed to create the window");

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        GL.createCapabilities();
        glEnable(GL_CULL_FACE);

        player = new Player(settings, new Vector3f(Chunk.WIDTH / 2f + .5f, Chunk.HEIGHT - 19, Chunk.DEPTH / 2f + 5.5f), 0f, true);
        world = new World(player);

        // Window resizing
        glfwSetFramebufferSizeCallback(window, (ignored, w, h) -> {
            width.set(w);
            height.set(h);
            glViewport(0, 0, width.get(), height.get());
            player.camera().updateProjection();
        });

        Controls.register(settings, window, world, player, width, height);

        // Viewport size
        glViewport(0, 0, width.get(), height.get());

        // Shaders
        shader = new ShaderProgram("default.vert", "default.frag");
        uiShader = new ShaderProgram("hud.vert", "hud.frag");

        // Blocks texture atlas
        textures = new TextureAtlas("blocks.png", 8);
        textures.bind();
        shader.uniformInt("uTexture", 0);

        // UI texture atlas
        uiIcons = new TextureAtlas("ui.png", 8);
        font = new Font();

        // Block registration
        blockRegistry = new BlockRegistry(textures);
        defaultBlocks();

        // UI
        crosshair = new UIMesh(uiIcons.region(0, 0));
        fpsCounter = new UIMesh[]{new UIMesh(font.character('0')), new UIMesh(font.character('0')), new UIMesh(font.character('0')), new UIMesh(font.character(' ')), new UIMesh(font.character('f')), new UIMesh(font.character('p')), new UIMesh(font.character('s'))};
        coords = new UIMesh[]{
                new UIMesh(font.character('0')),
                new UIMesh(font.character('0')),
                new UIMesh(font.character(' ')),
                new UIMesh(font.character('0')),
                new UIMesh(font.character('0')),
                new UIMesh(font.character(' ')),
                new UIMesh(font.character('0')),
                new UIMesh(font.character('0')),
        };

        // Meshes
        Block[] chunkBlocks = new Block[Chunk.WIDTH * Chunk.DEPTH * Chunk.HEIGHT];
        Block stone = blockRegistry.get(StoneBlock.class);
        Block dirt = blockRegistry.get(DirtBlock.class);
        Block glass = blockRegistry.get(GlassBlock.class);
        Block lemon = blockRegistry.get(LemonBlock.class);
        Block[] blockTypes = {stone, dirt, lemon};
        Random random = new Random();

        for (int x = 0; x < Chunk.WIDTH; x++) {
            for (int y = 0; y < Chunk.HEIGHT; y++) {
                for (int z = 0; z < Chunk.DEPTH; z++) {
                    int index = x + (y * Chunk.WIDTH) + (z * Chunk.WIDTH * Chunk.HEIGHT);
                    if (y < Chunk.HEIGHT - 20) chunkBlocks[index] = blockTypes[random.nextInt(blockTypes.length)];
                    else {
                        if (x == 8 && z >= 8 && z <= Chunk.DEPTH - 8 || x == Chunk.WIDTH - 8 && z >= 8 && z <= Chunk.DEPTH - 8 || z == 8 && x >= 8 && x <= Chunk.WIDTH - 8 || z == Chunk.DEPTH - 8 && x >= 8 && x <= Chunk.WIDTH - 8)
                            if (y == Chunk.HEIGHT - 1) chunkBlocks[index] = lemon;
                            else chunkBlocks[index] = glass;
                        else if (y == Chunk.HEIGHT - 20 && (x == Chunk.WIDTH / 2 - 1 && z == Chunk.DEPTH / 2 || x == Chunk.WIDTH / 2 - 2 && z == Chunk.DEPTH / 2 || x == Chunk.WIDTH / 2 + 1 && z == Chunk.DEPTH / 2 || x == Chunk.WIDTH / 2 + 2 && z == Chunk.DEPTH / 2))
                            chunkBlocks[index] = dirt;
                        else if (y == Chunk.HEIGHT - 20 && x == Chunk.WIDTH / 2 && z == Chunk.DEPTH / 2)
                            chunkBlocks[index] = stone;
                        else chunkBlocks[index] = null;
                    }
                }
            }
        }

        world.addChunk(new Vector2i(0, 0), chunkBlocks);
        world.addChunk(new Vector2i(1, 0), chunkBlocks);
        world.addChunk(new Vector2i(1, 1), chunkBlocks);
        world.addChunk(new Vector2i(0, 1), chunkBlocks);
        world.refreshMeshes();

//        meshes.add(dirt.toMesh(new Vector3f(-2f, 0f, 0f), new Cube.FaceStates<>(true, true, true, false, true, true)));
//        meshes.add(dirt.toMesh(new Vector3f(-1f, 0f, 0f), new Cube.FaceStates<>(true, true, false, false, true, true)));
//        meshes.add(stone.toMesh(new Vector3f(0f, 0f, 0f), new Cube.FaceStates<>(true, true, false, false, true, true)));
//        meshes.add(dirt.toMesh(new Vector3f(1f, 0f, 0f), new Cube.FaceStates<>(true, true, false, false, true, true)));
//        meshes.add(dirt.toMesh(new Vector3f(2f, 0f, 0f), new Cube.FaceStates<>(true, true, false, true, true, true)));

//        // Floor, ceiling, walls
//        for (int x = 0; x < 21; x++)
//            for (int z = 0; z < 21; z++) {
//                meshes.add(glass.toMesh(new Vector3f(x - 10, -1, z - 1), new Cube.FaceStates<>(false, false, false, false, true, false)));  // Floor
//                meshes.add(lemon.toMesh(new Vector3f(x - 10, 3, z - 1), new Cube.FaceStates<>(false, false, false, false, false, true)));   // Ceiling
//
//                if (x == 0 || x == 20 || z == 0 || z == 20) {
//                    meshes.add(glass.toMesh(new Vector3f(x - 10, 0, z - 1), Cube.FaceStates.FULL_TRUE));  // Bottom
//                    meshes.add(glass.toMesh(new Vector3f(x - 10, 1, z - 1), Cube.FaceStates.FULL_TRUE));  // Middle
//                    meshes.add(glass.toMesh(new Vector3f(x - 10, 2, z - 1), Cube.FaceStates.FULL_TRUE));  // Top
//                }  // Left
//            }
    }

    private void updateWorld(float deltaTime, int uProj, int uView, int uModel) {
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
        uiIcons.bind();

        // Crosshair
        float cSize = 24f;
        float cx = (width / 2f) - (cSize / 2f);
        float cy = (height / 2f) - (cSize / 2f);
        Matrix4f cModel = new Matrix4f().translation(cx, cy, 0f).scale(cSize, cSize, 1f);
        glUniformMatrix4fv(uModel, false, cModel.get(matrixBuffer));
        crosshair.render();

        // FPS counter
        float tSize = 24f;
        float tx = 10f;
        float ty = 10f;
        font.atlas.bind();

        String fps = FpsTracker.to3digits();

        int fi = 0;
        for (UIMesh value : fpsCounter) {
            Matrix4f tModel = new Matrix4f().translation(tx, ty, 0f).scale(tSize, tSize, 1f);
            glUniformMatrix4fv(uModel, false, tModel.get(matrixBuffer));

            value.useAtlasRegion(font.character(fps.charAt(fi)));
            value.render();

            tx += tSize + 5f;
            fi++;
        }

        // Coords
        float coSize = 24f;
        float cox = 10f;
        float coy = tSize + 10f * 2;
        font.atlas.bind();

        String playerPosition = String.format("%02d", Math.round(player.position().x)) + " " + String.format("%02d", Math.round(player.position().y)) + " " + String.format("%02d", Math.round(player.position().z));

        int coi = 0;
        for (UIMesh value : coords) {
            Matrix4f coModel = new Matrix4f().translation(cox, coy, 0f).scale(coSize, coSize, 1f);
            glUniformMatrix4fv(uModel, false, coModel.get(matrixBuffer));

            value.useAtlasRegion(font.character(playerPosition.charAt(coi)));
            value.render();

            cox += coSize + 5f;
            coi++;
        }

        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
    }

    private void loop() {
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

            glfwSwapBuffers(window);
            glfwPollEvents();
            FpsTracker.updateFPS();
        }

        shader.unbind();
    }

    public static void main(String[] args) {
        AssetManager.init().join();

        try {
            new Floorcraft().run();
        } catch (Exception ex) {
            String message = "Could not launch Floorcraft!\n" + ex;
            System.err.println(message);
            ErrorHandler.crash(message);
        }
    }
}
