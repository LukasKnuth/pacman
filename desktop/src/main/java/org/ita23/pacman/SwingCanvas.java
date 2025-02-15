package org.ita23.pacman;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.util.HashMap;
import java.util.Map;

import org.ita23.pacman.game.Canvas;
import org.ita23.pacman.game.Color;
import org.ita23.pacman.game.Font;
import org.ita23.pacman.res.ImageResource;

/**
 * A Pacman canvas to draw the game on using Swing/AWT as the underlying
 *  render method.
 */
public class SwingCanvas implements Canvas {

  private final Graphics graphics;
	private final Map<ImageResource, Image> resourceCache;
	private final Map<Color, java.awt.Color> colorCache;
	private final Map<Font, java.awt.Font> fontCache;

  public SwingCanvas(Graphics graphics) {
    this.graphics = graphics;
    this.resourceCache = new HashMap<>();
    this.colorCache = new HashMap<>();
    this.fontCache = new HashMap<>();
    preloadCache();
  }

  private void preloadCache() {
  	for (ImageResource resource : ImageResource.values()) {
			Image image = ClasspathResourceLoader.loadImage(resource);
  		this.resourceCache.put(resource, image);
  	}
  }

	@Override
	public void setColor(Color color) {
		if (!this.colorCache.containsKey(color)) {
			this.colorCache.put(color, new java.awt.Color(color.r, color.g, color.b));
		}
		this.graphics.setColor(this.colorCache.get(color));
	}

	@Override
	public void setFont(Font font) {
		if (!this.fontCache.containsKey(font)) {
		  int awtStyle = java.awt.Font.PLAIN;

		  switch (font.style) {
		    case BOLD:
		      awtStyle = java.awt.Font.BOLD;
		      break;
		    case ITALIC:
		      awtStyle = java.awt.Font.ITALIC;
		      break;
		    case BOLD_ITALIC:
		      awtStyle = java.awt.Font.BOLD | java.awt.Font.ITALIC;
		      break;
		  }
			this.fontCache.put(font, new java.awt.Font(font.name, awtStyle, font.size));
		}
	  this.graphics.setFont(this.fontCache.get(font));
	}

	@Override
	public void drawString(String text, int x, int y) {
	  this.graphics.drawString(text, x, y);
	}

	@Override
	public void drawImage(ImageResource resource, int x, int y) {
		this.graphics.drawImage(this.resourceCache.get(resource), x, y, null);
	}

	@Override
	public void drawImage(ImageResource resource, int x, int y, int width, int height) {
		this.graphics.drawImage(this.resourceCache.get(resource), x, y, width, height, null);
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
	  this.graphics.fillOval(x, y, width, height);
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
	  this.graphics.fillArc(x, y, width, height, startAngle, arcAngle);
	}

	@Override
	public void drawRect(int x, int y, int width, int height) {
	  this.graphics.drawRect(x, y, width, height);
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
	  this.graphics.fillRect(x, y, width, height);
	}

	@Override
	public void setStrokeWidth(float width) {
	  Graphics2D g2d = (Graphics2D) this.graphics;
	  g2d.setStroke(new BasicStroke(width));
	}

	@Override
	public float getStrokeWidth() {
	  Graphics2D g2d = (Graphics2D) this.graphics;
	  Stroke stroke = g2d.getStroke();
	  if (stroke instanceof BasicStroke) {
	    return ((BasicStroke) stroke).getLineWidth();
	  } else {
	    return 1.0f;
	  }
	}
  
}
