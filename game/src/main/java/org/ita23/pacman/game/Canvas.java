package org.ita23.pacman.game;

import org.ita23.pacman.res.ImageResource;

/**
 * A canvas to draw things onto. The actual drawing logic is implemented for each
 * supported platform in their respective project.
 */
public interface Canvas {

  public void setColor(Color color);

  public void setFont(Font font);

  /**
   * Draw text on the canvas using the current font and color.
   * @see #setColor(Color)
   * @see #setFont(Font)
   */
  public void drawString(String text, int x, int y);

  public void drawImage(ImageResource resource, int x, int y);

  public void drawImage(ImageResource resource, int x, int y, int width, int height);

  public void fillOval(int x, int y, int width, int height);

  public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle);

  public void drawRect(int x, int y, int width, int height);

  public void fillRect(int x, int y, int width, int height);

  /**
   * Sets an outline stroke to be used in any {@code draw}-call.
   * @see #drawRect(int, int, int, int)
   */
  public void setStrokeWidth(float width);

  public float getStrokeWidth();

}
