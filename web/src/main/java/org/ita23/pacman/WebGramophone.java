package org.ita23.pacman;

import org.ita23.pacman.game.Gramophone;
import org.ita23.pacman.res.SoundResource;
import org.teavm.jso.webaudio.AudioContext;

/**
 * WebGramophone
 */
public class WebGramophone implements Gramophone {

  private final AudioContext context;

  public WebGramophone() {
    this.context = new AudioContext();
  }

  private void loadResources() {
    // load all resources from disk. Should we convert them to binaries at BUILD time?
  }

	@Override
	public boolean isPlaying(SoundResource resource) {
	  // TODO implement
	  return false;
	}

	@Override
	public void stop(SoundResource resource) {
	  // TODO implement
	}

	@Override
	public void play(SoundResource resource) {
	  // TODO implement
	}

	@Override
	public void loopEndless(SoundResource resource) {
	  // TODO implement
	}
}
