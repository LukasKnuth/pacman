package org.ita23.pacman.game;

/**
 * Represents a simple RGB color, to be drawn on a {@code Canvas}.
 */
public class Color {
  public final int r;
  public final int g;
  public final int b;

  public Color(int r, int g, int b) {
    this.r = r;
    this.g = g;
    this.b = b;
  }

  public static final Color WHITE = new Color(255, 255, 255);
}
