package org.ita23.pacman.game;

import javax.sound.sampled.Clip;

/**
 * Represents a single sound which should be played when a defined event occurs.
 * @author Lukas Knuth
 * @version 1.0
 */
public class Sound {

    /** The audio-stream to read the sound from */
    private Clip audio;
    /** The given event-name to trigger this sound */
    private final String event;
    /** How often this sound should be looped. */
    private int loop_cycles;

    /**
     * Create a new {@code Sound}, which can the be added to the {@code SoundManger}
     *  for playback.
     * @param event the name of the event, used to trigger this sound.
     * @param sound_resource_path the audio-resource of this sound.
     * @throws IllegalArgumentException if the give URL couldn't be used to get
     *  a working audio input.
     * @see org.ita23.pacman.game.SoundManager#addSound(Sound)
     */
    public Sound(String event, String sound_resource_path){
        this.event = event;
        this.audio = ResourceLoader.loadSound(sound_resource_path);
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
    Clip getAudioClip(){
        return this.audio;
    }

    /**
     * Get the count of cycles this sound should loop.</p>
     * This might return an actual number {@code > 0}, which indicates the actual
     *  count of cycles, the exact value of {@code 0} telling you that this sound
     *  isn't looping at all, or a number {@code < 0} to indicate, that it's looping
     *  forever.
     * @return the amount of cycles this sound is looping, exactly {@code 0}or a
     *  number {@code <= 0} to indicate, that it's looping forever.
     */
    int getLoopCycles() {
        return loop_cycles;
    }

    /**
     * Decline this sound to be looped for the given cycles, exactly {@code 0} to
     *  indicate that this sound is not looping at all, or forever by giving
     *  a number {@code < 0}.
     * @param cycles how often the sound should be looped. Give {@code > 0} to
     *  loop it for n-times, exactly {@code 0} to not loop at all or {@code < 0}
     *  to loop forever.
     */
    void setLoopCycles(int cycles) {
        loop_cycles = cycles;
    }
}
