package org.ita23.pacman.figures;

import org.ita23.pacman.game.InputEvent;
import org.ita23.pacman.game.RenderEvent;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * The main-character of this game.
 * @author Lukas Knuth
 * @author Fabain Bottler
 * @version 1.0
 */
public class Pacman implements RenderEvent, InputEvent {

    /** The count of degrees needed to consider the moth "fully opened" */
    private final static int MOUTH_MAX = 90;
    /** The count of degrees needed to consider the moth "fully closed" */
    private final static int MOUTH_MIN = 0;
    
    private final static int ZINDEX = 1;

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

    /**
     * Create a new Pacman-figure with an animated mouth.
     */
    public Pacman(){
        mouth_degrees = 0;
        mouth_closing = false;
        current_direction = FacingDirection.LEFT;

    }
    
    public int getZIndex(){
        return ZINDEX;
    }
    
    @Override
    public void render(Graphics g) {
        // Draw the "ball"
        g.setColor(Color.YELLOW);
        g.fillOval(20, 20, 28, 28);

        // Draw the mouth:
        g.setColor(Color.BLACK);
        // Animate the mouth:
        if (mouth_degrees < MOUTH_MAX && !mouth_closing){
            // Mouth is opening.
            g.fillArc(20, 20, 28, 28,
                    calculateMouthSpacer(mouth_degrees)+current_direction.degrees,
                    mouth_degrees
            );
            mouth_degrees++;
        } else if (mouth_degrees > MOUTH_MIN) {
            // Mouth is closing
            g.fillArc(20, 20, 28, 28,
                    calculateMouthSpacer(mouth_degrees)+current_direction.degrees,
                    mouth_degrees
            );
            mouth_degrees--;
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
    public void keyboardInput(KeyEvent event, KeyEventType type) {
        if (event.getKeyCode() == KeyEvent.VK_UP)
            current_direction = FacingDirection.UP;
        else if (event.getKeyCode() == KeyEvent.VK_DOWN)
            current_direction = FacingDirection.DOWN;
        else if (event.getKeyCode() == KeyEvent.VK_LEFT)
            current_direction = FacingDirection.LEFT;
        else if (event.getKeyCode() == KeyEvent.VK_RIGHT)
            current_direction = FacingDirection.RIGHT;
    }
}
