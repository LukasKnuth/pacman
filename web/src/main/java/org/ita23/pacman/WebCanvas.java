package org.ita23.pacman;

import org.ita23.pacman.game.Canvas;
import org.ita23.pacman.game.Color;
import org.ita23.pacman.game.Font;
import org.ita23.pacman.res.ImageResource;
import org.teavm.jso.canvas.CanvasRenderingContext2D;

/**
 * The pacman canvas to draw the game on, implemented with Web Canvas technologies.
 */
public class WebCanvas implements Canvas {

  private final CanvasRenderingContext2D render;

  public WebCanvas(CanvasRenderingContext2D render) {
    this.render = render;
    loadImages();
  }

  private void loadImages() {
  	// TODO load the images and cache them.
  	// TODO render them later in the image-specific methods.
  }

	@Override
	public void setColor(Color color) {
		// TODO is this the _fill_ or _stroke_ style?
	  this.render.setFillStyle("rgb(" + color.r + "," + color.g + "," + color.b + ")"); 
	  this.render.setStrokeStyle("rgb(" + color.r + "," + color.g + "," + color.b + ")"); 
	}

	@Override
	public void setFont(Font font) {
		switch(font.style) {
			case Font.Style.BOLD:
				this.render.setFont("bold " + font.size + "px " + font.name);
				break;
			case Font.Style.ITALIC:
				this.render.setFont("italic " + font.size + "px " + font.name);
				break;
			case Font.Style.BOLD_ITALIC:
				this.render.setFont("italic bold " + font.size + "px " + font.name);
				break;
		}
	}

	@Override
	public void drawString(String text, int x, int y) {
		this.render.fillText(text, x, y);
	}

	@Override
	public void drawImage(ImageResource resource, int x, int y) {
		// TODO implement with resoutce loading
	}

	@Override
	public void drawImage(ImageResource resource, int x, int y, int width, int height) {
		// TODO implement with resoutce loading
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		// Adjust X/Y from top-left (in Swing) to center (HTML canvas)
		int centerX = x + width / 2;
		int centerY = y + width / 2;
		this.render.beginPath();
		// TODO this renders a perfect circle, not an oval (height is ignored...)
		this.render.arc(centerX, centerY, width / 2, 0, Math.PI * 2);
		this.render.closePath();
		this.render.fill();
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		// Adjust X/Y from top-left (in Swing) to center (HTML canvas)
		int centerX = x + width / 2;
		int centerY = y + width / 2;
		// Positive angles means COUNTER-clockwise rotation... (thanks Swing)
		startAngle = -startAngle;
		arcAngle = -arcAngle;
		// Convert from degrees to radians
		double startRad = startAngle * Math.PI / 180;
		double endRad = (startAngle + arcAngle) * Math.PI / 180;

		this.render.beginPath();
		// NOTE: last "true" to draw counter-clockwise!
		this.render.arc(centerX, centerY, width / 2, startRad, endRad, true);
		this.render.lineTo(centerX, centerY);
		this.render.closePath();
		this.render.fill();
	}

	@Override
	public void drawRect(int x, int y, int width, int height) {
		this.render.beginPath();
		this.render.rect(x, y, width, height);
		this.render.closePath();
		this.render.stroke();
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		this.render.beginPath();
		this.render.rect(x, y, width, height);
		this.render.closePath();
		this.render.fill();
	}

	@Override
	public void setStrokeWidth(float width) {
		this.render.setLineWidth(width);
	}

	@Override
	public float getStrokeWidth() {
		return (float) this.render.getLineWidth();
	}
}
