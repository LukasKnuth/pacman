package org.ita23.pacman.figures;

import org.ita23.pacman.game.AIEvent;
import org.ita23.pacman.game.CollusionEvent;
import org.ita23.pacman.game.RenderEvent;

/**
 * An abstract base-class, shared between all ghosts, which offers basic
 *  functionality.</p>
 * This includes the knowledge about where the Player ({@code Pacman} is
 *  currently located.
 * @author Lukas Knuth
 * @version 1.0
 */
abstract class Ghost implements AIEvent, RenderEvent, CollusionEvent {

    /** The diameter of a ghost's body, e.g. his hitbox */
    private static final int HITBOX = 28;
    
    /** The pacman-instance which is currently moving on the game-field. */
    private final Pacman player;

    /**
     * This will create a ghost with the basic implementation, which
     *  includes tracking the current player.
     * @param player the {@code Pacman}-instance, which is currently
     *  playing the game.
     */
    protected Ghost(Pacman player){
        this.player = player;
    }

    /**
     * Checks if the ghost has caught the player.
     * @param x the ghost's X-position.
     * @param y the ghost's Y-position.
     * @return weather this ghost caught the player or not.
     */
    // TODO Maybe make a blog-post about this...
    protected boolean gotPlayer(int x, int y){
        // Calculate the third piece of the triangle:
        int triangle_x = (player.getX()+Pacman.HITBOX) - (x+Ghost.HITBOX);
        int triangle_y = (player.getY()+Pacman.HITBOX) - (y+Ghost.HITBOX);
        // Calculate the distance between player and ghost:
        double distance = Math.sqrt((triangle_x*triangle_x)+(triangle_y*triangle_y));
        // Check if we hit:
        if (distance < (Pacman.HITBOX/2 + Ghost.HITBOX/2)) return true;
        else return false;
    }
    
    /**
     * Get the current X-position of the player ({@code Pacman}).
     * @return the X-coordinate of the player.
     */
    protected int getPlayerX(){
        return player.getX();
    }

    /**
     * Get the current Y-position of the player ({@code Pacman}).
     * @return the Y-coordinate of the player.
     */
    protected int getPlayerY(){
        return player.getY();
    }
    
}
