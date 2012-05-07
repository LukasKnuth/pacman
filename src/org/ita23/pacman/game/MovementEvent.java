package org.ita23.pacman.game;

/**
 * Describes an object which is part of an AI and therefore
 *  needs to act on it's own.
 * @author Lukas Knuth
 * @version 1.0
 */
public interface MovementEvent {

    /**
     * Called when the AI is to think about what step to do
     *  next.
     */
    public void think();
}
