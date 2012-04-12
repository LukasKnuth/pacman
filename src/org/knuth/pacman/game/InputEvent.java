package org.knuth.pacman.game;

import java.awt.event.KeyEvent;

/**
 * Describes an event which is interested in the users input.
 * @author Lukas Knuth
 * @version 1.0
 */
public interface InputEvent {

    /**
     * The user has given input by using the keyboard.
     * @param event the input-event.
     */
    public void keyboardInput(KeyEvent event);
}
