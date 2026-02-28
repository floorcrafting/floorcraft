package com.boyninja1555.floorcraft.blocks.lib;

import com.boyninja1555.floorcraft.Floorcraft;
import com.boyninja1555.floorcraft.blocks.Block;
import com.boyninja1555.floorcraft.blocks.CustomBlock;
import com.boyninja1555.floorcraft.blocks.NoBlock;
import com.boyninja1555.floorcraft.blocks.scripting.BlockScripts;
import com.boyninja1555.floorcraft.lib.AssetManager;
import com.boyninja1555.floorcraft.lib.ErrorHandler;
import com.boyninja1555.floorcraft.texture.atlas.TextureAtlas;
import com.boyninja1555.floorcraft.world.format.WorldBlockIDs;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class BlockRegistry {
    private final List<Class<? extends Block>> classBlocks;
    private final Map<Class<? extends Block>, Block> classCache;
    private final Map<String, Block> customBlocks;
    private final Map<String, Block> identifierCache;

    public BlockRegistry() {
        classBlocks = new ArrayList<>();
        classCache = new HashMap<>();
        customBlocks = new HashMap<>();
        identifierCache = new HashMap<>();
    }

    public void register(Class<? extends Block> block) {
        classBlocks.add(block);
    }

    public Block get(Class<? extends Block> blockClass) {
        if (classCache.containsKey(blockClass)) return classCache.get(blockClass);
        if (!classBlocks.contains(blockClass)) return null;
        try {
            Block block = blockClass.getConstructor(TextureAtlas.class).newInstance(Floorcraft.textures());
            if (block.cacheEnabled()) classCache.put(blockClass, block);
            identifierCache.put(block.identifier(), block);
            return block;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException ex) {
            ErrorHandler.crash("Could not load " + blockClass.getName() + " block!\n" + ex);
            return null;
        }
    }

    public Block get(String identifier) {
        Block cached = identifierCache.get(identifier);
        if (cached != null) return cached;
        for (Class<? extends Block> blockClass : classBlocks) {
            Block block = get(blockClass);
            if (block == null || !block.identifier().equals(identifier)) continue;
            return block;
        }

        return customBlocks.get(identifier);
    }

    public Block getById(int id) {
        if (id == -1) return null;
        String identifier = WorldBlockIDs.identifierFromId(id);
        if (identifier == null) return get(NoBlock.class);
        return get(identifier);
    }

    public List<Block> getAll() {
        List<Block> blocks = new ArrayList<>();
        for (Class<? extends Block> blockClass : classBlocks) {
            Block block = get(blockClass);
            if (block != null) blocks.add(block);
        }

        customBlocks.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).forEach(blocks::add);
        return blocks;
    }

    public void loadScripts() {
        for (Block block : classCache.values()) {
            if (block.definition().script() == null) continue;

            String scriptPath = block.definition().script();
            BlockScripts.register(block, scriptPath);
        }
    }

    public void loadCustomBlocks() {
        if (Floorcraft.textures() == null) return;

        Path blocksPath = AssetManager.blocksPath();
        if (!Files.isDirectory(blocksPath)) return;

        Set<String> builtInIdentifiers = new HashSet<>();
        for (Class<? extends Block> blockClass : classBlocks) {
            Block block = get(blockClass);
            if (block != null) builtInIdentifiers.add(block.identifier());
        }

        List<Path> definitions;
        try (var stream = Files.list(blocksPath)) {
            definitions = stream.filter(path -> path.getFileName().toString().endsWith(".json")).sorted(Comparator.comparing(path -> path.getFileName().toString())).toList();
        } catch (IOException ex) {
            ErrorHandler.error("Could not read custom block definitions!\n" + ex);
            return;
        }

        for (Path definition : definitions) {
            String filename = definition.getFileName().toString();
            String identifier = filename.substring(0, filename.length() - 5);
            if (builtInIdentifiers.contains(identifier)) continue;
            if (customBlocks.containsKey(identifier)) continue;
            if (!definitionExistsAndValid(definition, identifier)) continue;
            int id = WorldBlockIDs.registerCustomIdentifier(identifier);
            if (id < 0) continue;

            Block block = new CustomBlock(Floorcraft.textures(), identifier);
            customBlocks.put(identifier, block);
            identifierCache.put(identifier, block);
            System.out.println("Loaded custom block \"" + identifier + "\" as ID " + id);
        }
    }

    private boolean definitionExistsAndValid(Path path, String identifier) {
        try (FileReader reader = new FileReader(path.toFile())) {
            StaticBlockDefinition definition = Floorcraft.gson.fromJson(reader, StaticBlockDefinition.class);
            if (definition == null) throw new IOException("Definition is empty");
            if (definition.texture() == null) throw new IOException("Missing \"texture\" field");
            if (definition.transparent() == null) throw new IOException("Missing \"transparent\" field");
            if (definition.texture().length != 6) throw new IOException("\"texture\" must contain 6 faces");

            for (Integer[] face : definition.texture()) {
                if (face == null || face.length != 2 || face[0] == null || face[1] == null)
                    throw new IOException("Each texture face must have exactly 2 numbers");
            }

            return true;
        } catch (IOException | RuntimeException ex) {
            ErrorHandler.error("Could not load custom block definition \"" + identifier + "\"!\n" + ex);
            return false;
        }
    }
}
