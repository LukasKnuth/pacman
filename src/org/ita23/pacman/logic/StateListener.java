package org.ita23.pacman.logic;

/**
 * This listener will be notified, when there was any kind of change
 *  to the current game-state. This includes This includes a won round,
 *  a lost live or a completely lost game (no more lives).
 * @author Lukas Knuth
 * @version 1.0
 */
public interface StateListener {

    /**
     * The possible states this listener will notify you about.
     */
    public enum States{
        ROUND_WON, LIVE_LOST, GAME_OVER
    }

    /**
     * This method will be called, when there was a change to the current
     *  state of the game.
     * @param state the new state of the game.
     */
    public void stateChanged(States state);
}
