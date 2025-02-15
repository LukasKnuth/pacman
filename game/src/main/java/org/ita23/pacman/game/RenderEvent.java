package org.ita23.pacman.game;

/**
 * Describes an object that can be rendered.
 * @author Lukas Knuth
 * @version 1.0
 */
public interface RenderEvent {

  /**
   * This method is used to draw the current state on the
   * given {@code Canvas}.
   * @param c the object to draw on.
   */
  public void render(Canvas c);
}
