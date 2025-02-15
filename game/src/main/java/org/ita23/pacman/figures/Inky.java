package org.ita23.pacman.figures;

import org.ita23.pacman.res.ImageResource;
import org.ita23.pacman.game.CollusionTest;
import org.ita23.pacman.logic.ChunkedMap.Chunk;
import org.ita23.pacman.logic.Point;

/**
 * Inky is the blue ghost AI, which starts in the cage on the left side.</p>
 * For a detailed description of his behaviour, check out
 * <a href="http://home.comcast.net/~jpittman2/pacman/pacmandossier.html#CH4_Inky">
 *   Chapter 4 of "The Pac-Man Dossier" - Inky
 * </a>
 * @author Lukas Knuth
 * @version 1.0
 */
public class Inky extends Ghost{

    /** Inky's home-corner in the top-right corner of the field */
    private static final Point HOME_CORNER = new Point(Chunk.CHUNK_SIZE*26, Chunk.CHUNK_SIZE*31);
    /** The current {@code Blinky}-instance, to calculate the target in CHASE-mode. */
    private final Blinky blinky;

    /** A cached {@code Point}-instance for performance purposes */
    private Point target;

    /** The images for this ghost, looking to the left */
    private final ImageResource[] ghost_left;
    /** The images for this ghost, looking to the right */
    private final ImageResource[] ghost_right;
    /** The images for this ghost, looking up */
    private final ImageResource[] ghost_up;
    /** The images for this ghost, looking down */
    private final ImageResource[] ghost_down;

    /**
     * Create an instance of the blue ghost.
     * @param blinky the current instance of the red ghost. This is needed
     *  for targeting-purposes, as explained in the above linked dossier.
     */
    protected Inky(Pacman player, Blinky blinky) {
        super(player);
        target = new Point(0, 0);
        this.blinky = blinky;
        // Load the images:
        ghost_down = new ImageResource[]{
                ImageResource.INKY_DOWN_1,
                ImageResource.INKY_DOWN_2
        };
        ghost_left = new ImageResource[]{
                ImageResource.INKY_LEFT_1,
                ImageResource.INKY_LEFT_2
        };
        ghost_up = new ImageResource[]{
                ImageResource.INKY_UP_1,
                ImageResource.INKY_UP_2
        };
        ghost_right = new ImageResource[]{
                ImageResource.INKY_RIGHT_1,
                ImageResource.INKY_RIGHT_2
        };
    }

    @Override
    protected Point getTargetChunk(Pacman player) {
        int offset_x = 0, offset_y = 0;
        // First, get the position two chunks before packman:
        switch (player.getCurrentDirection()){
            case UP:
                offset_x = player.getX();
                offset_y = player.getY()-2*Chunk.CHUNK_SIZE;
                break;
            case DOWN:
                offset_x = player.getX();
                offset_y = player.getY()+2*Chunk.CHUNK_SIZE;
                break;
            case RIGHT:
                offset_x = player.getX()+2*Chunk.CHUNK_SIZE;
                offset_y = player.getY();
                break;
            case LEFT:
                offset_x = player.getX()-2*Chunk.CHUNK_SIZE;
                offset_y = player.getY();
        }
        // Calculate the sides of the triangle:
        int triangle_a = offset_x - blinky.x;
        int triangle_b = offset_y - blinky.y;
        // Calculate the wanted point:
        target.setX(offset_x + triangle_a);
        target.setY(offset_y + triangle_b);
        return target;
    }

    @Override
    protected ImageResource getGhostImage(CollusionTest.NextDirection direction, int image_index) {
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
