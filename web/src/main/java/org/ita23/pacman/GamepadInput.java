package org.ita23.pacman;

import org.ita23.pacman.game.InputEvent.JoystickState;
import org.teavm.jso.browser.Navigator;
import org.teavm.jso.gamepad.Gamepad;
import org.teavm.jso.gamepad.GamepadButton;
import org.teavm.jso.gamepad.GamepadEvent;

/**
 * Web browser based gamepad inputs.
 */
public class GamepadInput {
  // type from https://w3c.github.io/gamepad/#dom-gamepadmappingtype
  private static final String STANDARD_MAPPING = "standard";
  // standard mappings: https://w3c.github.io/gamepad/#dfn-standard-gamepad
  private static final int IDX_DIGI_L = 14;
  private static final int IDX_DIGI_R = 15;
  private static final int IDX_DIGI_U = 12;
  private static final int IDX_DIGI_D = 13;
  private static final int IDX_PAUSE = 9;

  private static final double AXIS_DEADZONE = 0.2;
  private static final int IDX_AXIS_L_X = 0;
  private static final int IDX_AXIS_L_Y = 1;
  private static final int IDX_AXIS_R_X = 2;
  private static final int IDX_AXIS_R_Y = 3;

  private static final int NOT_CONNECTED = -1;
  private int current_gamepad_index = NOT_CONNECTED;
  
  public void onConnected(GamepadEvent evt) {
    int gamepad_index = evt.getGamepad().getIndex();
    String mapping = evt.getGamepad().getMapping();
    // Only allow one pad - keep the same pad if a new one is connected
    // Only allow gamepads using the standard button mapping
    if (this.current_gamepad_index == NOT_CONNECTED && mapping == STANDARD_MAPPING) {
      this.current_gamepad_index = gamepad_index;
    }
  }

  public void onDisconnected(GamepadEvent evt) {
    int gamepad_index = evt.getGamepad().getIndex();
    // TODO use stack, keep all connected ids, pick the next one here.
    if (gamepad_index == this.current_gamepad_index) {
      this.current_gamepad_index = NOT_CONNECTED;
    }
  }

  public JoystickState getDirection() {
    if (this.current_gamepad_index == NOT_CONNECTED) {
      return JoystickState.NEUTRAL;
    } else {
      Gamepad pad = Navigator.getGamepads()[this.current_gamepad_index];
      if (pad.isConnected()) {
        JoystickState direction = fromDigital(pad);
        if (direction != JoystickState.NEUTRAL) return direction;
        return fromAnalog(pad);
      } else {
        return JoystickState.NEUTRAL;
      }
    }
  }

  private static JoystickState fromDigital(Gamepad gamepad) {
    GamepadButton[] btns = gamepad.getButtons();
    if (btns[IDX_DIGI_D].isPressed()) {
      return JoystickState.DOWN;
    } else if (btns[IDX_DIGI_U].isPressed()) {
      return JoystickState.UP;
    } else if (btns[IDX_DIGI_L].isPressed()) {
      return JoystickState.LEFT;
    } else if (btns[IDX_DIGI_R].isPressed()) {
      return JoystickState.RIGHT;
    } else {
      return JoystickState.NEUTRAL;
    }
  }

  private static JoystickState fromAnalog(Gamepad gamepad) {
    double[] axes = gamepad.getAxes();
    JoystickState result;
    result = fromAxe(axes[IDX_AXIS_L_X], JoystickState.RIGHT, JoystickState.LEFT);
    if (result != JoystickState.NEUTRAL) return result;
    result = fromAxe(axes[IDX_AXIS_L_Y], JoystickState.DOWN, JoystickState.UP);
    if (result != JoystickState.NEUTRAL) return result;
    result = fromAxe(axes[IDX_AXIS_R_X], JoystickState.RIGHT, JoystickState.LEFT);
    if (result != JoystickState.NEUTRAL) return result;
    result = fromAxe(axes[IDX_AXIS_R_Y], JoystickState.DOWN, JoystickState.UP);
    return result;
  }

  private static JoystickState fromAxe(double axe, JoystickState pos, JoystickState neg) {
    if (Math.abs(axe) > AXIS_DEADZONE) {
      if (axe > 0) return pos; else return neg;
    } else {
      return JoystickState.NEUTRAL;
    }
  }
}
