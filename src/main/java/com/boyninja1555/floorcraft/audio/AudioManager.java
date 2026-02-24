package com.boyninja1555.floorcraft.audio;

import com.boyninja1555.floorcraft.lib.ErrorHandler;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC10.alcOpenDevice;

public class AudioManager {

    public static void init() {
        try {
            long device = alcOpenDevice((ByteBuffer) null);

            if (device == 0) throw new Exception("Failed to open the default audio device");

            ALCCapabilities deviceCaps = ALC.createCapabilities(device);
            long context = ALC10.alcCreateContext(device, (IntBuffer) null);
            if (context == 0) throw new Exception("Failed to create OpenAL context");

            alcMakeContextCurrent(context);
            AL.createCapabilities(deviceCaps);
        } catch (Exception ex) {
            String message = "Could not initialize audio!\n" + ex;
            System.err.println(message);
            ErrorHandler.crash(message);
        }
    }
}
