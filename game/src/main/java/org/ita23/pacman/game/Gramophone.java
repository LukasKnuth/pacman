package org.ita23.pacman.game;

import org.ita23.pacman.res.SoundResource;

/**
 * An (virtual) device to play sounds on.
 */
public interface Gramophone {
  public boolean isPlaying(SoundResource resource);

  /**
   * Stops and resets the sound to its beginning.
   */
  public void stop(SoundResource resource);

  /**
   * Plays the given resource FROM THE START.
   */
  public void play(SoundResource resource);

  public void loopEndless(SoundResource resource);
}
