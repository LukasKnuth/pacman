package org.ita23.pacman;

import org.ita23.pacman.game.InputEvent.JoystickState;
import org.teavm.jso.dom.events.Touch;
import org.teavm.jso.dom.events.TouchEvent;

/**
 * Handles Touch input (usually on mobile devices) for the pacman game.
 */
public class TouchInput {
    private static final double MIN_DISTANCE = 45.0;
    private static final double NOT_SET = -1.0;
  
    private double previous_x = NOT_SET;
    private double previous_y = NOT_SET;
    private JoystickState state = JoystickState.NEUTRAL;

    public JoystickState getCurrentState() {
      return state;
    }
  
    public JoystickState onTouchEnd(TouchEvent event) {
      event.preventDefault();
      reset();
      return JoystickState.NEUTRAL;
    }

    public JoystickState onTouchCancel(TouchEvent event) {
      event.preventDefault();
      reset();
      return JoystickState.NEUTRAL;
    }

    public JoystickState onTouchStart(TouchEvent event) {
      event.preventDefault();
		  Touch first_finger = event.getChangedTouches().get(0);
		  // TODO can this even happen? if yes - share this code with move()
		  if (first_finger != null) {
  	    previous_x = first_finger.getClientX();
  	    previous_y = first_finger.getClientY();
		  }
      return JoystickState.NEUTRAL;
    }

    public JoystickState onTouchMove(TouchEvent event) {
		  event.preventDefault();
		  Touch first_finger = event.getChangedTouches().get(0);
		  // TODO perhaps instead use Touch.identifier to pick a finger to track?
		  if (first_finger != null) {
		    double new_x = first_finger.getClientX();
		    double new_y = first_finger.getClientY();
		    state = handleChange(previous_x, previous_y, new_x, new_y);
	      // Don't reset if we're waiting to hit movement threshold
		    if (state != JoystickState.NEUTRAL) {
  		    previous_x = new_x;
  		    previous_y = new_y;
		    }
		    return state;
		  } else {
		    return JoystickState.NEUTRAL;
		  }
    }

		private static JoystickState handleChange(double previous_x, double previous_y, double new_x, double new_y) {
	    double change_x = previous_x - new_x;
	    double change_y = previous_y - new_y;
	    // TODO _sometimes_, the direction is incorrect. Especially when the finger is at the edge of the canvas.
	    if (Math.abs(change_x) >= MIN_DISTANCE) {
	      if (change_x < 0.0) {
	        return JoystickState.RIGHT;
	      } else {
	        return JoystickState.LEFT;
	      }
	    } else if (Math.abs(change_y) >= MIN_DISTANCE) {
	      if (change_y < 0.0) {
	        return JoystickState.DOWN;
	      } else {
	        return JoystickState.UP;
	      }
	    } else {
	      return JoystickState.NEUTRAL;
	    }
		}

    private void reset() {
	    previous_x = NOT_SET;
	    previous_y = NOT_SET;
	    state = JoystickState.NEUTRAL;
    }
}
