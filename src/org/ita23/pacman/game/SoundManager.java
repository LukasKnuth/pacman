package org.ita23.pacman.game;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class offers the basic logic to play sounds when predefined Events occur.
 * @author Lukas Knuth
 * @version 1.0
 */
public enum SoundManager {

    /** The instance to work with */
    INSTANCE;

    private final Map<String, Sound> sounds;

    /**
     * Singleton -> Private constructor.
     */
    private SoundManager(){
        sounds = new HashMap<String, Sound>(8);
    }

    /**
     * Plays the sound, identified by the given event-name.
     * @param event_name the event-name of the desired sound.
     * @return the length of the played sound in seconds or -1 if
     *  there was a problem reading the length.
     */
    public int play(String event_name){
        // Check if the sound is in the library:
        if (!sounds.containsKey(event_name))
            throw new IllegalArgumentException("There is no sound for '"+event_name+"'");
        // Play it:
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(sounds.get(event_name).getInputStream());
            clip.start();
            return (int) (clip.getMicrosecondLength()/1000000);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Add a sound to the "media library" for later playback.</p>
     * To play this sound, use the {@code play(String)}-method with the event-
     *  name, you choose for this sound.
     * @param sound the sound to add to the "media library".
     * @see #play(String)
     */
    public void addSound(Sound sound){
        // Validate the argument:
        if (sound == null) throw  new NullPointerException("The sound can't be null.");
        // Add to the library:
        sounds.put(sound.getEventName(), sound);
    }
}
