package org.ita23.pacman.figures;

import org.ita23.pacman.game.CollusionEvent;
import org.ita23.pacman.game.CollusionTest;
import org.ita23.pacman.game.InputEvent;
import org.ita23.pacman.game.RenderEvent;
import org.ita23.pacman.logic.Chunk;
import org.ita23.pacman.logic.ChunkedMap;
import org.ita23.pacman.logic.Point;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * The main-character of this game.
 * @author Lukas Knuth
 * @author Fabain Bottler
 * @version 1.0
 */
public class Pacman implements RenderEvent, InputEvent, CollusionEvent {

    /** The count of degrees needed to consider the moth "fully opened" */
    private final static int MOUTH_MAX = 75;
    /** The count of degrees needed to consider the moth "fully closed" */
    private final static int MOUTH_MIN = 0;
    /** The pixels-per-repaint that pacman moves */
    private final static int MOVE_PER_PAINT = 2; // TODO Slower when not eating!
    /** The speed indicating how fast the mouth moves. The higher, the faster! */
    private final static int MOUTH_SPEED = 6;
    
    private final static int ZINDEX = 1;
    
    /** The color of pacmans body */
    private static final Color BODY_COLOR = new Color(255, 255, 87);

    /** The current X-coordinate */
    private int x;
    /** The current Y-coordinate */
    private int y;

    /** Counts the amount of pixels moved since the last direction-change */
    private int pixel_moved_count;

    /** The current degrees of the mouth */
    private int mouth_degrees;
    /** Specifies if the mouth is closing or opening */
    private boolean mouth_closing;

    /** Possible directions that pacman can look */
    private enum FacingDirection{
        UP(0), DOWN(180), LEFT(90), RIGHT(270);
        
        private final int degrees;
        private FacingDirection(int degrees){
            this.degrees = degrees;
        }
    }
    /** The current direction pacman looks */
    private FacingDirection current_direction;
    /** The direction pacman should move next possible turn */
    private FacingDirection next_direction;

    /**
     * Create a new Pacman-figure with an animated mouth.
     */
    public Pacman(Point point){
        mouth_degrees = 0;
        mouth_closing = false;
        current_direction = FacingDirection.LEFT;
        next_direction = current_direction;
        this.x = point.x+3;
        this.y = point.y+3;
    }
    
    public int getZIndex(){
        return ZINDEX;
    }
    
    @Override
    public void render(Graphics g) {
        // Check if direction-change is allowed:
        if (pixel_moved_count % (Chunk.CHUNK_SIZE / 3) == 0){
            // Change direction if needed:
            current_direction = next_direction;
            pixel_moved_count = 0;
        }
        // Move the character:
        switch (current_direction){
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
                break;
        }
        // Draw the "ball"
        g.setColor(BODY_COLOR);
        g.fillOval(this.x, this.y, 28, 28);
        // Draw the mouth:
        g.setColor(ChunkedMap.BACKGROUND_COLOR);
        // Animate the mouth:
        if (mouth_degrees < MOUTH_MAX && !mouth_closing){
            // Mouth is opening.
            g.fillArc(this.x, this.y, 28, 28,
                    calculateMouthSpacer(mouth_degrees)+current_direction.degrees,
                    mouth_degrees
            );
            mouth_degrees += MOUTH_SPEED;
        } else if (mouth_degrees > MOUTH_MIN) {
            // Mouth is closing
            g.fillArc(this.x, this.y, 28, 28,
                    calculateMouthSpacer(mouth_degrees)+current_direction.degrees,
                    mouth_degrees
            );
            mouth_degrees -= MOUTH_SPEED;
            mouth_closing = true;
        } else {
            // Mouth is closed. Open it again!
            mouth_closing = false;
        }
    }

    /**
     * Calculates the space needed to "center" the mouth on the
     *  body during the "open-close" animation.
     * @param current_degrees the number of degrees the mouth is
     *  currently opened.
     * @return the calculated space used to center the mouth.
     * TODO Add pictures or something for better explanation.
     */
    public int calculateMouthSpacer(int current_degrees){
        int element_space = current_degrees + 180;
        int usable_space = 360 - element_space;
        return (usable_space / 2);
    }
    
    @Override
    public void detectCollusion(CollusionTest tester) {
        Point p = new Point(this.x, this.y);
        System.out.println("Collided with something: "+tester.checkAnyCollusion(p));
        System.out.println("Collided with POINT: "+tester.checkCollusion(Chunk.ChunkObject.POINT, p));
    }

    @Override
    public void keyboardInput(KeyEvent event, KeyEventType type) {
        if (event.getKeyCode() == KeyEvent.VK_UP)
            next_direction = FacingDirection.UP;
        else if (event.getKeyCode() == KeyEvent.VK_DOWN)
            next_direction = FacingDirection.DOWN;
        else if (event.getKeyCode() == KeyEvent.VK_LEFT)
            next_direction = FacingDirection.LEFT;
        else if (event.getKeyCode() == KeyEvent.VK_RIGHT)
            next_direction = FacingDirection.RIGHT;
    }
}
