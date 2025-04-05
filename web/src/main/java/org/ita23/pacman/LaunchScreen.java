package org.ita23.pacman;

import org.ita23.pacman.game.Canvas;
import org.ita23.pacman.game.Color;
import org.ita23.pacman.game.Font;
import org.ita23.pacman.game.GameLoop;
import org.ita23.pacman.game.InputEvent;
import org.ita23.pacman.game.RenderEvent;
import org.ita23.pacman.logic.ChunkedMap;

/**
 * The initial prompt to start the game after everything is loaded
 */
public class LaunchScreen implements RenderEvent, InputEvent {
  private static final Font HEADLINE = new Font("Arial", Font.Style.BOLD, 18);
  private static final Font BODY = new Font("Arial", Font.Style.BOLD, 16);
  private static final Color FONT_COLOR = new Color(255, 255, 87);
  private static final Color BOX_COLOR = new Color(0, 0, 0);

  private boolean first_launch = true;

	@Override
	public void render(Canvas c) {
	  if (this.first_launch) {
	    // Overlay box
	    c.setColor(BOX_COLOR);
	    c.fillRect(80, 240, 300, 120);
      float old = c.getStrokeWidth();
      c.setStrokeWidth(2.0f);
      c.setColor(ChunkedMap.BLOCK_COLOR);
	    c.drawRect(80, 240, 300, 120);
	    c.setStrokeWidth(old);
	    // Text inside the box
	    c.setFont(HEADLINE);
	    c.setColor(FONT_COLOR);
  	  c.drawString("Any input to Start", 150, 260);
  	  c.setFont(BODY);
  	  c.drawString("Touch: Swipe or Fling", 120, 290);
  	  c.drawString("Keyboard: WASD or Arrows", 120, 315);
  	  c.drawString("Gamepad: Digital or Analog", 120, 340);
	  }
	}

	@Override
	public void joystickInput(JoystickState state) {
	  if (this.first_launch && state != JoystickState.NEUTRAL) {
	    this.first_launch = false;
	    GameLoop.INSTANCE.play();
	  }
	}
}
