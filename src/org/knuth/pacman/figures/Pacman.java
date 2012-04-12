package org.knuth.pacman.figures;

import org.knuth.pacman.game.RenderEvent;

import java.awt.*;

/**
 * The main-character of this game.
 * @author Lukas Knuth
 * @version 1.0
 */
public class Pacman implements RenderEvent{

    /** The count of degrees needed to consider the moth "fully opened" */
    private final static int MOUTH_MAX = 90;
    /** The count of degrees needed to consider the moth "fully closed" */
    private final static int MOUTH_MIN = 0;

    /** The current degrees of the mouth */
    private int mouth_degrees;
    /** Specifies if the mouth is closing or opening */
    private boolean mouth_closing;

    /**
     * Create a new Pacman-figure with an animated mouth.
     */
    public Pacman(){
        mouth_degrees = 0;
        mouth_closing = false;
    }
    
    @Override
    public Graphics render(Graphics g) {
        // Draw the "ball"
        g.setColor(Color.YELLOW);
        g.fillOval(20, 20, 40, 40);
        // Draw the mouth:
        g.setColor(Color.BLACK);
        // Animate the mouth:
        if (mouth_degrees < MOUTH_MAX && !mouth_closing){
            // Mouth is opening.
            g.fillArc(20, 20, 40, 40, 130, mouth_degrees);
            mouth_degrees++;
        } else if (mouth_degrees > MOUTH_MIN) {
            // Mouth is closing
            g.fillArc(20, 20, 40, 40, 130, mouth_degrees);
            mouth_degrees--;
            mouth_closing = true;
        } else {
            // Mouth is closed. Open it again!
            mouth_closing = false;
        }
        return g;
    }
}
