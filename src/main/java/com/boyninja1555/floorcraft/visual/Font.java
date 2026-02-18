package com.boyninja1555.floorcraft.visual;

import com.boyninja1555.floorcraft.texture.atlas.AtlasRegion;
import com.boyninja1555.floorcraft.texture.atlas.TextureAtlas;

import java.util.ArrayList;
import java.util.List;

public class Font {
    public final TextureAtlas atlas;

    public Font() {
        atlas = new TextureAtlas("font.png", 8);
        atlas.bind();
    }

    public AtlasRegion character(char c) {
        return switch (c) {
            case '0' -> atlas.region(1, 0);
            case '1' -> atlas.region(2, 0);
            case '2' -> atlas.region(3, 0);
            case '3' -> atlas.region(4, 0);
            case '4' -> atlas.region(5, 0);
            case '5' -> atlas.region(6, 0);
            case '6' -> atlas.region(7, 0);
            case '7' -> atlas.region(8, 0);
            case '8' -> atlas.region(9, 0);
            case '9' -> atlas.region(10, 0);
            case 'a' -> atlas.region(11, 0);
            case 'b' -> atlas.region(12, 0);
            case 'c' -> atlas.region(13, 0);
            case 'd' -> atlas.region(14, 0);
            case 'e' -> atlas.region(15, 0);
            case 'f' -> atlas.region(0, 1);
            case 'g' -> atlas.region(1, 1);
            case 'h' -> atlas.region(2, 1);
            case 'i' -> atlas.region(3, 1);
            case 'j' -> atlas.region(4, 1);
            case 'k' -> atlas.region(5, 1);
            case 'l' -> atlas.region(6, 1);
            case 'm' -> atlas.region(7, 1);
            case 'n' -> atlas.region(8, 1);
            case 'o' -> atlas.region(9, 1);
            case 'p' -> atlas.region(10, 1);
            case 'q' -> atlas.region(11, 1);
            case 'r' -> atlas.region(12, 1);
            case 's' -> atlas.region(13, 1);
            case 't' -> atlas.region(14, 1);
            case 'u' -> atlas.region(15, 1);
            case 'v' -> atlas.region(0, 2);
            case 'w' -> atlas.region(1, 2);
            case 'x' -> atlas.region(2, 2);
            case 'y' -> atlas.region(3, 2);
            case 'z' -> atlas.region(4, 2);
            default -> atlas.region(0, 0);
        };
    }

    public List<AtlasRegion> string(String string) {
        List<AtlasRegion> regions = new ArrayList<>();

        for (char c : string.toCharArray())
            regions.add(character(c));

        return regions;
    }
}
