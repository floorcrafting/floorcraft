package com.boyninja1555.floorcraft.blocks.scripting;

import com.boyninja1555.floorcraft.audio.SoundPlayer;
import com.boyninja1555.floorcraft.lib.ErrorHandler;
import com.boyninja1555.floorcraft.world.tick.WorldTicker;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

public class BlockScript {
    private final @NotNull String name;
    private @NotNull BlockUpdateCallback onPlace;
    private @NotNull BlockUpdateCallback onBreak;
    private @NotNull BlockUpdateCallback onTick;

    public @NotNull String name() {
        return name;
    }

    public @NotNull BlockUpdateCallback onPlace() {
        return onPlace;
    }

    public void onPlace(@NotNull BlockUpdateCallback onPlace) {
        this.onPlace = onPlace;
    }

    public @NotNull BlockUpdateCallback onBreak() {
        return onBreak;
    }

    public void onBreak(@NotNull BlockUpdateCallback onBreak) {
        this.onBreak = onBreak;
    }

    public @NotNull BlockUpdateCallback onTick() {
        return onTick;
    }

    public void onTick(@NotNull BlockUpdateCallback onTick) {
        this.onTick = onTick;
    }

    public BlockScript(@NotNull String name, @NotNull BlockUpdateCallback onPlace, @NotNull BlockUpdateCallback onBreak, @NotNull BlockUpdateCallback onTick) {
        this.name = name;
        this.onPlace = onPlace;
        this.onBreak = onBreak;
        this.onTick = onTick;
    }

    public BlockScript(@NotNull String name) {
        this(name, (ignored, ignored1, ignored2) -> {
        }, (ignored, ignored1, ignored2) -> {
        }, (ignored, ignored1, ignored2) -> {
        });
    }

    public void init(String blockName, String scriptContent) throws LuaError {
        Globals globals = JsePlatform.standardGlobals();

        // Math
        globals.set("vec3int", CoerceJavaToLua.coerce(Vector3i.class));
        globals.set("vec3", CoerceJavaToLua.coerce(Vector3d.class));

        // Engine
        globals.set("sounds", CoerceJavaToLua.coerce(SoundPlayer.class));
        globals.set("worldticker", CoerceJavaToLua.coerce(WorldTicker.class));

        globals.load(scriptContent).call();
        onPlace = createUpdateCallback(blockName, globals, "on_place");
        onBreak = createUpdateCallback(blockName, globals, "on_break");
        onTick = createUpdateCallback(blockName, globals, "on_tick");
    }

    private BlockUpdateCallback createUpdateCallback(String blockName, Globals globals, String functionName) {
        LuaValue func = globals.get(functionName);

        if (func.isfunction()) return (world, position, deltaTime) -> {
            try {
                func.call(CoerceJavaToLua.coerce(world), CoerceJavaToLua.coerce(position), CoerceJavaToLua.coerce(deltaTime));
            } catch (LuaError ex) {
                String message = "Could not run " + blockName + " block's script!\n" + ex;
                System.err.println(message);
                ErrorHandler.error(message);
            }
        };

        return (world, position, deltaTime) -> {
        };
    }
}
