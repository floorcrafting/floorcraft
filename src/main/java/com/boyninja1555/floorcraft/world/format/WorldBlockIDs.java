package com.boyninja1555.floorcraft.world.format;

import com.boyninja1555.floorcraft.Floorcraft;
import com.boyninja1555.floorcraft.blocks.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class WorldBlockIDs {
    private static final int CUSTOM_ID_OFFSET = 1_000;
    private static final int CUSTOM_ID_SPAN = Integer.MAX_VALUE - CUSTOM_ID_OFFSET;
    private static final Map<Class<? extends Block>, Integer> builtInClassToId = new HashMap<>();
    private static final Map<Integer, Class<? extends Block>> builtInIdToClass = new HashMap<>();
    private static final Map<String, Integer> identifierToId = new HashMap<>();
    private static final Map<Integer, String> idToIdentifier = new HashMap<>();

    static {
        registerBuiltIn(NoBlock.class, "none", -1);
        registerBuiltIn(StoneBlock.class, "stone", 1);
        registerBuiltIn(DirtBlock.class, "dirt", 2);
        registerBuiltIn(GlassBlock.class, "glass", 3);
        registerBuiltIn(LemonBlock.class, "lemon", 4);
        registerBuiltIn(HeartBlock.class, "heart", 5);
        registerBuiltIn(SeeSeeBlock.class, "see_see", 6);
        registerBuiltIn(SkinnedBlock.class, "skinned", 7);
        registerBuiltIn(AgonyBlock.class, "agony", 8);
        registerBuiltIn(PreservedDeityHeadBlock.class, "preserved_deity_head", 9);
        registerBuiltIn(DisturbedHeadBlock.class, "disturbed_head", 10);
    }

    private static void registerBuiltIn(Class<? extends Block> type, String identifier, int id) {
        builtInClassToId.put(type, id);
        builtInIdToClass.put(id, type);
        identifierToId.put(identifier, id);
        idToIdentifier.put(id, identifier);
    }

    private static int hashedCustomId(String identifier) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(identifier.getBytes(StandardCharsets.UTF_8));
            int value = ((digest[0] & 0x7F) << 24)
                    | ((digest[1] & 0xFF) << 16)
                    | ((digest[2] & 0xFF) << 8)
                    | (digest[3] & 0xFF);
            return CUSTOM_ID_OFFSET + Math.floorMod(value, CUSTOM_ID_SPAN);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("SHA-256 algorithm is unavailable", ex);
        }
    }

    public static synchronized int registerCustomIdentifier(String identifier) {
        Integer current = identifierToId.get(identifier);
        if (current != null) return current;

        int id = hashedCustomId(identifier);
        String existingIdentifier = idToIdentifier.get(id);
        if (existingIdentifier != null && !existingIdentifier.equals(identifier)) {
            String message = "Custom block ID hash collision between \"" + identifier + "\" and \""
                    + existingIdentifier + "\". Skipping the new block.";
            System.err.println(message);
            return -1;
        }

        identifierToId.put(identifier, id);
        idToIdentifier.put(id, identifier);
        return id;
    }

    public static synchronized Integer idFromIdentifier(String identifier) {
        return identifierToId.get(identifier);
    }

    public static synchronized String identifierFromId(int id) {
        return idToIdentifier.get(id);
    }

    public static int idFromBlock(Class<? extends Block> block) {
        Integer builtInId = builtInClassToId.get(block);
        if (builtInId != null) return builtInId;

        if (Floorcraft.blockRegistry() == null) return -1;
        Block instance = Floorcraft.blockRegistry().get(block);
        if (instance == null) return -1;
        return idFromBlock(instance);
    }

    public static int idFromBlock(Block block) {
        if (block == null) return -1;
        Integer id = idFromIdentifier(block.identifier());
        if (id != null) return id;
        return registerCustomIdentifier(block.identifier());
    }

    public static Class<? extends Block> blockClassFromId(int id) {
        return builtInIdToClass.get(id);
    }

    public static Block blockFromId(int id) {
        if (id == -1 || Floorcraft.blockRegistry() == null) return null;

        Class<? extends Block> blockClass = blockClassFromId(id);
        if (blockClass != null) return Floorcraft.blockRegistry().get(blockClass);

        String identifier = identifierFromId(id);
        if (identifier == null) return null;
        return Floorcraft.blockRegistry().get(identifier);
    }

    public static synchronized Map<Class<? extends Block>, Integer> all() {
        return new HashMap<>(builtInClassToId);
    }
}
