package org.ita23.pacman.figures;

import org.ita23.pacman.res.ImageResource;
import org.ita23.pacman.game.CollusionTest;
import org.ita23.pacman.logic.ChunkedMap.Chunk;
import org.ita23.pacman.logic.Point;

/**
 * Pinky is the pink ghost AI, which will start in the middle of the cage.</p>
 * For a detailed description of his behaviour, check out
 * <a href="http://home.comcast.net/~jpittman2/pacman/pacmandossier.html#CH4_Pinky">
 *   Chapter 4 of "The Pac-Man Dossier" - Pinky
 * </a>
 * @author Lukas Knuth
 * @version 1.0
 */
public class Pinky extends Ghost {

    /** Pinky's home-corner in the top-right corner of the field */
    private static final Point HOME_CORNER = new Point(Chunk.CHUNK_SIZE, 0);

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
     * Create a new instance of the pink ghost.
     */
    protected Pinky(Pacman player) {
        super(player);
        target = new Point(0, 0);
        // Load the images:
        ghost_down = new ImageResource[]{
                ImageResource.PINKY_DOWN_1,
                ImageResource.PINKY_DOWN_2
        };
        ghost_left = new ImageResource[]{
                ImageResource.PINKY_LEFT_1,
                ImageResource.PINKY_LEFT_2
        };
        ghost_up = new ImageResource[]{
                ImageResource.PINKY_UP_1,
                ImageResource.PINKY_UP_2
        };
        ghost_right = new ImageResource[]{
                ImageResource.PINKY_RIGHT_1,
                ImageResource.PINKY_RIGHT_2
        };
    }

    @Override
    protected Point getTargetChunk(Pacman player) {
        switch (player.getCurrentDirection()){
            case UP:
                target.setX(player.getX());
                target.setY(player.getY()-4*Chunk.CHUNK_SIZE);
                return target;
            case DOWN:
                target.setX(player.getX());
                target.setY(player.getY()+4*Chunk.CHUNK_SIZE);
                return target;
            case RIGHT:
                target.setX(player.getX()+4*Chunk.CHUNK_SIZE);
                target.setY(player.getY());
                return target;
            case LEFT:
                target.setX(player.getX()-4*Chunk.CHUNK_SIZE);
                target.setY(player.getY());
                return target;
            default:
                throw new IllegalArgumentException("The direction can't be "+player.getCurrentDirection());
        }
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
