package org.ita23.pacman;

import java.util.HashMap;
import java.util.Map;

import org.ita23.pacman.game.Canvas;
import org.ita23.pacman.game.Color;
import org.ita23.pacman.game.Font;
import org.ita23.pacman.res.ImageResource;
import org.teavm.jso.browser.Window;
import org.teavm.jso.canvas.CanvasRenderingContext2D;
import org.teavm.jso.dom.html.HTMLImageElement;

/**
 * The pacman canvas to draw the game on, implemented with Web Canvas
 * technologies.
 */
public class WebCanvas implements Canvas {

	private final Map<ImageResource, HTMLImageElement> imageCache;
	private final CanvasRenderingContext2D render;

	public WebCanvas(CanvasRenderingContext2D render) {
		this.render = render;
		this.imageCache = new HashMap<>();
		loadImages();
	}

	private void loadImages() {
		for (ImageResource resource : ImageResource.values()) {
			HTMLImageElement image = (HTMLImageElement) Window.current().getDocument().createElement("img");
			image.setSrc(Base64Resource.getResource(resource.resource_path));
			this.imageCache.put(resource, image);
		}
	}

	@Override
	public void setColor(Color color) {
		// NOTE: The original Swing implementation uses one color for both - so we
		// emulate this behaviour
		this.render.setFillStyle("rgb(" + color.r + "," + color.g + "," + color.b + ")");
		this.render.setStrokeStyle("rgb(" + color.r + "," + color.g + "," + color.b + ")");
	}

	@Override
	public void setFont(Font font) {
		switch (font.style) {
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
		this.render.drawImage(this.imageCache.get(resource), x, y);
	}

	@Override
	public void drawImage(ImageResource resource, int x, int y, int width, int height) {
		this.render.drawImage(this.imageCache.get(resource), x, y, width, height);
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		// Adjust X/Y from top-left (in Swing) to center (HTML canvas)
		int centerX = x + width / 2;
		int centerY = y + width / 2;
		this.render.beginPath();
		// NOTE: this renders a perfect circle, not an oval (height is ignored...) -
		// good enough
		this.render.arc(centerX, centerY, width / 2, 0, Math.PI * 2);
		this.render.closePath();
		this.render.fill();
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		int radius = width / 2;
		// Adjust X/Y from top-left (in Swing) to center (HTML canvas)
		int centerX = x + radius;
		int centerY = y + radius;
		// This value can become briefly negative, which flashes on Web.
		// It does nothing in the Swing implementation, go figure...
		arcAngle = Math.max(arcAngle, 0);
		// Positive angles means COUNTER-clockwise rotation... (thanks Swing)
		startAngle = -startAngle;
		arcAngle = -arcAngle;
		// Convert from degrees to radians
		double startRad = Math.toRadians(startAngle);
		double endRad = Math.toRadians(startAngle + arcAngle);

		this.render.beginPath();
		// NOTE: last "true" to draw counter-clockwise!
		this.render.arc(centerX, centerY, radius, startRad, endRad, true);
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
