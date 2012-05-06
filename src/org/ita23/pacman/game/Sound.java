package org.ita23.pacman.game;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.net.URL;

/**
 * Represents a single sound which should be played when a defined event occurs.
 * @author Lukas Knuth
 * @version 1.0
 */
public class Sound {

    /** The audio-stream to read the sound from */
    private final AudioInputStream audo_in;
    /** The given event-name to trigger this sound */
    private final String event;

    /**
     * Create a new {@code Sound}, which can the be added to the {@code SoundManger}
     *  for playback.
     * @param event the name of the event, used to trigger this sound.
     * @param sound_res the audio-resource of this sound.
     * @throws IllegalArgumentException if the give URL couldn't be used to get
     *  a working audio input.
     * @see org.ita23.pacman.game.SoundManager#addSound(Sound)
     */
    public Sound(String event, URL sound_res){
        try {
            audo_in = AudioSystem.getAudioInputStream(sound_res);
            this.event = event;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Returns the given event-name.
     * @return the given event-name.
     */
    String getEventName(){
        return this.event;
    }

    /**
     * Get the {@code AudioInputStream} for this {@code Sound}, which is then used
     *  for playback.</p>
     * The returned {@code AudioInputStream} is guaranteed to be valid!</p>
     * This method is <u>not for direct use</u> and should only be accessed by the
     *  {@code SoundManager}-class.
     * @return the input-stream to read the sound from.
     */
    AudioInputStream getInputStream(){
        return this.audo_in;
    }
}
