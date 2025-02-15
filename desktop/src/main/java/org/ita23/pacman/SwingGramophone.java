package org.ita23.pacman;

import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.Clip;

import org.ita23.pacman.game.Gramophone;
import org.ita23.pacman.res.SoundResource;

/**
 * A Pacman gramophone to play sounds on using the Swing/JavaX sound system.
 */
public class SwingGramophone implements Gramophone {

  private final Map<SoundResource, Clip> soundCache;

  public SwingGramophone() {
    this.soundCache = new HashMap<>();
    preloadCache();
  }

  private void preloadCache() {
    for (SoundResource resource : SoundResource.values()) {
      Clip sound = ClasspathResourceLoader.loadSound(resource.resource_path);
      this.soundCache.put(resource, sound);
    }
  }

	@Override
	public boolean isPlaying(SoundResource resource) {
	  Clip clip = this.soundCache.get(resource);
	  return clip.isActive();
	}

	@Override
	public void stop(SoundResource resource) {
    Clip clip = this.soundCache.get(resource);
    clip.stop();
    clip.setFramePosition(0);
	}

	@Override
	public void play(SoundResource resource) {
    Clip clip = this.soundCache.get(resource);
    // Ensure we're playing it from the start.
    clip.stop();
    clip.setFramePosition(0);
    clip.start();
	}

	@Override
	public void loopEndless(SoundResource resource) {
    Clip clip = this.soundCache.get(resource);
    // Ensure we're playing it from the start.
    clip.stop();
    clip.setFramePosition(0);
    clip.loop(Clip.LOOP_CONTINUOUSLY);
	}
}
