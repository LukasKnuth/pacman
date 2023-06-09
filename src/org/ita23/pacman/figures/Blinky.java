package org.ita23.pacman.figures;

import org.ita23.pacman.game.CollusionTest;
import org.ita23.pacman.logic.ChunkedMap.Chunk;
import org.ita23.pacman.logic.Point;

import java.awt.*;

/**
 * Blinky is the red ghost AI, which will start "out of the box" at the beginning
 *  of the game.
 * For a detailed description of his behaviour, check out
 * <a href="http://home.comcast.net/~jpittman2/pacman/pacmandossier.html#CH4_Blinky">
 *   Chapter 4 of "The Pac-Man Dossier" - Blinky
 * </a>
 * @author Lukas Knuth
 * @version 1.0
 */
class Blinky extends Ghost{

    /** Blinky's home-corner in the top-right corner of the field */
    private static final Point HOME_CORNER = new Point(Chunk.CHUNK_SIZE*26, 0);

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
     * Create a new instance of the red ghost.
     */
    public Blinky(Pacman player){
        super(player);
        target = new Point(0, 0);
        // Load the images:
        ghost_down = new Image[]{
            loadImageResource("blinky/blinky_down_1.png"),
            loadImageResource("blinky/blinky_down_2.png")
        };
        ghost_left = new Image[]{
            loadImageResource("blinky/blinky_left_1.png"),
            loadImageResource("blinky/blinky_left_2.png")
        };
        ghost_up = new Image[]{
            loadImageResource("blinky/blinky_up_1.png"),
            loadImageResource("blinky/blinky_up_2.png")
        };
        ghost_right = new Image[]{
            loadImageResource("blinky/blinky_right_1.png"),
            loadImageResource("blinky/blinky_right_2.png")
        };
    }

    @Override
    protected Point getTargetChunk(Pacman player) {
        target.setX(player.getX());
        target.setY(player.getY());
        return target;
    }

    @Override
    protected Image getGhostImage(CollusionTest.NextDirection direction, int image_index) {
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
