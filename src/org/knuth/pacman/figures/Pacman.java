package org.knuth.pacman.figures;

import org.knuth.pacman.game.RenderEvent;

import java.awt.*;

/**
 * The main-character of this game.
 * @author Lukas Knuth
 * @version 1.0
 */
public class Pacman implements RenderEvent{

    private MouthStates current_MouthState;
    private boolean mouth_opening;
    
    public Pacman(){
        this.current_MouthState = MouthStates.CLOSED;
        mouth_opening = true;
    }
    
    private enum MouthStates{
        OPEN(90), CLOSING(65), HALF_CLOSED(45), ALMOST_CLOSED(22), CLOSED(2);
        
        public final int degrees;
        private MouthStates(int degrees){
            this.degrees = degrees;
        }
    }
    
    @Override
    public Graphics render(Graphics g) {
        // Draw the "ball"
        g.setColor(Color.YELLOW);
        g.fillOval(20, 20, 40, 40);
        // Draw the mouth:
        g.setColor(Color.BLACK);
        switch (current_MouthState){
            case OPEN:
                g.fillArc(20, 20, 40, 40, 130, MouthStates.OPEN.degrees);
                mouth_opening = false;
                current_MouthState = MouthStates.CLOSING;
                break;
            case CLOSING:
                g.fillArc(20, 20, 40, 40, 130, MouthStates.CLOSING.degrees);
                if (mouth_opening) current_MouthState = MouthStates.OPEN;
                else current_MouthState = MouthStates.HALF_CLOSED;
                break;
            case HALF_CLOSED:
                g.fillArc(20, 20, 40, 40, 130, MouthStates.HALF_CLOSED.degrees);
                if (mouth_opening) current_MouthState = MouthStates.CLOSING;
                else current_MouthState = MouthStates.ALMOST_CLOSED;
                break;
            case ALMOST_CLOSED:
                g.fillArc(20, 20, 40, 40, 130, MouthStates.ALMOST_CLOSED.degrees);
                if (mouth_opening) current_MouthState = MouthStates.HALF_CLOSED;
                else current_MouthState = MouthStates.CLOSED;
                break;
            case CLOSED:
                g.fillArc(20, 20, 40, 40, 130, MouthStates.CLOSED.degrees);
                mouth_opening = true;
                current_MouthState = MouthStates.ALMOST_CLOSED;
                break;
        }
        return g;
    }
}
