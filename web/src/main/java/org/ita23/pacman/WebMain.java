package org.ita23.pacman;

import org.ita23.pacman.game.Canvas;
import org.ita23.pacman.game.GameLoop;
import org.ita23.pacman.game.InputEvent.JoystickState;
import org.ita23.pacman.game.SoundManager;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.events.KeyboardEvent;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.browser.Window;
import org.teavm.jso.canvas.CanvasRenderingContext2D;
import org.teavm.jso.browser.AnimationFrameCallback;

public class WebMain implements AnimationFrameCallback {

  private JoystickState last_input_state = JoystickState.NEUTRAL;
  private Canvas web_canvas;

  private static final double TARGET_FPS_INTERVAL = 1000 / 60;
  private double last_frame_time = -1;

  private WebMain() {
    setupGame();
    startLoop();
  }

  private void startLoop() {
    SoundManager.INSTANCE.setGramophone(new WebGramophone());
    GameLoop.INSTANCE.lock();
    Window.requestAnimationFrame(this);
  }
  
  @Override
  public void onAnimationFrame(double timestamp) {
    // register for next frame
	  Window.requestAnimationFrame(this);

    // populate frame time for very first frame drawn
    if (this.last_frame_time == -1) {
      this.last_frame_time = timestamp;
    }

    // Ensure we render/simulate at 60FPS
    // Partially stolen from https://stackoverflow.com/a/19772220/717341
    double elapsed = timestamp - last_frame_time;
    if (elapsed > TARGET_FPS_INTERVAL) {
  	  // NOTE: subtract any time we waited "too long" on the current frame as well
  	  this.last_frame_time = timestamp - (elapsed % TARGET_FPS_INTERVAL);

  	  GameLoop.INSTANCE.step(last_input_state, web_canvas);
    }
  }

  private void setupGame() {
    HTMLDocument document = Window.current().getDocument();
    HTMLCanvasElement canvas = (HTMLCanvasElement) document.getElementById("pacman_canvas");
    CanvasRenderingContext2D context = (CanvasRenderingContext2D) canvas.getContext("2d");
    this.web_canvas = new WebCanvas(context);

    document.addEventListener("keydown", new EventListener<KeyboardEvent>() {
  		@Override
  		public void handleEvent(KeyboardEvent e) {
  		  switch (e.getKey()) {
  		    case "ArrowUp":
  		    case "w":
  		      last_input_state = JoystickState.UP;
  		      break;
  		    case "ArrowDown":
  		    case "s":
  		      last_input_state = JoystickState.DOWN;
  		      break;
  		    case "ArrowLeft":
  		    case "a":
  		      last_input_state = JoystickState.LEFT;
  		      break;
  		    case "ArrowRight":
  		    case "d":
  		      last_input_state = JoystickState.RIGHT;
  		      break;
  		  }
  		}
    });

    Bootstrap.bootstrap(canvas.getWidth(), canvas.getHeight());
  }

  // TODO support WASD and ESC for the game
  // TODO Start off muted and allow toggle mute with M
  // TODO Support gamepad via Navigator.getGamepads()

  public static void main(String[] args) {
    new WebMain();
  }
}
