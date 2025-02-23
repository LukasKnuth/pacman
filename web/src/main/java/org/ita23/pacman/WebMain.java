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
	  GameLoop.INSTANCE.step(last_input_state, web_canvas);
	  Window.requestAnimationFrame(this);
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
  		      last_input_state = JoystickState.UP;
  		      break;
  		    case "ArrowDown":
  		      last_input_state = JoystickState.DOWN;
  		      break;
  		    case "ArrowLeft":
  		      last_input_state = JoystickState.LEFT;
  		      break;
  		    case "ArrowRight":
  		      last_input_state = JoystickState.RIGHT;
  		      break;
  		  }
  		}
    });

    Bootstrap.bootstrap(canvas.getWidth(), canvas.getHeight());
  }

  // TODO game-loop runs at variable FPS at the moment, 60 is target!
  // TODO support WASD and ESC for the game
  // TODO Start off muted and allow toggle mute with M
  // TODO Support gamepad via Navigator.getGamepads()

  public static void main(String[] args) {
    new WebMain();
  }
}
