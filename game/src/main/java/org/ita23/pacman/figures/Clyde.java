package org.ita23.pacman.figures;

import org.ita23.pacman.game.CollusionTest.NextDirection;
import org.ita23.pacman.logic.ChunkedMap.Chunk;
import org.ita23.pacman.logic.Point;

import java.awt.*;

/**
 * Clyde is the orange ghost AI, which will start from the cage and emerge last
 *  into the game.
 * For a detailed description of his behaviour, check out
 * <a href="http://home.comcast.net/~jpittman2/pacman/pacmandossier.html#CH4_Clyde">
 *   Chapter 4 of "The Pac-Man Dossier" - Clyde
 * </a>
 * @author Lukas Knuth
 * @version 1.0
 */
public class Clyde extends Ghost{

    /** Clyde's home-corner in the top-right corner of the field */
    private static final Point HOME_CORNER = new Point(0, Chunk.CHUNK_SIZE*31);

    /** A cached {@code Point}-instance for performance purposes */
    private Point target;

    /** The images for this ghost, looking to the left */
    private final Image[] ghost_left;
    /** The images for this ghost, looking to the right */
    private final Image[] ghost_right;
    /** The images for this ghost, looking up */
    private final Image[] ghost_up;
    /** The images for this ghost, looking down */
    private final Image[] ghost_down;

    /**
     * Create an instance of the orange ghost.
     */
    protected Clyde(Pacman player) {
        super(player);
        target = new Point(0, 0);
        // Load the images:
        ghost_down = new Image[]{
                loadImageResource("clyde/clyde_down_1.png"),
                loadImageResource("clyde/clyde_down_2.png")
        };
        ghost_left = new Image[]{
                loadImageResource("clyde/clyde_left_1.png"),
                loadImageResource("clyde/clyde_left_2.png")
        };
        ghost_up = new Image[]{
                loadImageResource("clyde/clyde_up_1.png"),
                loadImageResource("clyde/clyde_up_2.png")
        };
        ghost_right = new Image[]{
                loadImageResource("clyde/clyde_right_1.png"),
                loadImageResource("clyde/clyde_right_2.png")
        };
    }

    @Override
    protected Point getTargetChunk(Pacman player) {
        if ( (distanceToPacman(player.getX(), player.getY()) / Chunk.CHUNK_SIZE) <= 8){
            // To close, get away!
            target.setX(HOME_CORNER.getX());
            target.setY(HOME_CORNER.getY());
            return target;
        } else {
            // Not close enough, get Pacman!
            target.setX(player.getX());
            target.setY(player.getY());
            return target;
        }
    }

    /**
     * Calculates the distance from this ghost (Clyde) to Pacman's current position.
     * @param x pacman's x-coordinate.
     * @param y pacman's y-coordinate.
     * @return the distance between Clyde and Pacman.
     */
    private int distanceToPacman(int x, int y){
        // Calculate the third piece of the triangle:
        int triangle_x = x - this.x;
        int triangle_y = y - this.y;
        // Calculate the distance between player and ghost:
        return (int) Math.sqrt((triangle_x*triangle_x)+(triangle_y*triangle_y));
    }

    @Override
    protected Image getGhostImage(NextDirection direction, int image_index) {
        switch (direction){
            case UP:
                return ghost_up[image_index];
            case DOWN:
                return ghost_down[image_index];
            case LEFT:
                return ghost_left[image_index];
            case RIGHT:
                return ghost_right[image_index];
            default:
                throw new IllegalArgumentException("The direction can't be "+direction);
        }
    }

    @Override
    protected Point getHomeCorner() {
        return HOME_CORNER;
    }
}
