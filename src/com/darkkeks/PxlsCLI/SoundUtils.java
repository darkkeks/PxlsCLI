package com.darkkeks.PxlsCLI;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.IOException;
import java.io.InputStream;

public class SoundUtils {

    public static final String NOTIFY = "/notify.wav";

    public static void alert() {
        playSound(NOTIFY);
    }

    private static void playSound(String filename) {
        if(!PxlsCLI.config.getBool("main", "soundEnabled"))
            return;
        try {
            InputStream inputStream = PxlsCLI.class.getResourceAsStream(filename);
            AudioStream audioStream = new AudioStream(inputStream);
            AudioPlayer.player.start(audioStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
