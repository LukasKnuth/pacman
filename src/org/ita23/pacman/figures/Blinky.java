package org.ita23.pacman.figures;

import org.ita23.pacman.logic.ChunkedMap.Chunk;
import org.ita23.pacman.logic.Point;

import java.awt.*;

/**
 * Blinky is the red ghost AI, which will start "out of the box" at the beginning
 *  of the game.
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

    /** The variable used to switch between the two possible images of a direction */
    private int image_count;
    /** This counter is used to make the ghost blink at the end of frightened mode */
    private int blink_count;

    /**
     * Create a new instance of the red ghost.
     */
    public Blinky(Pacman player){
        super(player);
        target = new Point(0, 0);
        image_count = 0;
        blink_count = 0;
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
    protected Point getHomeCorner() {
        return HOME_CORNER;
    }

    @Override
    public void render(Graphics g) {
        // Calculate the current image-index
        int image_index = 0;
        image_count++;
        if (image_count < 4) image_index = 0;
        else if (image_count < 8) image_index = 1;
        else image_count = 0;
        // Paint:
        if (isEatable()){
            g.drawImage(frightened[image_index], this.x, this.y, null);
            // TODO Implement the blinking at the end of the period.
        } else if (isEaten()){
            switch (getNextDirection()){
                case UP:
                    g.drawImage(dead_up, this.x, this.y, null);
                    break;
                case DOWN:
                    g.drawImage(dead_down, this.x, this.y, null);
                    break;
                case LEFT:
                    g.drawImage(dead_left, this.x, this.y, null);
                    break;
                case RIGHT:
                    g.drawImage(dead_right, this.x, this.y, null);
            }
        } else {
            switch (getNextDirection()){
                case UP:
                    g.drawImage(ghost_up[image_index], this.x, this.y, null);
                    break;
                case DOWN:
                    g.drawImage(ghost_down[image_index], this.x, this.y, null);
                    break;
                case LEFT:
                    g.drawImage(ghost_left[image_index], this.x, this.y, null);
                    break;
                case RIGHT:
                    g.drawImage(ghost_right[image_index], this.x, this.y, null);
            }
        }
    }
}
