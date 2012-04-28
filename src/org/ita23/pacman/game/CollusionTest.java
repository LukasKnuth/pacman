package org.ita23.pacman.game;

import org.ita23.pacman.logic.Point;

/**
 * This object will check if a given {@code Point} has collided with an object
 *  on the current {@code Map}.</p>
 * @author Lukas Knuth
 * @version 1.0
 */
public interface CollusionTest {

    /**
     * This method will check if the object {@code you}, at it's given
     *  coordinates, has collided with <u>any kind</u> of object on the
     *  current {@code Map}-instance.</p>
     * If you need to know the kind of object it collided with too, use
     *  the {@code checkCollusion()}-method!
     * @param you the figure which checks if it collided.
     * @return whether if there was a collusion or not.
     * @see CollusionTest#checkCollusion(Object, org.ita23.pacman.logic.Point)
     */
    public boolean checkAnyCollusion(Point you);

    /**
     * This method will check if the object {@code you}, at it's given
     *  coordinates, has collided with <u>the given type</u> of object
     *  on the current {@code Map}-instance.</p>
     * If you just want to know if there was any kind of collusion, you
     *  can also use the {@code checkAnyCollusion()}-method.
     * @param object the object to to check for collusion with
     *           {@code you}.
     * @param you the figure which checks if it collided.
     * @param <T> the type of object you want to check for collusion with
     *           {@code you}.
     * @return whether there was a collusion with the given object-type
     *  or not.
     */
    public <T> boolean checkCollusion(T object, Point you);

    /**
     * Possible directions for the {@code checkNextCollusion()}-method.
     * @see CollusionTest#checkNextCollusion(Object, org.ita23.pacman.logic.Point, org.ita23.pacman.game.CollusionTest.NextDirection) 
     */
    public enum NextDirection{
        UP, RIGHT, DOWN, LEFT
    }

    /**
     * Will check for a collusion with a specified type of object on the next
     *  possible collusion-event on the current {@code Map}-instance.
     * @param object the object to to check for collusion with
     *           {@code you}.
     * @param you the figure which checks if it collided.
     * @param next the direction to which the collusion should be checked.
     * @param <T> the type of object you want to check for collusion with
     *           {@code you}.
     * @return whether there was a collusion with the given object-type
     *  or not.
     */
    public <T> boolean checkNextCollusion(T object, Point you, NextDirection next);

}
