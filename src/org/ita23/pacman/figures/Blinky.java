package org.ita23.pacman.figures;

import org.ita23.pacman.game.CollusionTest;
import org.ita23.pacman.logic.Chunk;

import java.awt.*;

/**
 * Blinky is the red ghost AI, which will start "out of the box" at the beginning
 *  of the game.
 * @author Lukas Knuth
 * @version 1.0
 */
public class Blinky extends Ghost{

    /** The X-coordinate */
    private int x;
    /** The Y-coordinate */
    private int y;

    /** The direction this ghost is currently going. */
    private CollusionTest.NextDirection currentDirection;
    /** The direction the ghost will go on his next move on the X-axis */
    private CollusionTest.NextDirection nextDirection_x;
    /** The direction the ghost will go on his next move on the X-axis */
    private CollusionTest.NextDirection nextDirection_y;
    
    private int dir_count;

    /** The amount of pixels this ghost moves per repaint */
    private final static int MOVE_PER_PAINT = 2;
    /** Counts the amount of pixels moved since the last direction-change */
    private int pixel_moved_count;

    /**
     * Create a new instance of the red ghost.
     */
    public Blinky(Pacman player){
        super(player);
        this.x = player.getX()+(3*Chunk.CHUNK_SIZE); // TODO Add "cage" as a point!
        this.y = player.getY()+(5*Chunk.CHUNK_SIZE);
        currentDirection = CollusionTest.NextDirection.UP;
        nextDirection_x = null;
        nextDirection_y = null;
        dir_count = 0;
    }

    @Override
    public void think() {
        // Check if we can change directions:
        if (pixel_moved_count % (Chunk.CHUNK_SIZE / Chunk.OBJECTS_PER_CHUNK_LINE) == 0){
            if (nextDirection_x != null && dir_count % 2 == 0){
                currentDirection = nextDirection_x;
                nextDirection_x = null;
                dir_count = 1;
            } else if (nextDirection_y != null){
                currentDirection = nextDirection_y;
                nextDirection_y = null;
                dir_count++;
            }
            pixel_moved_count = 0;
        }
        // Move the character:
        switch (currentDirection){
            case UP:
                this.y -= MOVE_PER_PAINT;
                pixel_moved_count += MOVE_PER_PAINT;
                break;
            case RIGHT:
                this.x += MOVE_PER_PAINT;
                pixel_moved_count += MOVE_PER_PAINT;
                break;
            case DOWN:
                this.y += MOVE_PER_PAINT;
                pixel_moved_count += MOVE_PER_PAINT;
                break;
            case LEFT:
                this.x -= MOVE_PER_PAINT;
                pixel_moved_count += MOVE_PER_PAINT;
        }
        // Check where player is:
        if (getPlayerX() < x) // Go left:
            nextDirection_x = CollusionTest.NextDirection.LEFT;
        else if (getPlayerX() > x) // Go right:
            nextDirection_x = CollusionTest.NextDirection.RIGHT;
        else
            nextDirection_x = null;
        if (getPlayerY() < y) // Go up:
            nextDirection_y = CollusionTest.NextDirection.UP;
        else if (getPlayerY() > y) // Go down:
            nextDirection_y = CollusionTest.NextDirection.DOWN;
        else 
            nextDirection_y = null;
    }

    @Override
    public void detectCollusion(CollusionTest tester) {
        if (pixel_moved_count % (Chunk.CHUNK_SIZE / Chunk.OBJECTS_PER_CHUNK_LINE) != 0) return;
        // Check if we got pacman:
        if (gotPlayer(x, y)){
            // TODO Party Hard!
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(this.x, this.y, 28, 28);
    }
}