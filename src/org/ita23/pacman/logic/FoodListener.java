package org.ita23.pacman.logic;

/**
 * This listener will be notified when {@code Pacman} ate something.</p>
 * The listener can be registered to the {@code GameState}-class.
 * @author Lukas Knuth
 * @version 1.0
 */
public interface FoodListener {

    /**
     * This method will be called when {@code Pacman} ate something.
     * @param food the kind of food pacman ate.
     */
    public void consumed(GameState.Food food);
}
