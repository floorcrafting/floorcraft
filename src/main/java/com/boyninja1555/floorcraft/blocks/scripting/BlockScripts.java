package com.boyninja1555.floorcraft.blocks.scripting;

import com.boyninja1555.floorcraft.blocks.Block;
import com.boyninja1555.floorcraft.lib.AssetManager;
import com.boyninja1555.floorcraft.lib.ErrorHandler;
import com.boyninja1555.floorcraft.world.World;
import org.joml.Vector3i;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class BlockScripts {
    private static final Map<Block, BlockScript> scripts = new HashMap<>();

    public static void register(Block block, String scriptPath) {
        try {
            Path path = AssetManager.blockScriptsPath().resolve(scriptPath);
            if (!Files.exists(path) || Files.isDirectory(path)) throw new IOException(path + " does not exist");

            BlockScript script = new BlockScript(scriptPath);
            if (!script.init(Files.readString(path))) throw new Exception();
            scripts.put(block, script);
        } catch (Exception ex) {
            ErrorHandler.crash("Could not load " + block.definition().name() + " block's script!\n" + ex);
        }
    }

    public static boolean hasScript(Block block) {
        return scripts.containsKey(block);
    }

    public static void onPlace(Block block, World world, Vector3i position) {
        if (!hasScript(block)) return;
        scripts.get(block).onPlace().run(world, position);
    }

    public static void onBreak(Block block, World world, Vector3i position) {
        if (!hasScript(block)) return;
        scripts.get(block).onBreak().run(world, position);
    }

    public static void onTick(Block block, World world, Vector3i position) {
        if (!hasScript(block)) return;
        scripts.get(block).onTick().run(world, position);
    }
}
