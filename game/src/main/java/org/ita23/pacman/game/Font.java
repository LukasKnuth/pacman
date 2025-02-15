package org.ita23.pacman.game;

public class Font {
  public enum Style {
    BOLD, ITALIC, BOLD_ITALIC;
  }
  
  public final String name;
  public final Style style;
  public final int size;

  public Font(String name, Style style, int size) {
    this.name = name;
    this.style = style;
    this.size = size;
  }
}
