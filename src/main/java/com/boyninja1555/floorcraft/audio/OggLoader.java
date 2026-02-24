package com.boyninja1555.floorcraft.audio;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;

public class OggLoader {

    public static int load(String path) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer channels = stack.mallocInt(1);
            IntBuffer sampleRate = stack.mallocInt(1);
            ShortBuffer raw = stb_vorbis_decode_filename(path, channels, sampleRate);

            if (raw == null) return -1;
            int format = (channels.get(0) == 1) ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16;
            int buffer = alGenBuffers();
            alBufferData(buffer, format, raw, sampleRate.get(0));
            MemoryUtil.memFree(raw);
            return buffer;
        }
    }
}
