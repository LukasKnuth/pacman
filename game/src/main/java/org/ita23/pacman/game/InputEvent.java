package org.ita23.pacman.game;

/**
 * Describes an event which is interested in the users input.
 * @author Lukas Knuth
 * @version 1.0
 */
public interface InputEvent {

    /**
     * The current state of the (usually virtual) Joystick for directional input
     * to control Pacman.
     */
    public enum JoystickState {
      UP, DOWN, LEFT, RIGHT, NEUTRAL;
    }

    /**
     * The user has given input by using the (virtual) Joystick.</p>
     * If no key was pressed, the state is {@code NEUTRAL}.
     * @param state the state of the virtual joystick.
     */
    public void joystickInput(JoystickState state);
}
