package com.boyninja1555.floorcraft.blocks.lib;

import org.jetbrains.annotations.Nullable;

public record StaticBlockDefinition(String name, Integer[][] texture, Boolean transparent, @Nullable String script) {
}
