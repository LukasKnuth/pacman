package org.ita23.pacman.res;

/**
 * A collection of all known sound resouces used in the game.
 * Can be passed to {@code SoundManager} to play.
 */
public enum SoundResource {
  INTRO("/sound/intro.wav"),
  DIEING("/sound/dieing.wav"),
  ROUND_OVER("/sound/round_over.wav"),
  EAT("/sound/eat.wav"),
  EAT_FRUIT("/sound/eat_fruit.wav");

  public final String resource_path;

  private SoundResource(String resource_path) {
    this.resource_path = resource_path;
  }
}
