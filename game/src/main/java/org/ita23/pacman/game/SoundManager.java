package org.ita23.pacman.game;

import org.ita23.pacman.res.SoundResource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class offers the basic logic to play sounds when predefined Events occur.
 * @author Lukas Knuth
 * @version 1.0
 */
public enum SoundManager {

    /** The instance to work with */
    INSTANCE;

    /** A list of sounds which where started previously */
    private final Map<SoundResource, Boolean> started;
    /** The device to play sounds on, provided by the Platform */
    private Gramophone playbackDevice;

    private SoundManager(){
        started = new HashMap<SoundResource, Boolean>(8);
    }

    /**
     * Set the platform specific {@code Gramophone} implementation to play
     * the actual {@code SoundResource}es.
     */
    public void setGramophone(Gramophone gramophone) {
        this.playbackDevice = gramophone;
    }

    /**
     * This method will continue the playback of all previously paused sounds.
     * @see #pauseAll()
     */
    public void unpauseAll(){
        for (Map.Entry<SoundResource, Boolean> entry : started.entrySet()){
            if (entry.getValue() == true){
                // Sound was looping
                loop(entry.getKey());
            } else {
                play(entry.getKey());
            }
        }
    }

    /**
     * This method is used to pause all currently played sounds, no madder if
     *  they where just playing or looping. One of the use-cases could be pausing
     *  every sound playback, when pausing your application.</p>
     * @see #unpauseAll()
     */
    public void pauseAll(){
        // We crop the list of started sounds down to the ones _actually_ still playing
        Iterator<Map.Entry<SoundResource, Boolean>> iter = started.entrySet().iterator();
        while (iter.hasNext()){
            Map.Entry<SoundResource, Boolean> entry = iter.next();
            if (!playbackDevice.isPlaying(entry.getKey())) {
                iter.remove();
            } else {
                playbackDevice.stop(entry.getKey());
            }
        }
    }

    /**
     * Plays the given {@code SoundResource} only once.</p>
     * If you want to loop a sound or play it more frequently, you might want
     *  to use the {@code loop()}-method.</p>
     * When this method is called, any previously playback will be stopped and
     *  the sound will start from the beginning.
     * @param resource the sound resource to play.
     * @see org.ita23.pacman.game.SoundManager#loop(String, int)
     */
    public void play(SoundResource resource){
        if (playbackDevice == null) {
            throw new IllegalStateException("Gramophone was not set on SoundManger yet!");
        }
        started.put(resource, false);
        playbackDevice.play(resource);
    }

    /**
     * Will loop the given {@code SoundResource} continiously.</p>
     * This is thought as a method for playing background noises (for example).
     * To stop a sound which is continuously looping, use the {@code stop()}-method.</p>
     * If a sound is already looping, calling this method will not have any effect!
     * @param resource the sound resource to looped.
     * @see org.ita23.pacman.game.SoundManager#stop(String)
     */
    public void loop(SoundResource resource){
        if (playbackDevice == null) {
            throw new IllegalStateException("Gramophone was not set on SoundManger yet!");
        }
        // Don't restart the loop if the clip is already playing.
        if (started.get(resource) == null || started.get(resource) == false) {
            started.put(resource, true);
            playbackDevice.loopEndless(resource);
        }
    }

    /**
     * This method should be used to stop a currently playing sound.</p>
     * A common case of usage for this method is, when you are "endlessly" playing a
     *  sound (by using the {@code loop()}-method) and now want to stop it.</p>
     * Stopping a sound will also cause it to be set to it's beginning, so any further
     *  playback using either the {@code loop()} or {@code play()}-methods will start
     *  from the beginning again.
     * @param resource the sound resource to stop.
     */
    public void stop(SoundResource resource){
        if (playbackDevice == null) {
            throw new IllegalStateException("Gramophone was not set on SoundManger yet!");
        }
        started.remove(resource);
        playbackDevice.stop(resource);
    }
}
