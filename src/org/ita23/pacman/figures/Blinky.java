package org.ita23.pacman.figures;

import org.ita23.pacman.game.CollusionTest;
import org.ita23.pacman.game.CollusionTest.NextDirection;
import org.ita23.pacman.logic.ChunkedMap.Chunk;
import org.ita23.pacman.logic.GameState;
import org.ita23.pacman.logic.Point;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Blinky is the red ghost AI, which will start "out of the box" at the beginning
 *  of the game.
 * @author Lukas Knuth
 * @version 1.0
 */
class Blinky extends Ghost{

    /** Blinky's home-corner in the top-right corner of the field */
    private static final Point HOME_CORNER = new Point(Chunk.CHUNK_SIZE*26, 0);

    /** The direction this ghost is currently going. */
    private CollusionTest.NextDirection currentDirection;
    /** The direction the ghost will go on his next move */
    private CollusionTest.NextDirection nextDirection;

    /** The amount of pixels this ghost moves per repaint */
    private final static int MOVE_PER_PAINT = 2;
    /** Counts the amount of pixels moved since the last direction-change */
    private int pixel_moved_count;

    /** All directions determined to be possible for the next step */
    private final List<NextDirection> possible_directions;

    /**
     * Create a new instance of the red ghost.
     */
    public Blinky(Pacman player){
        super(player);
        currentDirection = NextDirection.RIGHT;
        nextDirection = currentDirection;
        possible_directions = new ArrayList<NextDirection>(4);
    }

    @Override
    protected Point getHomeCorner() {
        return HOME_CORNER;
    }

    @Override
    public void detectCollusion(CollusionTest tester) {
        if (pixel_moved_count % Chunk.CHUNK_SIZE != 0 && !isCaged()) return;
        // Check if we got pacman:
        if (gotPlayer(x, y)){
            GameState.INSTANCE.removeLive();
            // Reset the rest:
            currentDirection = NextDirection.LEFT;
            nextDirection = currentDirection;
            possible_directions.clear();
            return;
        }
        // Check if we went into the "jumper":
        if (tester.checkCollusion(this.x, this.y, Chunk.JUMPER)){
            if (this.x <= Chunk.CHUNK_SIZE-3){ // Went into the left jumper, so go to the right:
                this.x = Chunk.CHUNK_SIZE * 27;
            } else {
                this.x = Chunk.CHUNK_SIZE;
            }
            return;
        }
        // Check the next possible turns:
        int x_next = 0, y_next = 0;
        switch (currentDirection){
            case UP:
                y_next = this.y-Chunk.CHUNK_SIZE;
                x_next = this.x;
                break;
            case LEFT:
                y_next = this.y;
                x_next = this.x-Chunk.CHUNK_SIZE;
                break;
            case DOWN:
                y_next = this.y+Chunk.CHUNK_SIZE;
                x_next = this.x;
                break;
            case RIGHT:
                y_next = this.y;
                x_next = this.x+Chunk.CHUNK_SIZE;
        }
        // Find the possible directions:
        possible_directions.clear();
        if (!tester.checkNextCollusion(x_next, y_next, Chunk.BLOCK, NextDirection.RIGHT)){
            // Exclude the opposite direction:
            if (currentDirection != NextDirection.LEFT)
                possible_directions.add(NextDirection.RIGHT);
        }
        if (!tester.checkNextCollusion(x_next, y_next, Chunk.BLOCK, NextDirection.DOWN)){
            if (currentDirection != NextDirection.UP)
                possible_directions.add(NextDirection.DOWN);
        }
        if (!tester.checkNextCollusion(x_next, y_next, Chunk.BLOCK, NextDirection.LEFT)){
            if (currentDirection != NextDirection.RIGHT)
                possible_directions.add(NextDirection.LEFT);
        }
        if (!tester.checkNextCollusion(x_next, y_next, Chunk.BLOCK, NextDirection.UP)){
            if (currentDirection != NextDirection.DOWN)
                possible_directions.add(NextDirection.UP);
        }
        // Check for pacman:
        int shortest = Integer.MAX_VALUE;
        int current = 0;
        for (NextDirection next : possible_directions){
            // X - and Y from the "next"-point ++ direction!
            switch (next){
                case UP:
                    current = measureDistance(x_next, y_next-Chunk.CHUNK_SIZE);
                    break;
                case LEFT:
                    current = measureDistance(x_next-Chunk.CHUNK_SIZE, y_next);
                    break;
                case DOWN:
                    current = measureDistance(x_next, y_next+Chunk.CHUNK_SIZE);
                    break;
                case RIGHT:
                    current = measureDistance(x_next+Chunk.CHUNK_SIZE, y_next);
            }
            if (current <= shortest){
                nextDirection = next;
                shortest = current;
            }
        }
    }

    @Override
    public void move() {
        if (isCaged()){
            // TODO Move up and down in the cage
            return;
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
        // Check if we can change directions:
        if (pixel_moved_count % Chunk.CHUNK_SIZE == 0){
            currentDirection = nextDirection;
            pixel_moved_count = 0;
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(this.x-3, this.y-3, 28, 28);
    }
}
